package edu.hust.marketflow.producer.datagenerator;

import edu.hust.marketflow.model.UnifiedDataModel;
import edu.hust.marketflow.model.olistsrc.*;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class OlistDataWrapper implements DataWrapper {
    private static final String SOURCE_NAME = "Olist";

    private static final String CUSTOMERS_PATH = "datasets/olist/olist_customers_dataset.csv";
    private static final String GEOLOCATION_PATH = "datasets/olist/olist_geolocation_dataset.csv";
    private static final String ORDER_ITEMS_PATH = "datasets/olist/olist_order_items_dataset.csv";
    private static final String ORDER_PAYMENTS_PATH = "datasets/olist/olist_order_payments_dataset.csv";
    private static final String ORDER_REVIEWS_PATH = "datasets/olist/olist_order_reviews_dataset.csv";
    private static final String ORDERS_PATH = "datasets/olist/olist_orders_dataset.csv";
    private static final String PRODUCTS_PATH = "datasets/olist/olist_products_dataset.csv";
    private static final String SELLERS_PATH = "datasets/olist/olist_sellers_dataset.csv";
    private static final String PRODUCT_NAME_TRANSLATION_PATH = "datasets/olist/product_category_name_translation.csv";

    // LRU caches to speed up repeated lookups
    private final Map<String, OlistCustomer> customerCache = createLruCache(5_000);
    private final Map<String, OlistGeolocation> geolocationCache = createLruCache(5_000);
    private final Map<String, OlistOrder> orderCache = createLruCache(5_000);
    private final Map<String, OlistOrderItem> orderItemCache = createLruCache(5_000);
    private final Map<String, OlistOrderPayment> orderPaymentCache = createLruCache(5_000);
    private final Map<String, OlistOrderReview> orderReviewCache = createLruCache(5_000);
    private final Map<String, OlistProduct> productCache = createLruCache(5_000);
    private final Map<String, OlistProductCategoryNameTranslation> productNameTranslationCache = createLruCache(5_000);
    private final Map<String, OlistSeller> sellerCache = createLruCache(5_000);

    private BufferedReader orderReader;

    public OlistDataWrapper() {
        try {
            orderReader = new BufferedReader(new FileReader(ORDERS_PATH));
            orderReader.readLine(); // skip header
        } catch (IOException e) {
            throw new RuntimeException("Failed to load orders file", e);
        }
    }

    /** Creates an LRU cache with given capacity **/
    private static <K, V> Map<K, V> createLruCache(final int capacity) {
        return new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
            }
        };
    }

    @Override
    @Nullable
    public UnifiedDataModel nextData() {
        try {
            String line = orderReader.readLine();
            if (line == null) {
                // loop again (simulate continuous stream)
                orderReader.close();
                orderReader = new BufferedReader(new FileReader(ORDERS_PATH));
                orderReader.readLine();
                line = orderReader.readLine();
                if (line == null) throw new RuntimeException("Orders file empty");
            }

            String[] p = splitCsv(line);
            if (p.length < OlistOrder.getFieldCount()) {
                return null;
            }

            OlistOrder order = OlistOrder.fromArray(p);
            if (order == null) return null;

            // find related entities
            OlistOrderItem orderItem = findOrderItemByOrderId(order.getOrderId());
            OlistProduct product = orderItem == null ? null : findProductById(orderItem.getProductId());
            OlistCustomer customer = findCustomerById(order.getCustomerId());
            OlistOrderPayment payment = findPaymentByOrderId(order.getOrderId());
            OlistOrderReview review = findReviewByOrderId(order.getOrderId());

            return getUnifiedDataModel(order, orderItem, product, customer, payment, review);

        } catch (IOException e) {
            throw new RuntimeException("Error reading order", e);
        }
    }

    /** Simple CSV splitter that keeps things robust for missing trailing fields **/
    private String[] splitCsv(String line) {
        // CSVs in datasets are simple and use commas without embedded commas in fields
        return line.split(",",
                -1); // keep empty trailing fields
    }

    /** Lazy product lookup from CSV with LRU cache **/
    private OlistProduct findProductById(String productId) {
        if (productId == null) return null;

        OlistProduct cached = productCache.get(productId);
        if (cached != null) return cached;

        try (BufferedReader br = new BufferedReader(new FileReader(PRODUCTS_PATH))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = splitCsv(line);
                if (p.length < OlistProduct.getFieldCount()) continue;
                if (p[0].equals(productId)) {
                    OlistProduct product = OlistProduct.fromArray(p);
                    productCache.put(productId, product);
                    return product;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Lazy customer lookup from CSV with LRU cache **/
    private OlistCustomer findCustomerById(String customerId) {
        if (customerId == null) return null;

        OlistCustomer cached = customerCache.get(customerId);
        if (cached != null) return cached;

        try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMERS_PATH))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = splitCsv(line);
                if (p.length < OlistCustomer.getFieldCount()) continue;
                if (p[0].equals(customerId)) {
                    OlistCustomer customer = OlistCustomer.fromArray(p);
                    customerCache.put(customerId, customer);
                    return customer;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Lazy order item lookup (returns first item for order) **/
    private OlistOrderItem findOrderItemByOrderId(String orderId) {
        if (orderId == null) return null;

        OlistOrderItem cached = orderItemCache.get(orderId);
        if (cached != null) return cached;

        try (BufferedReader br = new BufferedReader(new FileReader(ORDER_ITEMS_PATH))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = splitCsv(line);
                if (p.length < OlistOrderItem.getFieldCount()) continue;
                if (p[0].equals(orderId)) {
                    OlistOrderItem item = OlistOrderItem.fromArray(p);
                    orderItemCache.put(orderId, item);
                    return item;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Lazy payment lookup by orderId **/
    private OlistOrderPayment findPaymentByOrderId(String orderId) {
        if (orderId == null) return null;

        OlistOrderPayment cached = orderPaymentCache.get(orderId);
        if (cached != null) return cached;

        try (BufferedReader br = new BufferedReader(new FileReader(ORDER_PAYMENTS_PATH))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = splitCsv(line);
                if (p.length < OlistOrderPayment.getFieldCount()) continue;
                if (p[0].equals(orderId)) {
                    OlistOrderPayment payment = OlistOrderPayment.fromArray(p);
                    orderPaymentCache.put(orderId, payment);
                    return payment;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Lazy review lookup by orderId **/
    private OlistOrderReview findReviewByOrderId(String orderId) {
        if (orderId == null) return null;

        OlistOrderReview cached = orderReviewCache.get(orderId);
        if (cached != null) return cached;

        try (BufferedReader br = new BufferedReader(new FileReader(ORDER_REVIEWS_PATH))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = splitCsv(line);
                if (p.length < OlistOrderReview.getFieldCount()) continue;
                if (p[1].equals(orderId)) {
                    OlistOrderReview review = OlistOrderReview.fromArray(p);
                    orderReviewCache.put(orderId, review);
                    return review;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private UnifiedDataModel getUnifiedDataModel(OlistOrder order,
                                                 OlistOrderItem item,
                                                 OlistProduct product,
                                                 OlistCustomer customer,
                                                 OlistOrderPayment payment,
                                                 OlistOrderReview review) {
        UnifiedDataModel unified = new UnifiedDataModel();

        unified.sourceSystem = (SOURCE_NAME);
        unified.timestamp = (order.getOrderPurchaseTimestamp());

        unified.orderStatus = (order.getOrderStatus());
        unified.shippingMethod = (item == null ? null : "Standard");
        unified.paymentMethod = (payment == null ? null : payment.getPaymentType());

        unified.totalAmount = (safeDouble(payment == null ? item == null ? "0" : item.getPrice() : payment.getPaymentValue()));
        unified.quantity = (1);
        unified.price = (safeDouble(item == null ? "0" : item.getPrice()));
        // TODO: fetch exchange rate if needed
        // BRL to USD
        unified.price = unified.price * 0.19;
        unified.totalAmount = unified.totalAmount * 0.19;

        if (product != null) {
            unified.productType = (product.getProductCategoryName());
            unified.brand = (null);
            unified.category = (product.getProductCategoryName());
            unified.productName = (product.getProductCategoryName());
            unified.productId = (product.getProductId());
        }

        if (customer != null) {
            unified.age = (0);
            unified.region = (customer.getCustomerState() != null ? customer.getCustomerState() : customer.getCustomerZipCodePrefix());
            unified.customerSegment = ("Unknown");
            unified.customerId = (customer.getCustomerId());
        }

        unified.rating = (safeDouble(review == null ? "0" : review.getReviewScore()));

        return unified;
    }

    private double safeDouble(String s) {
        try {
            return s == null ? 0.0 : Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}

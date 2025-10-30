package edu.hust.marketflow.producer.datagenerator;

import edu.hust.marketflow.model.hnmsrc.*;
import edu.hust.marketflow.model.UnifiedDataModel;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;

public class HnMDataWrapper implements DataWrapper {
    private static final String SOURCE_NAME = "H&M";

    private static final String ARTICLES_PATH = "datasets/hm_fashion/articles.csv";
    private static final String CUSTOMERS_PATH = "datasets/hm_fashion/customers.csv";
    private static final String TRANSACTIONS_PATH = "datasets/hm_fashion/transactions.csv";

    // LRU caches to speed up repeated lookups
    private final Map<String, HnMArticle> articleCache = createLruCache(10_000);
    private final Map<String, HnMCustomer> customerCache = createLruCache(10_000);

    private BufferedReader transactionReader;

    public HnMDataWrapper() {
        try {
            transactionReader = new BufferedReader(new FileReader(TRANSACTIONS_PATH));
            transactionReader.readLine(); // skip header
        } catch (IOException e) {
            throw new RuntimeException("Failed to load transactions file", e);
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
            String line = transactionReader.readLine();
            if (line == null) {
                // loop again (simulate continuous stream)
                transactionReader.close();
                transactionReader = new BufferedReader(new FileReader(TRANSACTIONS_PATH));
                transactionReader.readLine();
                line = transactionReader.readLine();
                if (line == null) throw new RuntimeException("Transaction file empty");
            }

            String[] p = line.split(",");
            if (p.length < HnMTransaction.getFieldCount()) {
                return null;
            }

            HnMTransaction tx = HnMTransaction.fromArray(p);
            if (tx == null) return null;

            HnMArticle article = findArticleById(tx.getArticleId());
            HnMCustomer customer = findCustomerById(tx.getCustomerId());

            return getUnifiedDataModel(tx, article, customer);

        } catch (IOException e) {
            throw new RuntimeException("Error reading transaction", e);
        }
    }

    /** Lazy article lookup from CSV with LRU cache **/
    private HnMArticle findArticleById(String articleId) {
        if (articleId == null) return null;

        HnMArticle cached = articleCache.get(articleId);
        if (cached != null) return cached;

        try (BufferedReader br = new BufferedReader(new FileReader(ARTICLES_PATH))) {
            String header = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length < HnMArticle.getFieldCount()) continue;
                if (p[0].equals(articleId)) {
                    HnMArticle article = HnMArticle.fromArray(p);
                    articleCache.put(articleId, article);
                    return article;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Lazy customer lookup from CSV with LRU cache **/
    private HnMCustomer findCustomerById(String customerId) {
        if (customerId == null) return null;

        HnMCustomer cached = customerCache.get(customerId);
        if (cached != null) return cached;

        try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMERS_PATH))) {
            String header = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length < HnMCustomer.getFieldCount()) continue;
                if (p[0].equals(customerId)) {
                    HnMCustomer customer = HnMCustomer.fromArray(p);
                    customerCache.put(customerId, customer);
                    return customer;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private UnifiedDataModel getUnifiedDataModel(HnMTransaction tx, HnMArticle article, HnMCustomer customer) {
        UnifiedDataModel unified = new UnifiedDataModel();

        unified.sourceSystem = (SOURCE_NAME);
        unified.timestamp = (tx.gettDat());

        unified.orderStatus = ("Completed");
        unified.shippingMethod = ("Online");
        unified.paymentMethod = ("Credit Card");

        // TODO: fetch exchange rate if needed
        unified.totalAmount = (tx.getPrice());
        unified.quantity = (1);
        unified.price = (tx.getPrice());

        // SEK -> USD
        unified.totalAmount = unified.totalAmount * 1000 * 0.11;
        unified.price = unified.price * 1000 * 0.11;

        if (article != null) {
            unified.productType = (article.getProductTypeName());
            unified.brand = (article.getDepartmentName());
            unified.category = (article.getProductGroupName());
            unified.productName = (article.getProdName());
            unified.productId = (article.getArticleId());
        }

        if (customer != null) {
            unified.age = (parseIntSafe(customer.getAge()));
            unified.region = (customer.getPostalCode());
            unified.customerSegment = (deriveCustomerSegment(customer));
            unified.customerId = (customer.getCustomerId());
        }

        return unified;
    }

    private int parseIntSafe(String s) {
        try {
            return s == null ? 0 : Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String deriveCustomerSegment(HnMCustomer customer) {
        if (customer == null) return "Unknown";

        String fn = safeLower(customer.getFn());
        String club = safeUpper(customer.getClubMemberStatus());
        String freq = safeLower(customer.getFashionNewsFrequency());

        boolean isFrequent = fn.equals("1") || fn.equals("1.0");
        boolean isActive = club.equals("ACTIVE");
        boolean isPreCreate = club.equals("PRE-CREATE");
        boolean readsFashionNews = freq.equals("regularly");

        if (isActive && isFrequent && readsFashionNews) return "Loyal VIP Member";
        if (isActive && readsFashionNews) return "Engaged Member";
        if (isActive && isFrequent) return "Active Frequent Buyer";
        if (isActive) return "Active Member";

        if (isPreCreate && readsFashionNews) return "New Interested Member";
        if (isPreCreate) return "New Member";

        if (isFrequent && readsFashionNews) return "Fashion Enthusiast";
        if (isFrequent) return "Frequent Buyer";
        if (readsFashionNews) return "Fashion Reader";

        return "Non-Member";
    }

    private String safeLower(String s) {
        return s == null ? "" : s.trim().toLowerCase();
    }

    private String safeUpper(String s) {
        return s == null ? "" : s.trim().toUpperCase();
    }
}

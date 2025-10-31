package edu.hust.marketflow.producer.datagenerator;

import edu.hust.marketflow.model.UnifiedDataModel;
import edu.hust.marketflow.model.olistsrc.*;
import edu.hust.marketflow.model.retailsrc.RetailModel;
import edu.hust.marketflow.utils.DataSourceMapper;
import edu.hust.marketflow.utils.TypeConvertHelper;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RetailDataWrapper implements DataWrapper {
    private static final String SOURCE_NAME = "Retail";

    private static final String RETAIL_DATA_PATH = "datasets/retail/retail_data_1.csv";

    private BufferedReader orderReader;

    public RetailDataWrapper() {
        try {
            orderReader = new BufferedReader(new FileReader(RETAIL_DATA_PATH));
            orderReader.readLine(); // skip header
        } catch (IOException e) {
            throw new RuntimeException("Failed to load orders file", e);
        }
    }

    @Override
    @Nullable
    public UnifiedDataModel nextData() {
        try {
            String line = orderReader.readLine();
            if (line == null) {
                // loop again (simulate continuous stream)
                orderReader.close();
                orderReader = new BufferedReader(new FileReader(RETAIL_DATA_PATH));
                orderReader.readLine();
                line = orderReader.readLine();
                if (line == null) throw new RuntimeException("Orders file empty");
            }

            String[] p = splitCsv(line);
            if (p.length < DataSourceMapper.getFieldCount(OlistOrder.class)) {
                return null;
            }

            RetailModel order = DataSourceMapper.fromArray(p, RetailModel.class);

            return getUnifiedDataModel(order);

        } catch (IOException e) {
            throw new RuntimeException("Error reading order", e);
        }
    }

    private String[] splitCsv(String line) {
        return line.split(",", -1);
    }

    public static UnifiedDataModel getUnifiedDataModel(RetailModel order) {
        if (order == null) return null;

        UnifiedDataModel unified = new UnifiedDataModel();

        // --- Source System ---
        unified.sourceSystem = (SOURCE_NAME);

        // --- Customer Info ---
        unified.customerId = (order.getCustomerId());
        unified.customerName = (order.getName());
        unified.customerSegment = (order.getCustomerSegment() != null ? order.getCustomerSegment() : "Unknown");
        unified.gender = (order.getGender());
        unified.region = (order.getState() != null ? order.getState() : order.getCountry());

        unified.age = (TypeConvertHelper.safeInt(order.getAge()));
        unified.income = (TypeConvertHelper.safeDouble(order.getIncome()));

        // --- Product Info ---
        unified.productId = (order.getProducts());
        unified.productName = (order.getProducts() != null ? order.getProducts() : order.getProductType());
        unified.category = (order.getProductCategory());
        unified.brand = (order.getProductBrand());
        unified.productType = (order.getProductType());

        // --- Transaction Info ---
        unified.price = (TypeConvertHelper.safeDouble(order.getAmount()));
        unified.quantity = (1);  // assuming each record = one purchase
        unified.totalAmount = (TypeConvertHelper.safeDouble(order.getTotalAmount()));
        unified.paymentMethod = (order.getPaymentMethod());
        unified.shippingMethod = (order.getShippingMethod() != null ? order.getShippingMethod() : "Standard");
        unified.orderStatus = (order.getOrderStatus());

        unified.rating = (TypeConvertHelper.safeDouble(order.getRatings()));

        // --- Timestamp ---
        // Combine date + time if both exist
        unified.timestamp = ((order.getDate() != null && order.getTime() != null)
                ? order.getDate() + " " + order.getTime()
                : order.getDate());

        return unified;
    }

}

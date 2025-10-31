package edu.hust.marketflow.model;

import java.io.Serializable;

/**
 * UnifiedDataModel represents a harmonized data schema
 * for all transaction-like datasets (H&M, Olist, Retail, etc.).
 * It ensures consistency across diverse sources for downstream analytics.
 */
public class UnifiedDataModel implements Serializable {
    public String timestamp;
    public String sourceSystem; // e.g. "H&M", "Olist", "Retail"

    public String customerId;
    public String customerName;
    public String customerSegment;
    public String region;
    public String gender;
    public int age;
    public double income;

    public String productId;
    public String productName;
    public String category;
    public String brand;
    public String productType;

    public double price;
    public int quantity;
    public double totalAmount;

    public String paymentMethod;
    public String shippingMethod;
    public String orderStatus;
    public double rating;

    @Override
    public String toString() {
        return "UnifiedDataModel{" +
                "timestamp='" + timestamp + '\'' +
                ", sourceSystem='" + sourceSystem + '\'' +
                ", customerId='" + customerId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerSegment='" + customerSegment + '\'' +
                ", region='" + region + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", income=" + income +
                ", productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", productType='" + productType + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", totalAmount=" + totalAmount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", shippingMethod='" + shippingMethod + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", rating=" + rating +
                '}';
    }
}

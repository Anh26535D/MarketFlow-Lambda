package edu.hust.marketflow.model.serving;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import edu.hust.marketflow.ConfigLoader;

import java.io.Serializable;

/**
 * Represents aggregated purchase revenue data for each hour.
 * Used in the batch layer for analytical queries and trend analysis.
 */
@Table(keyspace = ConfigLoader.CASSANDRA_KEYSPACE, name = "hourly_purchase_revenue")
public class HourlyPurchaseRevenue implements Serializable {

    @PartitionKey
    @Column(name = "region")
    private String region;

    @ClusteringColumn(0)
    @Column(name = "hour_window")
    private String hourWindow; // e.g., "2025-11-02T15:00:00Z"

    @ClusteringColumn(1)
    @Column(name = "category")
    private String category;

    @Column(name = "total_revenue")
    private double totalRevenue;

    @Column(name = "total_quantity")
    private long totalQuantity;

    @Column(name = "total_orders")
    private long totalOrders;

    @Column(name = "avg_order_value")
    private double avgOrderValue;

    @Column(name = "source_system")
    private String sourceSystem;

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getHourWindow() { return hourWindow; }
    public void setHourWindow(String hourWindow) { this.hourWindow = hourWindow; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public long getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(long totalQuantity) { this.totalQuantity = totalQuantity; }

    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }

    public double getAvgOrderValue() { return avgOrderValue; }
    public void setAvgOrderValue(double avgOrderValue) { this.avgOrderValue = avgOrderValue; }

    public String getSourceSystem() { return sourceSystem; }
    public void setSourceSystem(String sourceSystem) { this.sourceSystem = sourceSystem; }

    @Override
    public String toString() {
        return "HourlyPurchaseRevenue{" +
                "region='" + region + '\'' +
                ", hourWindow='" + hourWindow + '\'' +
                ", category='" + category + '\'' +
                ", totalRevenue=" + totalRevenue +
                ", totalQuantity=" + totalQuantity +
                ", totalOrders=" + totalOrders +
                ", avgOrderValue=" + avgOrderValue +
                ", sourceSystem='" + sourceSystem + '\'' +
                '}';
    }
}

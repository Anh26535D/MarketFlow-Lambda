package edu.hust.marketflow.batch;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import edu.hust.marketflow.ConfigLoader;
import edu.hust.marketflow.model.UnifiedDataModel;
import edu.hust.marketflow.model.serving.CustomerPurchaseHistory;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Row;

import java.text.SimpleDateFormat;
import java.util.*;

public class HdfsToCustomerPurchaseHistory {

    public static void main(String[] args) {
        // --- Load configurations ---
        String cassandraHost = ConfigLoader.getOrThrow(ConfigLoader.CASSANDRA_HOST_KEY);
        String cassandraPort = ConfigLoader.getOrThrow(ConfigLoader.CASSANDRA_PORT_KEY);
        String cassandraKeyspace = ConfigLoader.getOrThrow(ConfigLoader.CASSANDRA_KEYSPACE);
        String cassandraTable = "customer_purchase_history"; // TODO: Make configurable if needed
        String hdfsPath = ConfigLoader.getOrThrow(ConfigLoader.HDFS_DATA_DIR);

        // --- Initialize Spark ---
        SparkSession spark = SparkSession.builder()
                .appName("HdfsToCassandra")
                .master("spark://spark-master:7077")
                .config("spark.cassandra.connection.host", cassandraHost)
                .config("spark.cassandra.connection.port", cassandraPort)
                .getOrCreate();

        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());
        spark.sparkContext().setLogLevel("WARN");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        JavaRDD<Row> rows = spark.read().parquet(hdfsPath).javaRDD();

        // --- Map each row into UnifiedDataModel ---
        JavaRDD<UnifiedDataModel> unifiedRDD = rows.map(row -> {
            UnifiedDataModel model = new UnifiedDataModel();

            model.customerId = row.getAs("customerId");
            model.customerName = row.getAs("customerName");
            model.customerSegment = row.getAs("customerSegment");
            model.region = row.getAs("region");
            model.gender = row.getAs("gender");
            model.age = row.getAs("age") != null ? ((Number) row.getAs("age")).intValue() : 0;
            model.income = row.getAs("income") != null ? ((Number) row.getAs("income")).doubleValue() : 0.0;

            model.productId = row.getAs("productId");
            model.productName = row.getAs("productName");
            model.category = row.getAs("category");
            model.brand = row.getAs("brand");
            model.productType = row.getAs("productType");

            model.price = row.getAs("price") != null ? ((Number) row.getAs("price")).doubleValue() : 0.0;
            model.quantity = row.getAs("quantity") != null ? ((Number) row.getAs("quantity")).intValue() : 0;
            model.totalAmount = row.getAs("totalAmount") != null ? ((Number) row.getAs("totalAmount")).doubleValue() : 0.0;

            model.paymentMethod = row.getAs("paymentMethod");
            model.shippingMethod = row.getAs("shippingMethod");
            model.orderStatus = row.getAs("orderStatus");
            model.rating = row.getAs("rating") != null ? ((Number) row.getAs("rating")).doubleValue() : 0.0;

            model.timestamp = row.getAs("timestamp");
            model.sourceSystem = row.getAs("sourceSystem");

            return model;
        }).filter(Objects::nonNull);

        // --- Transform UnifiedDataModel → CustomerPurchaseHistory ---
        JavaRDD<CustomerPurchaseHistory> customerRDD = unifiedRDD.map(u -> {
            CustomerPurchaseHistory c = new CustomerPurchaseHistory();
            c.setCustomerId(u.customerId);
            c.setTimestamp(u.timestamp);
            c.setProductId(u.productId);
            c.setProductName(u.productName);
            c.setCategory(u.category);
            c.setBrand(u.brand);
            c.setPrice(u.price);
            c.setQuantity(u.quantity);
            c.setTotalAmount(u.totalAmount);
            c.setPaymentMethod(u.paymentMethod);
            c.setRegion(u.region);
            c.setSourceSystem(u.sourceSystem);
            return c;
        });

        // --- Preview sample before writing ---
        List<CustomerPurchaseHistory> sampleCustomerPurchaseHistory = customerRDD.take(5);
        System.out.println("✅ Preview sample data before writing to Cassandra:");
        sampleCustomerPurchaseHistory.forEach(System.out::println);

        // --- Write to Cassandra ---
        CassandraJavaUtil.javaFunctions(customerRDD)
                .writerBuilder(
                        cassandraKeyspace,
                        cassandraTable,
                        CassandraJavaUtil.mapToRow(CustomerPurchaseHistory.class)
                )
                .saveToCassandra();

        System.out.println("✅ Successfully saved data to Cassandra table: "
                + cassandraKeyspace + "." + cassandraTable);

        jsc.close();
        spark.stop();
    }
}

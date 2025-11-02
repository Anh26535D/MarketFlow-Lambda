package edu.hust.marketflow.speed;

import edu.hust.marketflow.ConfigLoader;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.Trigger;
import org.apache.spark.sql.types.*;

import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.apache.spark.sql.functions.*;

public class SaveLiveDataToCassandra {

    public static void main(String[] args) throws Exception {

        // 🔹 Load configuration
        String cassandraHost = ConfigLoader.getOrThrow(ConfigLoader.CASSANDRA_HOST_KEY);
        String cassandraPort = ConfigLoader.getOrThrow(ConfigLoader.CASSANDRA_PORT_KEY);
        String cassandraKeyspace = ConfigLoader.getOrThrow(ConfigLoader.CASSANDRA_KEYSPACE);
        String checkpointPath = ConfigLoader.getOrThrow(ConfigLoader.HDFS_CHECKPOINT_DIR);
        String kafkaBootstrapServers = ConfigLoader.getOrThrow(ConfigLoader.KAFKA_BOOTSTRAP_SERVERS);
        String kafkaTopic = ConfigLoader.getOrThrow(ConfigLoader.KAFKA_TOPIC);

        // 🔹 Initialize Spark
        SparkSession spark = SparkSession.builder()
                .appName("SaveLiveDataToCassandra")
                .master("spark://spark-master:7077")
                .config("spark.cassandra.connection.host", cassandraHost)
                .config("spark.cassandra.connection.port", cassandraPort)
                .getOrCreate();

        spark.sparkContext().setLogLevel("WARN");

        // 🔹 Schema for incoming JSON
        StructType schema = new StructType()
                .add("timestamp", DataTypes.StringType)
                .add("sourceSystem", DataTypes.StringType)
                .add("customerId", DataTypes.StringType)
                .add("customerName", DataTypes.StringType)
                .add("customerSegment", DataTypes.StringType)
                .add("region", DataTypes.StringType)
                .add("gender", DataTypes.StringType)
                .add("age", DataTypes.IntegerType)
                .add("income", DataTypes.DoubleType)
                .add("productId", DataTypes.StringType)
                .add("productName", DataTypes.StringType)
                .add("category", DataTypes.StringType)
                .add("brand", DataTypes.StringType)
                .add("productType", DataTypes.StringType)
                .add("price", DataTypes.DoubleType)
                .add("quantity", DataTypes.IntegerType)
                .add("totalAmount", DataTypes.DoubleType)
                .add("paymentMethod", DataTypes.StringType)
                .add("shippingMethod", DataTypes.StringType)
                .add("orderStatus", DataTypes.StringType)
                .add("rating", DataTypes.DoubleType);

        // 🔹 Read Kafka stream
        Dataset<Row> parsedDf = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", kafkaBootstrapServers)
                .option("subscribe", kafkaTopic)
                .option("startingOffsets", "latest")
                .load()
                .select(from_json(col("value").cast("string"), schema).alias("data"))
                .select("data.*");

        // 🔹 Clean string columns
        String[] stringCols = new String[]{
                "customerId", "productId", "customerName", "region",
                "sourceSystem", "customerSegment", "gender",
                "productName", "category", "brand", "productType",
                "paymentMethod", "shippingMethod", "orderStatus"
        };
        for (String colName : stringCols) {
            parsedDf = parsedDf.withColumn(colName, regexp_replace(col(colName), "\"", ""));
        }

        // 🔹 Lowercase column names
        for (String colName : parsedDf.columns()) {
            parsedDf = parsedDf.withColumnRenamed(colName, colName.toLowerCase());
        }

        // 🔹 Parse timestamp and filter nulls
        parsedDf = parsedDf
                .withColumn("event_time", to_timestamp(col("timestamp"), "yyyy-MM-dd HH:mm:ss.SSS"))
                .filter("customerid IS NOT NULL AND totalamount IS NOT NULL");

        // =========================
        // 🔹 Helper to print key-value
        // =========================
        java.util.function.BiConsumer<Dataset<Row>, String> printAsKeyValue = (df, name) -> {
            try {
                df.writeStream()
                        .outputMode("append")
                        .foreachBatch((batchDF, batchId) -> {
                            System.out.println("===== " + name + " Batch " + batchId + " =====");
                            batchDF.collectAsList().forEach(row -> {
                                String line = java.util.stream.IntStream.range(0, row.size())
                                        .mapToObj(i -> "\"" + row.schema().fields()[i].name() + "\" : \"" + row.get(i) + "\"")
                                        .collect(Collectors.joining(", "));
                                System.out.println(line);
                            });
                        })
                        .start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        // =========================
        // 🔹 5-min aggregates
        // =========================
        createAggregates(parsedDf, cassandraKeyspace, checkpointPath, printAsKeyValue, "5 minutes", "5min");

        // =========================
        // 🔹 1-hour aggregates
        // =========================
        createAggregates(parsedDf, cassandraKeyspace, checkpointPath, printAsKeyValue, "1 hour", "1hour");

        // Keep running
        spark.streams().awaitAnyTermination();
    }

    // =========================
    // 🔹 Method to create aggregates for all four tables
    // =========================
    private static void createAggregates(Dataset<Row> df, String keyspace, String checkpoint,
                                         java.util.function.BiConsumer<Dataset<Row>, String> printer,
                                         String duration, String windowLabel) throws TimeoutException {

        // ----- total revenue -----
        Dataset<Row> totalRevenue = df
                .withWatermark("event_time", duration)
                .groupBy(window(col("event_time"), duration))
                .agg(
                        sum("totalamount").alias("total_revenue"),
                        count("*").alias("transactions"),
                        expr("approx_count_distinct(customerid)").alias("unique_customers")
                )
                .withColumn("window_type", lit(windowLabel))
                .withColumn("window_start", col("window.start"))
                .withColumn("window_end", col("window.end"))
                .withColumn("update_time", current_timestamp())
                .select("window_type", "window_start", "window_end", "total_revenue", "transactions", "unique_customers", "update_time");

        printer.accept(totalRevenue, "totalRevenue_" + windowLabel);

        totalRevenue.writeStream()
                .outputMode("append")
                .format("org.apache.spark.sql.cassandra")
                .option("keyspace", keyspace)
                .option("table", "total_revenue")
                .option("checkpointLocation", checkpoint + "/totalRevenue_" + windowLabel + "_cassandra")
                .trigger(Trigger.ProcessingTime(duration))
                .start();

        // ----- region revenue -----
        Dataset<Row> regionRevenue = df
                .withWatermark("event_time", duration)
                .groupBy(window(col("event_time"), duration), col("region"))
                .agg(sum("totalamount").alias("total_revenue"))
                .withColumn("window_type", lit(windowLabel))
                .withColumn("window_start", col("window.start"))
                .withColumn("window_end", col("window.end"))
                .withColumn("update_time", current_timestamp())
                .select("window_type", "window_start", "window_end", "region", "total_revenue", "update_time");

        printer.accept(regionRevenue, "regionRevenue_" + windowLabel);

        regionRevenue.writeStream()
                .outputMode("append")
                .format("org.apache.spark.sql.cassandra")
                .option("keyspace", keyspace)
                .option("table", "region_revenue")
                .option("checkpointLocation", checkpoint + "/regionRevenue_" + windowLabel + "_cassandra")
                .trigger(Trigger.ProcessingTime(duration))
                .start();

        // ----- top products -----
        Dataset<Row> topProducts = df
                .withWatermark("event_time", duration)
                .groupBy(window(col("event_time"), duration), col("productid"), col("productname"))
                .agg(
                        sum("quantity").alias("total_quantity"),
                        sum("totalamount").alias("total_revenue")
                )
                .withColumn("window_type", lit(windowLabel))
                .withColumn("window_start", col("window.start"))
                .withColumn("window_end", col("window.end"))
                .withColumn("update_time", current_timestamp())
                .select("window_type", "window_start", "window_end", "productid", "productname", "total_quantity", "total_revenue", "update_time");

        printer.accept(topProducts, "topProducts_" + windowLabel);

        topProducts.writeStream()
                .outputMode("append")
                .format("org.apache.spark.sql.cassandra")
                .option("keyspace", keyspace)
                .option("table", "top_products")
                .option("checkpointLocation", checkpoint + "/topProducts_" + windowLabel + "_cassandra")
                .trigger(Trigger.ProcessingTime(duration))
                .start();

        // ----- top customers -----
        Dataset<Row> topCustomers = df
                .withWatermark("event_time", duration)
                .groupBy(window(col("event_time"), duration), col("customerid"), col("customersegment"))
                .agg(sum("totalamount").alias("total_spent"))
                .withColumn("window_type", lit(windowLabel))
                .withColumn("window_start", col("window.start"))
                .withColumn("window_end", col("window.end"))
                .withColumn("update_time", current_timestamp())
                .select("window_type", "window_start", "window_end", "customerid", "customersegment", "total_spent", "update_time");

        printer.accept(topCustomers, "topCustomers_" + windowLabel);

        topCustomers.writeStream()
                .outputMode("append")
                .format("org.apache.spark.sql.cassandra")
                .option("keyspace", keyspace)
                .option("table", "top_customers")
                .option("checkpointLocation", checkpoint + "/topCustomers_" + windowLabel + "_cassandra")
                .trigger(Trigger.ProcessingTime(duration))
                .start();
    }
}

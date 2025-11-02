package edu.hust.marketflow.batch;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import edu.hust.marketflow.ConfigLoader;
import edu.hust.marketflow.model.serving.HourlyPurchaseRevenue;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.*;

import java.sql.Timestamp;
import java.util.*;

import static org.apache.spark.sql.functions.*;

public class HdfsToHourlyPurchaseRevenue {

    public static void main(String[] args) {
        // --- Load configurations ---
        String cassandraHost = ConfigLoader.getOrThrow(ConfigLoader.CASSANDRA_HOST_KEY);
        String cassandraPort = ConfigLoader.getOrThrow(ConfigLoader.CASSANDRA_PORT_KEY);
        String cassandraKeyspace = ConfigLoader.getOrThrow(ConfigLoader.CASSANDRA_KEYSPACE);
        String cassandraTable = "hourly_purchase_revenue";
        String hdfsPath = ConfigLoader.getOrThrow(ConfigLoader.HDFS_DATA_DIR);

        // --- Initialize Spark ---
        SparkSession spark = SparkSession.builder()
                .appName("HdfsToHourlyPurchaseRevenue")
                .master("spark://spark-master:7077")
                .config("spark.cassandra.connection.host", cassandraHost)
                .config("spark.cassandra.connection.port", cassandraPort)
                .getOrCreate();

        spark.sparkContext().setLogLevel("WARN");
        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());

        // --- Read data from HDFS ---
        Dataset<Row> df = spark.read().parquet(hdfsPath)
                .filter("totalAmount IS NOT NULL AND timestamp IS NOT NULL")
                .withColumn("timestamp", to_timestamp(col("timestamp")))
                .filter(col("timestamp").gt(expr("current_timestamp() - INTERVAL 1 HOUR")));

        // --- Normalize timestamp to hourly window ---
        df = df.withColumn("hour_window", date_format(col("timestamp"), "yyyy-MM-dd HH:00:00"));

        // --- Group and aggregate by region, hour, category ---
        Dataset<Row> agg = df.groupBy(col("region"), col("hour_window"), col("category"), col("sourceSystem"))
                .agg(
                        sum("totalAmount").alias("total_revenue"),
                        sum("quantity").alias("total_quantity"),
                        countDistinct("customerId").alias("total_orders"),
                        (sum("totalAmount").divide(countDistinct("customerId"))).alias("avg_order_value")
                );

        // --- Map aggregated DataFrame to model class ---
        JavaRDD<HourlyPurchaseRevenue> revenueRDD = agg.javaRDD().map(row -> {
            HourlyPurchaseRevenue r = new HourlyPurchaseRevenue();
            r.setRegion(row.getAs("region"));
            r.setHourWindow(row.getAs("hour_window"));
            r.setCategory(row.getAs("category"));
            r.setTotalRevenue(row.getAs("total_revenue") != null ? ((Number) row.getAs("total_revenue")).doubleValue() : 0.0);
            r.setTotalQuantity(row.getAs("total_quantity") != null ? ((Number) row.getAs("total_quantity")).longValue() : 0L);
            r.setTotalOrders(row.getAs("total_orders") != null ? ((Number) row.getAs("total_orders")).longValue() : 0L);
            r.setAvgOrderValue(row.getAs("avg_order_value") != null ? ((Number) row.getAs("avg_order_value")).doubleValue() : 0.0);
            r.setSourceSystem(row.getAs("sourceSystem"));
            return r;
        });

        // --- Preview sample data ---
        List<HourlyPurchaseRevenue> samples = revenueRDD.take(5);
        System.out.println("✅ Preview aggregated hourly revenue data:");
        samples.forEach(System.out::println);

        // --- Write results to Cassandra ---
        CassandraJavaUtil.javaFunctions(revenueRDD)
                .writerBuilder(
                        cassandraKeyspace,
                        cassandraTable,
                        CassandraJavaUtil.mapToRow(HourlyPurchaseRevenue.class)
                )
                .saveToCassandra();

        System.out.println("✅ Successfully saved hourly revenue data to Cassandra table: "
                + cassandraKeyspace + "." + cassandraTable);

        jsc.close();
        spark.stop();
    }
}

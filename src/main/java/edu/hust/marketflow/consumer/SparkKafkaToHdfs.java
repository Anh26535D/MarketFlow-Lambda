package edu.hust.marketflow.consumer;

import edu.hust.marketflow.ConfigLoader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;
import org.apache.spark.sql.types.StructType;

import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.*;

public class SparkKafkaToHdfs {
    public static void main(String[] args) throws StreamingQueryException, TimeoutException {

        String kafkaBootstrapServers = ConfigLoader.getOrThrow(ConfigLoader.KAFKA_BOOTSTRAP_SERVERS);
        String kafkaTopic = ConfigLoader.getOrThrow(ConfigLoader.KAFKA_TOPIC);
        String hdfsOutputPath = ConfigLoader.getOrThrow(ConfigLoader.HDFS_DATA_DIR);
        String checkpointPath = ConfigLoader.getOrThrow(ConfigLoader.HDFS_CHECKPOINT_DIR);

        SparkSession spark = SparkSession.builder()
                .appName("KafkaToHdfsStreaming")
                .master("spark://spark-master:7077")
                .getOrCreate();

        spark.sparkContext().setLogLevel("WARN");

        // --- Define Unified Schema (matches UnifiedDataModel) ---
        StructType unifiedSchema = new StructType()
                .add("customerId", "string")
                .add("customerName", "string")
                .add("customerSegment", "string")
                .add("region", "string")
                .add("gender", "string")
                .add("age", "integer")
                .add("income", "double")
                .add("productId", "string")
                .add("productName", "string")
                .add("category", "string")
                .add("brand", "string")
                .add("productType", "string")
                .add("price", "double")
                .add("quantity", "integer")
                .add("totalAmount", "double")
                .add("paymentMethod", "string")
                .add("shippingMethod", "string")
                .add("orderStatus", "string")
                .add("rating", "double")
                .add("timestamp", "string")
                .add("sourceSystem", "string");

        // --- Read from Kafka ---
        Dataset<Row> kafkaStream = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", kafkaBootstrapServers)
                .option("subscribe", kafkaTopic)
                .option("startingOffsets", "earliest")
                .load();

        // --- Extract and Parse JSON ---
        Dataset<Row> parsed = kafkaStream
                .selectExpr("CAST(value AS STRING) as jsonStr")
                .select(from_json(col("jsonStr"), unifiedSchema).alias("data"))
                .select("data.*");

        // --- Data Cleaning ---
        Dataset<Row> cleaned = parsed
                // Drop records missing key identifiers
                .filter("customerId IS NOT NULL AND productId IS NOT NULL AND timestamp IS NOT NULL")

                // Normalize categorical columns
                .withColumn("gender", lower(trim(col("gender"))))
                .withColumn("region", upper(trim(col("region"))))
                .withColumn("orderStatus", lower(trim(col("orderStatus"))))

                // Standardize timestamp format
                .withColumn("timestamp", date_format(to_timestamp(col("timestamp")), "yyyy-MM-dd HH:mm:ss.SSS"))

                // Deduplicate based on unique transaction keys
                .dropDuplicates("customerId", "productId", "timestamp");

        // --- Write to HDFS (Parquet format preferred) ---
        StreamingQuery query = cleaned.writeStream()
                .format("parquet") // more efficient than text
                .outputMode("append")
                .option("path", hdfsOutputPath)
                .option("checkpointLocation", checkpointPath)
                .trigger(Trigger.ProcessingTime("5 seconds"))
                .start();

        query.awaitTermination();
    }
}

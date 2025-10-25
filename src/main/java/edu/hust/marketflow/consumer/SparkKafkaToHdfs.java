package edu.hust.marketflow.consumer;

import edu.hust.marketflow.ConfigLoader;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;

import java.util.concurrent.TimeoutException;

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

        Dataset<Row> kafkaStream = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", kafkaBootstrapServers)
                .option("subscribe", kafkaTopic)
                .option("startingOffsets", "earliest")
                .load();

        Dataset<Row> values = kafkaStream.selectExpr("CAST(value AS STRING)");

        StreamingQuery query = values.writeStream()
                .outputMode("append")
                .format("text")
                .option("path", hdfsOutputPath)
                .option("checkpointLocation", checkpointPath)
                .trigger(Trigger.ProcessingTime("2 seconds"))
                .start();

        query.awaitTermination();
    }
}

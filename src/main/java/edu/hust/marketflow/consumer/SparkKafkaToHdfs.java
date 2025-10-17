package edu.hust.marketflow.consumer;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;

import java.util.concurrent.TimeoutException;

public class SparkKafkaToHdfs {
    public static void main(String[] args) throws StreamingQueryException, TimeoutException {

        String kafkaBootstrapServers = "kafka:9092";  // docker service name
        String kafkaTopic = "stock_prices";
        String hdfsOutputPath = "hdfs://namenode:8020/marketflow/stock_prices/";
        String checkpointPath = "hdfs://namenode:8020/marketflow/checkpoints/";

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
                .format("text") // could switch to parquet later
                .option("path", hdfsOutputPath)
                .option("checkpointLocation", checkpointPath)
                .trigger(Trigger.ProcessingTime("5 seconds"))
                .start();

        query.awaitTermination();
    }
}

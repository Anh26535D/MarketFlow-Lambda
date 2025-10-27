package edu.hust.marketflow.speed;

import edu.hust.marketflow.ConfigLoader;
import org.apache.spark.sql.*;
import org.apache.spark.sql.streaming.*;
import org.apache.spark.sql.types.*;

import static org.apache.spark.sql.functions.*;

public class SaveLiveDataToCassandra {

    public static void main(String[] args) throws Exception {

        // Cassandra config
        String cassandraHost = ConfigLoader.getOrThrow(ConfigLoader.CASSANDRA_HOST_KEY);
        String cassandraPort = ConfigLoader.getOrThrow(ConfigLoader.CASSANDRA_PORT_KEY);
        String cassandraKeyspace = ConfigLoader.getOrThrow(ConfigLoader.CASSANDRA_KEYSPACE);
        String cassandraTable = ConfigLoader.getOrThrow(ConfigLoader.CASSANDRA_TABLE);

        // Spark session
        SparkSession spark = SparkSession.builder()
                .appName("KafkaToCassandraStream")
                .master("spark://spark-master:7077")
                .config("spark.cassandra.connection.host", cassandraHost)
                .config("spark.cassandra.connection.port", cassandraPort)
                .getOrCreate();

        spark.sparkContext().setLogLevel("WARN");

        // Schema matches StockPriceModel.toJsonFormat()
        StructType schema = new StructType()
                .add("symbol", DataTypes.StringType, true)
                .add("date", DataTypes.StringType, true)
                .add("adjustedPrice", DataTypes.DoubleType, true)
                .add("closePrice", DataTypes.DoubleType, true)
                .add("change", DataTypes.DoubleType, true)
                .add("matchedVolume", DataTypes.LongType, true)
                .add("matchedValue", DataTypes.DoubleType, true)
                .add("negotiatedVolume", DataTypes.LongType, true)
                .add("negotiatedValue", DataTypes.DoubleType, true)
                .add("openPrice", DataTypes.DoubleType, true)
                .add("highPrice", DataTypes.DoubleType, true)
                .add("lowPrice", DataTypes.DoubleType, true);

        // Read Kafka stream
        Dataset<Row> kafkaStream = spark.readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers", ConfigLoader.getOrThrow("kafka.bootstrap.servers"))
                .option("subscribe", "stock_prices")
                .option("startingOffsets", "latest")
                .load();

        // Parse JSON
        Dataset<Row> parsed = kafkaStream
                .selectExpr("CAST(value AS STRING) AS json")
                .select(from_json(col("json"), schema).as("data"))
                .select("data.*");

        // Convert date to timestamp
        Dataset<Row> parsedWithTimestamp = parsed
                .withColumn("date", to_timestamp(col("date"), "yyyy-MM-dd HH:mm:ss"));

        // Write every micro-batch to Cassandra
        StreamingQuery query = parsedWithTimestamp.writeStream()
                .outputMode("append")
                .foreachBatch((batchDF, batchId) -> {
                    batchDF.write()
                            .format("org.apache.spark.sql.cassandra")
                            .option("keyspace", cassandraKeyspace)
                            .option("table", cassandraTable)
                            .mode(SaveMode.Append)
                            .save();
                    System.out.println("Batch " + batchId + " saved: " + batchDF.count() + " rows.");
                })
                .trigger(Trigger.ProcessingTime("5 minutes"))
                .start();

        query.awaitTermination();
    }
}

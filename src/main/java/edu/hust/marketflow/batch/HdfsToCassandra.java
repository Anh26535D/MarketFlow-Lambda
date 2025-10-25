package edu.hust.marketflow.batch;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import edu.hust.marketflow.ConfigLoader;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import edu.hust.marketflow.model.StockPriceModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HdfsToCassandra {

    public static void main(String[] args) {
        String cassandraHost = ConfigLoader.getOrThrow(ConfigLoader.CASSANDRA_HOST_KEY);
        String cassandraPort = ConfigLoader.getOrThrow(ConfigLoader.CASSANDRA_PORT_KEY);
        String cassandraKeyspace = ConfigLoader.getOrThrow(ConfigLoader.CASSANDRA_KEYSPACE);
        String cassandraTable = ConfigLoader.getOrThrow(ConfigLoader.CASSANDRA_TABLE);

        String hdfsPath = ConfigLoader.getOrThrow(ConfigLoader.HDFS_DATA_DIR);

        // Initialize SparkSession & JavaSparkContext
        SparkSession spark = SparkSession.builder()
                .appName("HdfsToCassandra")
                .master("spark://spark-master:7077")
                .config("spark.cassandra.connection.host", cassandraHost)
                .config("spark.cassandra.connection.port", cassandraPort)
                .getOrCreate();

        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());
        spark.sparkContext().setLogLevel("WARN");


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        JavaRDD<StockPriceModel> stockRDD = spark.read().json(hdfsPath)
                .javaRDD()
                .map(row -> {
                    String dateStr = row.getAs("date");
                    java.util.Date date = null;
                    try {
                        if (dateStr != null) date = sdf.parse(dateStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    return new StockPriceModel(
                            row.getAs("symbol"),
                            date,
                            row.getAs("adjustedPrice"),
                            row.getAs("closePrice"),
                            row.getAs("change"),
                            row.getAs("matchedVolume"),
                            row.getAs("matchedValue"),
                            row.getAs("negotiatedVolume"),
                            row.getAs("negotiatedValue"),
                            row.getAs("openPrice"),
                            row.getAs("highPrice"),
                            row.getAs("lowPrice")
                    );
                });

        // ✅ Preview a few records to verify correctness
        List<StockPriceModel> sample = stockRDD.take(5);
        System.out.println("✅ Preview sample data before writing to Cassandra:");
        for (StockPriceModel record : sample) {
            System.out.println(record);
        }

        // ✅ Define column mappings (Java field → Cassandra column)
        Map<String, String> columnNameMappings = new HashMap<>();
        columnNameMappings.put("symbol", "symbol");
        columnNameMappings.put("date", "date");
        columnNameMappings.put("negotiatedVolume", "negotiated_volume");
        columnNameMappings.put("matchedVolume", "matched_volume");
        columnNameMappings.put("negotiatedValue", "negotiated_value");
        columnNameMappings.put("matchedValue", "matched_value");
        columnNameMappings.put("openPrice", "open_price");
        columnNameMappings.put("highPrice", "high_price");
        columnNameMappings.put("lowPrice", "low_price");
        columnNameMappings.put("closePrice", "close_price");
        columnNameMappings.put("adjustedPrice", "adjusted_price");
        columnNameMappings.put("change", "change");


        // ✅ Write RDD to Cassandra
        CassandraJavaUtil.javaFunctions(stockRDD)
                .writerBuilder(
                        cassandraKeyspace,
                        cassandraTable,
                        CassandraJavaUtil.mapToRow(StockPriceModel.class, columnNameMappings)
                )
                .saveToCassandra();

        System.out.println("✅ Successfully saved data to Cassandra table: " + cassandraKeyspace + "." + cassandraTable);

        jsc.close();
        spark.stop();
    }
}

package batch;

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.datastax.spark.connector.japi.CassandraRow;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

import java.util.List;

public class CassandraReadTest {
    public static void main(String[] args) {
        String cassandraHost = System.getenv().getOrDefault("CASSANDRA_HOST", "cassandra");
        String keyspace = "marketflow";
        String table = "stock_prices";

        SparkSession spark = SparkSession.builder()
                .appName("VerifyCassandraConnectionJavaFunctions")
                .master("spark://spark-master:7077")
                .config("spark.cassandra.connection.host", cassandraHost)
                .config("spark.cassandra.connection.port", "9042")
                .getOrCreate();

        spark.sparkContext().setLogLevel("WARN");

        try (JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext())) {
            System.out.println("🚀 Trying to read from Cassandra via JavaFunctions: " + keyspace + "." + table);
            JavaRDD<CassandraRow> rdd = CassandraJavaUtil.javaFunctions(jsc)
                    .cassandraTable(keyspace, table);

            List<CassandraRow> sample = rdd.take(5);

            if (!sample.isEmpty()) {
                System.out.println("✅ Successfully connected to Cassandra!");
                System.out.println("Sample data:");
                for (CassandraRow row : sample) {
                    // Print all columns of the row
                    System.out.println(" -> " + row);
                }
            } else {
                System.out.println("⚠️ Table is empty.");
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to read from Cassandra! " + e.getMessage());
            e.printStackTrace(System.err);
        } finally {
            spark.stop();
        }
    }
}

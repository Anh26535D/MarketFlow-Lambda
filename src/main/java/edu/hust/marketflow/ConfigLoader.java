package edu.hust.marketflow;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final String CONFIG_PATH = "config.properties";
    private static final Properties props = new Properties();

    public static final String KAFKA_BOOTSTRAP_SERVERS = "kafka.bootstrap.servers";
    public static final String KAFKA_TOPIC = "kafka.topic";

    public static final String HDFS_DATA_DIR = "hdfs.data.dir";
    public static final String HDFS_CHECKPOINT_DIR = "hdfs.checkpoint.dir";

    public static final String CASSANDRA_HOST_KEY = "cassandra.host";
    public static final String CASSANDRA_PORT_KEY = "cassandra.port";
    public static final String CASSANDRA_KEYSPACE = "cassandra.keyspace";
    public static final String CASSANDRA_TABLE = "cassandra.table";

    static {
        try (InputStream input = ConfigLoader.class.getClassLoader()
                .getResourceAsStream(CONFIG_PATH)) {
            if (input != null) props.load(input);
        } catch (Exception e) {
            System.err.println("Failed to load " + CONFIG_PATH + " " + e.getMessage());
        }
    }

    public static String getOrThrow(String key) {
        String val = props.getProperty(key);
        if (val == null) {
            throw new RuntimeException("Missing required config key: " + key);
        }
        return val;
    }

    public static String getOrDefault(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}
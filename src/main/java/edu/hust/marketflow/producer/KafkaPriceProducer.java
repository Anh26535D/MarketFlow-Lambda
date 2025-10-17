package edu.hust.marketflow.producer;

import edu.hust.marketflow.model.StockPriceModel;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaPriceProducer {
    private static final String TOPIC = "stock_prices";
    private static final String BOOTSTRAP_SERVERS = "localhost:9094";

    private final KafkaProducer<String, String> kafkaProducer;

    public KafkaPriceProducer(KafkaProducer<String, String> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public Future<RecordMetadata> send(ProducerRecord<String, String> record) {
        return this.kafkaProducer.send(record);
    }

    private static String toJson(StockPriceModel record) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return String.format(
                "{\"symbol\":\"%s\",\"date\":\"%s\",\"adjustedPrice\":%.2f,\"closePrice\":%.2f,\"change\":%.2f," +
                        "\"matchedVolume\":%d,\"matchedValue\":%.2f,\"negotiatedVolume\":%d,\"negotiatedValue\":%.2f," +
                        "\"openPrice\":%.2f,\"highPrice\":%.2f,\"lowPrice\":%.2f}",
                record.getSymbol(),
                df.format(record.getDate()),
                record.getAdjustedPrice(),
                record.getClosePrice(),
                record.getChange(),
                record.getMatchedVolume(),
                record.getMatchedValue(),
                record.getNegotiatedVolume(),
                record.getNegotiatedValue(),
                record.getOpenPrice(),
                record.getHighPrice(),
                record.getLowPrice()
        );
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(props);
        KafkaPriceProducer priceProducer = new KafkaPriceProducer(kafkaProducer);

        while (true) {
            StockPriceModel record = new StockPriceModel(
                    "VIC", new Date(), 71.5, 72.1, 0.8,
                    500_000, 35_000_000, 50_000, 3_500_000,
                    71.0, 72.5, 70.8
            );

            String jsonValue = toJson(record);
            ProducerRecord<String, String> kafkaRecord =
                    new ProducerRecord<>(TOPIC, record.getSymbol(), jsonValue);

            priceProducer.send(kafkaRecord).get();
            System.out.println("✅ Sent: " + jsonValue);

            // Add 2 seconds delay between messages
            Thread.sleep(2000);
        }
    }
}

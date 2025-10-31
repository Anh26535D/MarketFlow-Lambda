package edu.hust.marketflow.producer;

import com.google.gson.Gson;
import edu.hust.marketflow.ConfigLoader;
import edu.hust.marketflow.model.UnifiedDataModel;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.function.Consumer;

public class KafkaProducerDataConsumer implements Consumer<UnifiedDataModel> {
    private static final String TOPIC = ConfigLoader.getOrThrow(ConfigLoader.KAFKA_TOPIC);
    private static final String BOOTSTRAP_SERVERS = "localhost:9094";

    private final KafkaProducer<String, String> kafkaProducer;
    private final Gson gson = new Gson();

    public KafkaProducerDataConsumer() {
        this.kafkaProducer = createKafkaProducer();
    }

    private KafkaProducer<String, String> createKafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    @Override
    public void accept(UnifiedDataModel unifiedDataModel) {
        String json = gson.toJson(unifiedDataModel);

        ProducerRecord<String, String> kafkaRecord = new ProducerRecord<>(TOPIC, unifiedDataModel.customerId, json);

        kafkaProducer.send(kafkaRecord, (metadata, exception) -> {
            if (exception != null) {
                System.err.println("!! Failed to send record: " + exception.getMessage());
            } else {
                System.out.printf(">> Sent to Kafka | Topic=%s Partition=%d Offset=%d | %s%n",
                        metadata.topic(), metadata.partition(), metadata.offset(), json);
            }
        });
    }
}

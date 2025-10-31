package edu.hust.marketflow.producer;

import edu.hust.marketflow.model.UnifiedDataModel;
import edu.hust.marketflow.producer.datagenerator.DataWrapper;
import edu.hust.marketflow.producer.datagenerator.HnMDataWrapper;
import edu.hust.marketflow.producer.datagenerator.OlistDataWrapper;
import edu.hust.marketflow.producer.datagenerator.RetailDataWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FakeStreamProducer {
    private final List<DataWrapper> wrappers = new ArrayList<>();
    private final long delayMillis;
    private final List<Consumer<UnifiedDataModel>> dataConsumers = new ArrayList<>();

    public FakeStreamProducer(long delayMillis) {
        this.delayMillis = delayMillis;
    }

    public FakeStreamProducer addWrapper(DataWrapper wrapper) {
        this.wrappers.add(wrapper);
        return this;
    }

    public FakeStreamProducer addDataConsumer(Consumer<UnifiedDataModel> consumer) {
        this.dataConsumers.add(consumer);
        return this;
    }

    public void start() throws Exception {
        int wrapperIdx = 0;
        while (true) {
            UnifiedDataModel record = wrappers.get(wrapperIdx % wrappers.size()).nextData();
            wrapperIdx++;
            for (Consumer<UnifiedDataModel> consumer : dataConsumers) {
                consumer.accept(record);
            }
            Thread.sleep(delayMillis);
        }
    }

    public static void main(String[] args) throws Exception {
        FakeStreamProducer hmStream = new FakeStreamProducer(5000);
        hmStream.addWrapper(new HnMDataWrapper())
                .addWrapper(new OlistDataWrapper())
                .addWrapper(new RetailDataWrapper())
                .addDataConsumer(new KafkaProducerDataConsumer());
        hmStream.start();
    }
}


package edu.hust.marketflow.producer.datagenerator;

import edu.hust.marketflow.model.UnifiedDataModel;

import javax.annotation.Nullable;

public interface DataWrapper {
    @Nullable
    UnifiedDataModel nextData();
}

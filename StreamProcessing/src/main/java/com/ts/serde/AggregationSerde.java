package com.ts.serde;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class AggregationSerde implements Serde<AggregationClass> {
    @Override
    public Serializer<AggregationClass> serializer() {
        return new AggregateSerializer<>();
    }

    @Override
    public Deserializer<AggregationClass> deserializer() {
        return new AggregateDeserializer<>();
    }
}

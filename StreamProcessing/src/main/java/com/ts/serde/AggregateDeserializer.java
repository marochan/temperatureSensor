package com.ts.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class AggregateDeserializer<T> implements Deserializer<AggregationClass> {

    private ObjectMapper objectMapper = new ObjectMapper();

    private Class<T> tClass;
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {


    }

    @Override
    public AggregationClass deserialize(String s, byte[] bytes) {
        if (bytes == null)
            return null;

        AggregationClass data;
        try {
            data = objectMapper.readValue(bytes, AggregationClass.class);
        } catch (Exception e) {
            throw new SerializationException(e);
        }

        return data;
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }
}

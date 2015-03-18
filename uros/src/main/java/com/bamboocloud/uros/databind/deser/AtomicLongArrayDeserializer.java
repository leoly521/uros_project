package com.bamboocloud.uros.databind.deser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicLongArray;

import com.bamboocloud.uros.io.UrosReader;

final class AtomicLongArrayDeserializer implements UrosDeserializer {

    public final static UrosDeserializer instance = new AtomicLongArrayDeserializer();

    public Object read(UrosReader reader, Class<?> cls, Type type) throws IOException {
        return new AtomicLongArray(reader.readLongArray());
    }

}

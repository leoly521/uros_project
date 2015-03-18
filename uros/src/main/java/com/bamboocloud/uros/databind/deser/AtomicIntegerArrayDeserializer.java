package com.bamboocloud.uros.databind.deser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicIntegerArray;

import com.bamboocloud.uros.io.UrosReader;

final class AtomicIntegerArrayDeserializer implements UrosDeserializer {

    public final static UrosDeserializer instance = new AtomicIntegerArrayDeserializer();

    public Object read(UrosReader reader, Class<?> cls, Type type) throws IOException {
        return new AtomicIntegerArray(reader.readIntArray());
    }

}

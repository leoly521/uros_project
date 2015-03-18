package com.bamboocloud.uros.databind.deser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicInteger;

import com.bamboocloud.uros.io.UrosReader;

final class AtomicIntegerDeserializer implements UrosDeserializer {

    public final static UrosDeserializer instance = new AtomicIntegerDeserializer();

    public Object read(UrosReader reader, Class<?> cls, Type type) throws IOException {
        return new AtomicInteger(reader.readInt());
    }

}

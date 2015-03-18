package com.bamboocloud.uros.databind.deser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicLong;

import com.bamboocloud.uros.io.UrosReader;

final class AtomicLongDeserializer implements UrosDeserializer {

    public final static UrosDeserializer instance = new AtomicLongDeserializer();

    public Object read(UrosReader reader, Class<?> cls, Type type) throws IOException {
        return new AtomicLong(reader.readLong());
    }

}

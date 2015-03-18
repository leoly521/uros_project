package com.bamboocloud.uros.databind.deser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicBoolean;

import com.bamboocloud.uros.io.UrosReader;

final class AtomicBooleanDeserializer implements UrosDeserializer {

    public final static UrosDeserializer instance = new AtomicBooleanDeserializer();

    public Object read(UrosReader reader, Class<?> cls, Type type) throws IOException {
        return new AtomicBoolean(reader.readBoolean());
    }

}

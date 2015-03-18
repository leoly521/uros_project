package com.bamboocloud.uros.databind.deser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

import com.bamboocloud.uros.io.UrosReader;

final class HashMapDeserializer implements UrosDeserializer {

    public final static UrosDeserializer instance = new HashMapDeserializer();

    public Object read(UrosReader reader, Class<?> cls, Type type) throws IOException {
        return reader.readMap(HashMap.class, type);
    }

}

package com.bamboocloud.uros.databind.deser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;

import com.bamboocloud.uros.io.UrosReader;

final class HashSetDeserializer implements UrosDeserializer {

    public final static UrosDeserializer instance = new HashSetDeserializer();

    public Object read(UrosReader reader, Class<?> cls, Type type) throws IOException {
        return reader.readCollection(HashSet.class, type);
    }

}

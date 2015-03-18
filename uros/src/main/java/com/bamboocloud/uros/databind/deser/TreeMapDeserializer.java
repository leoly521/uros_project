package com.bamboocloud.uros.databind.deser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.TreeMap;

import com.bamboocloud.uros.io.UrosReader;

final class TreeMapDeserializer implements UrosDeserializer {

    public final static UrosDeserializer instance = new TreeMapDeserializer();

    public Object read(UrosReader reader, Class<?> cls, Type type) throws IOException {
        return reader.readMap(TreeMap.class, type);
    }

}

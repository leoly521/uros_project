package com.bamboocloud.uros.databind.deser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.TreeSet;

import com.bamboocloud.uros.io.UrosReader;

final class TreeSetDeserializer implements UrosDeserializer {

    public final static UrosDeserializer instance = new TreeSetDeserializer();

    public Object read(UrosReader reader, Class<?> cls, Type type) throws IOException {
        return reader.readCollection(TreeSet.class, type);
    }

}

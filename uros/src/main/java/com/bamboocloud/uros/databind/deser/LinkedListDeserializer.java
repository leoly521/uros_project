package com.bamboocloud.uros.databind.deser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedList;

import com.bamboocloud.uros.io.UrosReader;

final class LinkedListDeserializer implements UrosDeserializer {

    public final static UrosDeserializer instance = new LinkedListDeserializer();

    public Object read(UrosReader reader, Class<?> cls, Type type) throws IOException {
        return reader.readCollection(LinkedList.class, type);
    }

}

package com.bamboocloud.uros.databind.deser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.bamboocloud.uros.io.UrosReader;

final class ArrayListDeserializer implements UrosDeserializer {

    public final static UrosDeserializer instance = new ArrayListDeserializer();

    public Object read(UrosReader reader, Class<?> cls, Type type) throws IOException {
        return reader.readCollection(ArrayList.class, type);
    }

}

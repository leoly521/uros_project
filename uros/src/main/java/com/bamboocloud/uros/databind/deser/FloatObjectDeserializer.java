package com.bamboocloud.uros.databind.deser;

import java.io.IOException;
import java.lang.reflect.Type;

import com.bamboocloud.uros.io.UrosReader;

final class FloatObjectDeserializer implements UrosDeserializer {

    public final static UrosDeserializer instance = new FloatObjectDeserializer();

    public Object read(UrosReader reader, Class<?> cls, Type type) throws IOException {
        return reader.readFloatObject();
    }

}

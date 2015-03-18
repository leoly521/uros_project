package com.bamboocloud.uros.databind.deser;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import com.bamboocloud.uros.common.exception.UrosProtocolException;
import com.bamboocloud.uros.io.UrosReader;

final class MapDeserializer implements UrosDeserializer {

    public final static UrosDeserializer instance = new MapDeserializer();

    public Object read(UrosReader reader, Class<?> cls, Type type) throws IOException {
        if (!Modifier.isInterface(cls.getModifiers()) && !Modifier.isAbstract(cls.getModifiers())) {
            return reader.readMap(cls, type);
        }
        else {
            throw new UrosProtocolException(type.toString() + " is not an instantiable class.");
        }
    }

}

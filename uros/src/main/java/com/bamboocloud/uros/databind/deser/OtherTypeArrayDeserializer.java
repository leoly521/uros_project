package com.bamboocloud.uros.databind.deser;

import java.io.IOException;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

import com.bamboocloud.uros.io.UrosReader;

final class OtherTypeArrayDeserializer implements UrosDeserializer {

    public final static UrosDeserializer instance = new OtherTypeArrayDeserializer();

    public Object read(UrosReader reader, Class<?> cls, Type type) throws IOException {
        Class<?> componentClass = cls.getComponentType();
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return reader.readOtherTypeArray(componentClass, componentType);
        }
        else {
            return reader.readOtherTypeArray(componentClass, componentClass);
        }
    }

}

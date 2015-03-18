package com.bamboocloud.uros.databind.deser;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.bamboocloud.uros.io.UrosReader;

final class AtomicReferenceDeserializer implements UrosDeserializer {

    public final static UrosDeserializer instance = new AtomicReferenceDeserializer();

    public Object read(UrosReader reader, Class<?> cls, Type type) throws IOException {
        if (type instanceof ParameterizedType) {
            return reader.readAtomicReference(((ParameterizedType)type).getActualTypeArguments()[0]);
        }
        else {
            return reader.readAtomicReference(Object.class);
        }
    }

}

package com.bamboocloud.uros.databind.deser;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.bamboocloud.uros.io.UrosHelper;
import com.bamboocloud.uros.io.UrosReader;

final class AtomicReferenceArrayDeserializer implements UrosDeserializer {

    public final static UrosDeserializer instance = new AtomicReferenceArrayDeserializer();

    public Object read(UrosReader reader, Class<?> cls, Type type) throws IOException {
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType)type).getActualTypeArguments()[0];
            cls = UrosHelper.toClass(type);
            return reader.readAtomicReferenceArray(cls, type);
        }
        else {
            return reader.readAtomicReferenceArray(Object.class, Object.class);
        }
    }

}

package com.bamboocloud.uros.databind.deser;

import java.io.IOException;
import java.lang.reflect.Type;

import com.bamboocloud.uros.io.UrosReader;

final class DateArrayDeserializer implements UrosDeserializer {

    public final static UrosDeserializer instance = new DateArrayDeserializer();

    public Object read(UrosReader reader, Class<?> cls, Type type) throws IOException {
        return reader.readDateArray();
    }

}

package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.common.exception.UrosProtocolException;
import com.bamboocloud.uros.io.UrosWriter;

@SuppressWarnings("rawtypes")
final class ObjectSerializer implements UrosSerializer {

    public final static UrosSerializer instance = new ObjectSerializer();

    public void write(UrosWriter writer, Object obj) throws IOException {
        if (obj != null) {
            Class<?> cls = obj.getClass();
            if (Object.class.equals(cls)) {
                throw new UrosProtocolException("Can't serialize an object of the Object class.");
            }
        }
        writer.serialize(obj);
    }
}

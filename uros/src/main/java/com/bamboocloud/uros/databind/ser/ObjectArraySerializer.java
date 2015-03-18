package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class ObjectArraySerializer implements UrosSerializer<Object[]> {

    public final static UrosSerializer<Object[]> instance = new ObjectArraySerializer();

    public void write(UrosWriter writer, Object[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class StringArraySerializer implements UrosSerializer<String[]> {

    public final static UrosSerializer<String[]> instance = new StringArraySerializer();

    public void write(UrosWriter writer, String[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

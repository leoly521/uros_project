package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class StringBuilderArraySerializer implements UrosSerializer<StringBuilder[]> {

    public final static UrosSerializer<StringBuilder[]> instance = new StringBuilderArraySerializer();

    public void write(UrosWriter writer, StringBuilder[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class StringBufferArraySerializer implements UrosSerializer<StringBuffer[]> {

    public final static UrosSerializer<StringBuffer[]> instance = new StringBufferArraySerializer();

    public void write(UrosWriter writer, StringBuffer[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

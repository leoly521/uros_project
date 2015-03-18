package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class CharsArraySerializer implements UrosSerializer<char[][]> {

    public final static UrosSerializer<char[][]> instance = new CharsArraySerializer();

    public void write(UrosWriter writer, char[][] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

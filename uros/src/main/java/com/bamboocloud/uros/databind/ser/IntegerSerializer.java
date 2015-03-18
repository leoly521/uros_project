package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class IntegerSerializer implements UrosSerializer<Integer> {

    public final static UrosSerializer<Integer> instance = new IntegerSerializer();

    public void write(UrosWriter writer, Integer obj) throws IOException {
        writer.writeInteger(obj);
    }
}

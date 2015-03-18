package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class ShortSerializer implements UrosSerializer<Short> {

    public final static UrosSerializer<Short> instance = new ShortSerializer();

    public void write(UrosWriter writer, Short obj) throws IOException {
        writer.writeInteger(obj);
    }
}

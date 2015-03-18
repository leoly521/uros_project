package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class ByteSerializer implements UrosSerializer<Byte> {

    public final static UrosSerializer<Byte> instance = new ByteSerializer();

    public void write(UrosWriter writer, Byte obj) throws IOException {
        writer.writeInteger(obj);
    }
}

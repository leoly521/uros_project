package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class ByteArraySerializer implements UrosSerializer<byte[]> {

    public final static UrosSerializer<byte[]> instance = new ByteArraySerializer();

    public void write(UrosWriter writer, byte[] obj) throws IOException {
        writer.writeBytesWithRef(obj);
    }
}

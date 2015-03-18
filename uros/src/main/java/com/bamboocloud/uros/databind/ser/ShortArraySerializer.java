package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class ShortArraySerializer implements UrosSerializer<short[]> {

    public final static UrosSerializer<short[]> instance = new ShortArraySerializer();

    public void write(UrosWriter writer, short[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

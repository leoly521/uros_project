package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class LongArraySerializer implements UrosSerializer<long[]> {

    public final static UrosSerializer<long[]> instance = new LongArraySerializer();

    public void write(UrosWriter writer, long[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

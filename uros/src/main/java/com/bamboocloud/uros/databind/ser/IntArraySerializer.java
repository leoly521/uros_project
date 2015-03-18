package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class IntArraySerializer implements UrosSerializer<int[]> {

    public final static UrosSerializer<int[]> instance = new IntArraySerializer();

    public void write(UrosWriter writer, int[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

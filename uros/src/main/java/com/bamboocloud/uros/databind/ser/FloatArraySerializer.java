package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class FloatArraySerializer implements UrosSerializer<float[]> {

    public final static UrosSerializer<float[]> instance = new FloatArraySerializer();

    public void write(UrosWriter writer, float[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

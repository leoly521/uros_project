package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class FloatSerializer implements UrosSerializer<Float> {

    public final static UrosSerializer<Float> instance = new FloatSerializer();

    public void write(UrosWriter writer, Float obj) throws IOException {
        writer.writeDouble(obj);
    }
}

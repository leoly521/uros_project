package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class DoubleSerializer implements UrosSerializer<Double> {

    public final static UrosSerializer<Double> instance = new DoubleSerializer();

    public void write(UrosWriter writer, Double obj) throws IOException {
        writer.writeDouble(obj);
    }
}

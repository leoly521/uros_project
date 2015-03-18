package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class DoubleArraySerializer implements UrosSerializer<double[]> {

    public final static UrosSerializer<double[]> instance = new DoubleArraySerializer();

    public void write(UrosWriter writer, double[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

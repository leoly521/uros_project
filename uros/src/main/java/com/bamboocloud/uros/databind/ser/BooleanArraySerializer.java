package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class BooleanArraySerializer implements UrosSerializer<boolean[]> {

    public final static UrosSerializer<boolean[]> instance = new BooleanArraySerializer();

    public void write(UrosWriter writer, boolean[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

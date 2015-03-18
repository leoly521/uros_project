package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class BooleanSerializer implements UrosSerializer<Boolean> {

    public final static UrosSerializer<Boolean> instance = new BooleanSerializer();

    public void write(UrosWriter writer, Boolean obj) throws IOException {
        writer.writeBoolean(obj);
    }
}

package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

@SuppressWarnings("rawtypes")
final class NullSerializer implements UrosSerializer {

    public final static UrosSerializer instance = new NullSerializer();

    public void write(UrosWriter writer, Object obj) throws IOException {
        writer.writeNull();
    }
}

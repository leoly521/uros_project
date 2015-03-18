package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class LongSerializer implements UrosSerializer<Long> {

    public final static UrosSerializer<Long> instance = new LongSerializer();

    public void write(UrosWriter writer, Long obj) throws IOException {
        writer.writeLong(obj);
    }
}

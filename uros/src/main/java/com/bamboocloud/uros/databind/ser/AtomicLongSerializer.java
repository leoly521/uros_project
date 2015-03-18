package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import com.bamboocloud.uros.io.UrosWriter;

final class AtomicLongSerializer implements UrosSerializer<AtomicLong> {

    public final static UrosSerializer<AtomicLong> instance = new AtomicLongSerializer();

    public void write(UrosWriter writer, AtomicLong obj) throws IOException {
        writer.writeLong(obj.get());
    }
}

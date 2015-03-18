package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLongArray;

import com.bamboocloud.uros.io.UrosWriter;

final class AtomicLongArraySerializer implements UrosSerializer<AtomicLongArray> {

    public final static UrosSerializer<AtomicLongArray> instance = new AtomicLongArraySerializer();

    public void write(UrosWriter writer, AtomicLongArray obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

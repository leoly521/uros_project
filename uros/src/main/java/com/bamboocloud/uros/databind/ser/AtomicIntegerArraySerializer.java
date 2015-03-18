package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicIntegerArray;

import com.bamboocloud.uros.io.UrosWriter;

final class AtomicIntegerArraySerializer implements UrosSerializer<AtomicIntegerArray> {

    public final static UrosSerializer<AtomicIntegerArray> instance = new AtomicIntegerArraySerializer();

    public void write(UrosWriter writer, AtomicIntegerArray obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

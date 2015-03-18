package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.bamboocloud.uros.io.UrosWriter;

final class AtomicIntegerSerializer implements UrosSerializer<AtomicInteger> {

    public final static UrosSerializer<AtomicInteger> instance = new AtomicIntegerSerializer();

    public void write(UrosWriter writer, AtomicInteger obj) throws IOException {
        writer.writeInteger(obj.get());
    }
}

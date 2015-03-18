package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.bamboocloud.uros.io.UrosWriter;

final class AtomicBooleanSerializer implements UrosSerializer<AtomicBoolean> {

    public final static UrosSerializer<AtomicBoolean> instance = new AtomicBooleanSerializer();

    public void write(UrosWriter writer, AtomicBoolean obj) throws IOException {
        writer.writeBoolean(obj.get());
    }
}

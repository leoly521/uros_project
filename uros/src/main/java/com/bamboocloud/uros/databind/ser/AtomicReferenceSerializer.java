package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import com.bamboocloud.uros.io.UrosWriter;

@SuppressWarnings("rawtypes")
final class AtomicReferenceSerializer implements UrosSerializer<AtomicReference> {

    public final static UrosSerializer<AtomicReference> instance = new AtomicReferenceSerializer();

    public void write(UrosWriter writer, AtomicReference obj) throws IOException {
        writer.serialize(obj.get());
    }
}

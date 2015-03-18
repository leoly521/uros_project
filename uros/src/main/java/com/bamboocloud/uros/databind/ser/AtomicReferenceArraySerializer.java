package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReferenceArray;

import com.bamboocloud.uros.io.UrosWriter;

@SuppressWarnings("rawtypes")
final class AtomicReferenceArraySerializer implements UrosSerializer<AtomicReferenceArray> {

    public final static UrosSerializer<AtomicReferenceArray> instance = new AtomicReferenceArraySerializer();

    public void write(UrosWriter writer, AtomicReferenceArray obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

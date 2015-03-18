package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

@SuppressWarnings("rawtypes")
final class OtherTypeSerializer implements UrosSerializer {

    public final static UrosSerializer instance = new OtherTypeSerializer();

    public void write(UrosWriter writer, Object obj) throws IOException {
        writer.writeObjectWithRef(obj);
    }
}

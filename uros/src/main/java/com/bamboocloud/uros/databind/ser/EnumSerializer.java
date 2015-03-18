package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

@SuppressWarnings("rawtypes")
final class EnumSerializer implements UrosSerializer<Enum> {

    public final static UrosSerializer<Enum> instance = new EnumSerializer();

    public void write(UrosWriter writer, Enum obj) throws IOException {
        writer.writeString(obj.name());
    }
}

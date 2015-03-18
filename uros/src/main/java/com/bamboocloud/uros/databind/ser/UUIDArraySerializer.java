package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.util.UUID;

import com.bamboocloud.uros.io.UrosWriter;

final class UUIDArraySerializer implements UrosSerializer<UUID[]> {

    public final static UrosSerializer<UUID[]> instance = new UUIDArraySerializer();

    public void write(UrosWriter writer, UUID[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

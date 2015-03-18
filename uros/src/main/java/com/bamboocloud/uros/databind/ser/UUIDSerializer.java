package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.util.UUID;

import com.bamboocloud.uros.io.UrosWriter;

final class UUIDSerializer implements UrosSerializer<UUID> {

    public final static UrosSerializer<UUID> instance = new UUIDSerializer();

    public void write(UrosWriter writer, UUID obj) throws IOException {
        writer.writeUUIDWithRef(obj);
    }
}

package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.sql.Timestamp;

import com.bamboocloud.uros.io.UrosWriter;

final class TimestampSerializer implements UrosSerializer<Timestamp> {

    public final static UrosSerializer<Timestamp> instance = new TimestampSerializer();

    public void write(UrosWriter writer, Timestamp obj) throws IOException {
        writer.writeDateWithRef(obj);
    }
}

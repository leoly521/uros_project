package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.sql.Timestamp;

import com.bamboocloud.uros.io.UrosWriter;

final class TimestampArraySerializer implements UrosSerializer<Timestamp[]> {

    public final static UrosSerializer<Timestamp[]> instance = new TimestampArraySerializer();

    public void write(UrosWriter writer, Timestamp[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.sql.Time;

import com.bamboocloud.uros.io.UrosWriter;

final class TimeArraySerializer implements UrosSerializer<Time[]> {

    public final static UrosSerializer<Time[]> instance = new TimeArraySerializer();

    public void write(UrosWriter writer, Time[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.sql.Time;

import com.bamboocloud.uros.io.UrosWriter;

final class TimeSerializer implements UrosSerializer<Time> {

    public final static UrosSerializer<Time> instance = new TimeSerializer();

    public void write(UrosWriter writer, Time obj) throws IOException {
        writer.writeDateWithRef(obj);
    }
}

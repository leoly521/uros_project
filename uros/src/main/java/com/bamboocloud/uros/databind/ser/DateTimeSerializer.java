package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.util.Date;

import com.bamboocloud.uros.io.UrosWriter;

final class DateTimeSerializer implements UrosSerializer<Date> {

    public final static UrosSerializer<Date> instance = new DateTimeSerializer();

    public void write(UrosWriter writer, Date obj) throws IOException {
        writer.writeDateWithRef(obj);
    }
}

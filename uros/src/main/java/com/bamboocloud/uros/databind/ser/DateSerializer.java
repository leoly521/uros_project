package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.sql.Date;

import com.bamboocloud.uros.io.UrosWriter;

final class DateSerializer implements UrosSerializer<Date> {

    public final static UrosSerializer<Date> instance = new DateSerializer();

    public void write(UrosWriter writer, Date obj) throws IOException {
        writer.writeDateWithRef(obj);
    }
}

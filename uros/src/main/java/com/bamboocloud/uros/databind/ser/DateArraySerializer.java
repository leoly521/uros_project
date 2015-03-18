package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.sql.Date;

import com.bamboocloud.uros.io.UrosWriter;

final class DateArraySerializer implements UrosSerializer<Date[]> {

    public final static UrosSerializer<Date[]> instance = new DateArraySerializer();

    public void write(UrosWriter writer, Date[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

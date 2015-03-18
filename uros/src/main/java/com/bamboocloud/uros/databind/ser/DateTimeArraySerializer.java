package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.util.Date;

import com.bamboocloud.uros.io.UrosWriter;

final class DateTimeArraySerializer implements UrosSerializer<Date[]> {

    public final static UrosSerializer<Date[]> instance = new DateTimeArraySerializer();

    public void write(UrosWriter writer, Date[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

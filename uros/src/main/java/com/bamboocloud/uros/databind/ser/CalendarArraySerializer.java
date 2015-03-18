package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.util.Calendar;

import com.bamboocloud.uros.io.UrosWriter;

final class CalendarArraySerializer implements UrosSerializer<Calendar[]> {

    public final static UrosSerializer<Calendar[]> instance = new CalendarArraySerializer();

    public void write(UrosWriter writer, Calendar[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

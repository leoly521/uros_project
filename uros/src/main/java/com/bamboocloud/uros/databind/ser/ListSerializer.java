package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.util.List;

import com.bamboocloud.uros.io.UrosWriter;

@SuppressWarnings("rawtypes")
final class ListSerializer implements UrosSerializer<List> {

    public final static UrosSerializer<List> instance = new ListSerializer();

    public void write(UrosWriter writer, List obj) throws IOException {
        writer.writeListWithRef(obj);
    }
}

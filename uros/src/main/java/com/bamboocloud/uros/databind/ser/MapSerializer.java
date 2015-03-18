package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.util.Map;

import com.bamboocloud.uros.io.UrosWriter;

@SuppressWarnings("rawtypes")
final class MapSerializer implements UrosSerializer<Map> {

    public final static UrosSerializer<Map> instance = new MapSerializer();

    public void write(UrosWriter writer, Map obj) throws IOException {
        writer.writeMapWithRef(obj);
    }
}

package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class StringSerializer implements UrosSerializer<String> {

    public final static UrosSerializer<String> instance = new StringSerializer();

    public void write(UrosWriter writer, String obj) throws IOException {
        switch (obj.length()) {
            case 0: writer.writeEmpty(); break;
            case 1: writer.writeUTF8Char(obj.charAt(0)); break;
            default: writer.writeStringWithRef(obj); break;
        }
    }
}

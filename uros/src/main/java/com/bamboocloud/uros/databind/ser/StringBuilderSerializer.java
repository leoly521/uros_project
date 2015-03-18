package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class StringBuilderSerializer implements UrosSerializer<StringBuilder> {

    public final static UrosSerializer<StringBuilder> instance = new StringBuilderSerializer();

    public void write(UrosWriter writer, StringBuilder obj) throws IOException {
        switch (obj.length()) {
            case 0: writer.writeEmpty(); break;
            case 1: writer.writeUTF8Char(obj.charAt(0)); break;
            default: writer.writeStringWithRef(obj); break;
        }
    }
}

package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class StringBufferSerializer implements UrosSerializer<StringBuffer> {

    public final static UrosSerializer<StringBuffer> instance = new StringBufferSerializer();

    public void write(UrosWriter writer, StringBuffer obj) throws IOException {
        switch (obj.length()) {
            case 0: writer.writeEmpty(); break;
            case 1: writer.writeUTF8Char(obj.charAt(0)); break;
            default: writer.writeStringWithRef(obj); break;
        }
    }
}

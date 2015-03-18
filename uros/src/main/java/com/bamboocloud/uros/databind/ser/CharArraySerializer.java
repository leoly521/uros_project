package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class CharArraySerializer implements UrosSerializer<char[]> {

    public final static UrosSerializer<char[]> instance = new CharArraySerializer();

    public void write(UrosWriter writer, char[] obj) throws IOException {
        switch (obj.length) {
            case 0: writer.writeEmpty(); break;
            case 1: writer.writeUTF8Char(obj[0]); break;
            default: writer.writeStringWithRef(obj); break;
        }
    }
}

package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

final class CharSerializer implements UrosSerializer<Character> {

    public final static UrosSerializer<Character> instance = new CharSerializer();

    public void write(UrosWriter writer, Character obj) throws IOException {
        writer.writeUTF8Char(obj);
    }
}

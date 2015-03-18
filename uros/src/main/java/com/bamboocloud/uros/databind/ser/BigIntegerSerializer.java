package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.math.BigInteger;

import com.bamboocloud.uros.io.UrosWriter;

final class BigIntegerSerializer implements UrosSerializer<BigInteger> {

    public final static UrosSerializer<BigInteger> instance = new BigIntegerSerializer();

    public void write(UrosWriter writer, BigInteger obj) throws IOException {
        writer.writeLong(obj);
    }
}

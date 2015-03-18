package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.math.BigInteger;

import com.bamboocloud.uros.io.UrosWriter;

final class BigIntegerArraySerializer implements UrosSerializer<BigInteger[]> {

    public final static UrosSerializer<BigInteger[]> instance = new BigIntegerArraySerializer();

    public void write(UrosWriter writer, BigInteger[] obj) throws IOException {
        writer.writeArrayWithRef(obj);
    }
}

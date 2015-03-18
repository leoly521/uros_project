package com.bamboocloud.uros.databind.ser;

import java.io.IOException;
import java.math.BigDecimal;

import com.bamboocloud.uros.io.UrosWriter;

final class BigDecimalSerializer implements UrosSerializer<BigDecimal> {

    public final static UrosSerializer<BigDecimal> instance = new BigDecimalSerializer();

    public void write(UrosWriter writer, BigDecimal obj) throws IOException {
        writer.writeDouble(obj);
    }
}

package com.bamboocloud.uros.databind.ser;

import java.io.IOException;

import com.bamboocloud.uros.io.UrosWriter;

public interface UrosSerializer<T> {

	void write(UrosWriter writer, T obj) throws IOException;

}

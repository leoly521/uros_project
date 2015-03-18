package com.bamboocloud.uros.databind.deser;

import java.io.IOException;
import java.lang.reflect.Type;

import com.bamboocloud.uros.io.UrosReader;

public interface UrosDeserializer {

	Object read(UrosReader reader, Class<?> cls, Type type) throws IOException;

}

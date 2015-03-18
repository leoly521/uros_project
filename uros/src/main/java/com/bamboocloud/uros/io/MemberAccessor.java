package com.bamboocloud.uros.io;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

import com.bamboocloud.uros.databind.deser.UrosDeserializer;
import com.bamboocloud.uros.databind.ser.UrosSerializer;

abstract class MemberAccessor {

	protected Class<?> cls;
	protected Type type;
	@SuppressWarnings("rawtypes")
	protected UrosSerializer serializer;
	protected UrosDeserializer deserializer;

	abstract void set(Object obj, Object value) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException;

	abstract Object get(Object obj) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException;
}
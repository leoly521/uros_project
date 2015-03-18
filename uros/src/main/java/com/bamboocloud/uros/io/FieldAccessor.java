package com.bamboocloud.uros.io;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.bamboocloud.uros.databind.deser.DeserializerFactory;
import com.bamboocloud.uros.databind.ser.SerializerFactory;

final class FieldAccessor extends MemberAccessor {
	
	private final Field accessor;

	public FieldAccessor(Field accessor) {
		accessor.setAccessible(true);
		this.accessor = accessor;
		this.type = accessor.getGenericType();
		this.cls = UrosHelper.toClass(type);
		this.serializer = SerializerFactory.get(cls);
		this.deserializer = DeserializerFactory.get(cls);
	}

	@Override
	void set(Object obj, Object value) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		this.accessor.set(obj, value);
	}

	@Override
	Object get(Object obj) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		return this.accessor.get(obj);
	}
}
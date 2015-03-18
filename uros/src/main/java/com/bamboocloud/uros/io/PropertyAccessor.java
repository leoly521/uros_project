package com.bamboocloud.uros.io;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.bamboocloud.uros.databind.deser.DeserializerFactory;
import com.bamboocloud.uros.databind.ser.SerializerFactory;

final class PropertyAccessor extends MemberAccessor {
	
	private final Method getter;
	private final Method setter;
	private static final Object[] nullArgs = new Object[0];

	public PropertyAccessor(Method getter, Method setter) {
		getter.setAccessible(true);
		setter.setAccessible(true);
		this.getter = getter;
		this.setter = setter;
		this.type = getter.getGenericReturnType();
		this.cls = UrosHelper.toClass(type);
		this.serializer = SerializerFactory.get(cls);
		this.deserializer = DeserializerFactory.get(cls);
	}

	@Override
	void set(Object obj, Object value) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		setter.invoke(obj, new Object[] { value });
	}

	@Override
	Object get(Object obj) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		return getter.invoke(obj, nullArgs);
	}
}
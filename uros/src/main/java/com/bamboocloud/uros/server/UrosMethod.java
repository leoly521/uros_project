package com.bamboocloud.uros.server;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import com.bamboocloud.uros.common.UrosResultMode;

public final class UrosMethod {
	
    public Object obj;
    public Method method;
    public Type[] paramTypes;
    public UrosResultMode mode;
    public boolean simple;

    public UrosMethod(Method method, Object obj, UrosResultMode mode, boolean simple) {
        this.obj = obj;
        this.method = method;
        this.paramTypes = method.getGenericParameterTypes();
        this.mode = mode;
        this.simple = simple;
    }
    public UrosMethod(Method method, Object obj, UrosResultMode mode) {
        this(method, obj, mode, false);
    }
    public UrosMethod(Method method, Object obj, boolean simple) {
        this(method, obj, UrosResultMode.Normal, simple);
    }
    public UrosMethod(Method method, Object obj) {
        this(method, obj, UrosResultMode.Normal, false);
    }
    public UrosMethod(Method method) {
        this(method, null, UrosResultMode.Normal, false);
    }
    public UrosMethod(String methodName, Class<?> type, Class<?>[] paramTypes, UrosResultMode mode, boolean simple) throws NoSuchMethodException {
        this.obj = null;
        this.method = type.getMethod(methodName, paramTypes);
        if (!Modifier.isStatic(this.method.getModifiers())) {
            throw new NoSuchMethodException();
        }
        this.paramTypes = method.getGenericParameterTypes();
        this.mode = mode;
        this.simple = simple;
    }
    public UrosMethod(String methodName, Class<?> type, Class<?>[] paramTypes, UrosResultMode mode) throws NoSuchMethodException {
        this(methodName, type, paramTypes, mode, false);
    }
    public UrosMethod(String methodName, Class<?> type, Class<?>[] paramTypes, boolean simple) throws NoSuchMethodException {
        this(methodName, type, paramTypes, UrosResultMode.Normal, simple);
    }
    public UrosMethod(String methodName, Class<?> type, Class<?>[] paramTypes) throws NoSuchMethodException {
        this(methodName, type, paramTypes, UrosResultMode.Normal, false);
    }
    public UrosMethod(String methodName, Object obj, Class<?>[] paramTypes, UrosResultMode mode, boolean simple) throws NoSuchMethodException {
        this.obj = obj;
        this.method = obj.getClass().getMethod(methodName, paramTypes);
        if (Modifier.isStatic(this.method.getModifiers())) {
            throw new NoSuchMethodException();
        }
        this.paramTypes = method.getGenericParameterTypes();
        this.mode = mode;
        this.simple = simple;
    }
    public UrosMethod(String methodName, Object obj, Class<?>[] paramTypes, UrosResultMode mode) throws NoSuchMethodException {
        this(methodName, obj, paramTypes, mode, false);
    }
    public UrosMethod(String methodName, Object obj, Class<?>[] paramTypes, boolean simple) throws NoSuchMethodException {
        this(methodName, obj, paramTypes, UrosResultMode.Normal, simple);
    }
    public UrosMethod(String methodName, Object obj, Class<?>[] paramTypes) throws NoSuchMethodException {
        this(methodName, obj, paramTypes, UrosResultMode.Normal, false);
    }
}
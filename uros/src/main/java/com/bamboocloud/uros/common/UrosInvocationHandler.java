package com.bamboocloud.uros.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.bamboocloud.uros.client.api.UrosCallback;
import com.bamboocloud.uros.client.api.UrosCallback1;
import com.bamboocloud.uros.common.annotation.UrosByRef;
import com.bamboocloud.uros.common.annotation.UrosMethodName;
import com.bamboocloud.uros.common.annotation.UrosResultMode;
import com.bamboocloud.uros.common.annotation.UrosSimpleMode;
import com.bamboocloud.uros.io.UrosHelper;

public class UrosInvocationHandler implements InvocationHandler {
	
	private final UrosInvoker client;
	private final String ns;

	public UrosInvocationHandler(UrosInvoker client, String ns) {
		this.client = client;
		this.ns = (ns == null) ? "" : ns + "_";
	}

	@SuppressWarnings("rawtypes")
	public Object invoke(Object proxy, Method method, final Object[] arguments) throws Throwable {
		UrosMethodName methodName = method.getAnnotation(UrosMethodName.class);
		final String functionName = ns + ((methodName == null) ? method.getName() : methodName.value());
		UrosResultMode rm = method.getAnnotation(UrosResultMode.class);
		final com.bamboocloud.uros.common.UrosResultMode resultMode = (rm == null) ? com.bamboocloud.uros.common.UrosResultMode.Normal : rm.value();
		UrosSimpleMode sm = method.getAnnotation(UrosSimpleMode.class);
		final boolean simple = (sm == null) ? false : sm.value();
		UrosByRef byref = method.getAnnotation(UrosByRef.class);
		final boolean byRef = (byref == null) ? false : byref.value();
		Type[] paramTypes = method.getGenericParameterTypes();
		Type returnType = method.getGenericReturnType();
		if (void.class.equals(returnType) || Void.class.equals(returnType)) {
			returnType = null;
		}
		int n = paramTypes.length;
		Object result = null;
		if ((n > 0) && UrosHelper.toClass(paramTypes[n - 1]).equals(UrosCallback1.class)) {
			if (paramTypes[n - 1] instanceof ParameterizedType) {
				returnType = ((ParameterizedType) paramTypes[n - 1]).getActualTypeArguments()[0];
			}
			UrosCallback1 callback = (UrosCallback1) arguments[n - 1];
			Object[] tmpargs = new Object[n - 1];
			System.arraycopy(arguments, 0, tmpargs, 0, n - 1);
			this.client.invoke(functionName, tmpargs, callback, null, returnType, resultMode, simple);
		} else if ((n > 0) && UrosHelper.toClass(paramTypes[n - 1]).equals(UrosCallback.class)) {
			if (paramTypes[n - 1] instanceof ParameterizedType) {
				returnType = ((ParameterizedType) paramTypes[n - 1]).getActualTypeArguments()[0];
			}
			UrosCallback callback = (UrosCallback) arguments[n - 1];
			Object[] tmpargs = new Object[n - 1];
			System.arraycopy(arguments, 0, tmpargs, 0, n - 1);
			this.client.invoke(functionName, tmpargs, callback, null, returnType, byRef, resultMode, simple);
		} else if ((n > 1) && UrosHelper.toClass(paramTypes[n - 2]).equals(UrosCallback1.class)
				&& UrosHelper.toClass(paramTypes[n - 1]).equals(UrosErrorEvent.class)) {
			if (paramTypes[n - 2] instanceof ParameterizedType) {
				returnType = ((ParameterizedType) paramTypes[n - 2]).getActualTypeArguments()[0];
			}
			UrosCallback1 callback = (UrosCallback1) arguments[n - 2];
			UrosErrorEvent errorEvent = (UrosErrorEvent) arguments[n - 1];
			Object[] tmpargs = new Object[n - 2];
			System.arraycopy(arguments, 0, tmpargs, 0, n - 2);
			this.client.invoke(functionName, tmpargs, callback, errorEvent, returnType, resultMode, simple);
		} else if ((n > 1) && UrosHelper.toClass(paramTypes[n - 2]).equals(UrosCallback.class)
				&& UrosHelper.toClass(paramTypes[n - 1]).equals(UrosErrorEvent.class)) {
			if (paramTypes[n - 2] instanceof ParameterizedType) {
				returnType = ((ParameterizedType) paramTypes[n - 2]).getActualTypeArguments()[0];
			}
			UrosCallback callback = (UrosCallback) arguments[n - 2];
			UrosErrorEvent errorEvent = (UrosErrorEvent) arguments[n - 1];
			Object[] tmpargs = new Object[n - 2];
			System.arraycopy(arguments, 0, tmpargs, 0, n - 2);
			this.client.invoke(functionName, tmpargs, callback, errorEvent, returnType, byRef, resultMode, simple);
		} else {
			result = this.client.invoke(functionName, arguments, returnType, byRef, resultMode, simple);
		}
		return result;
	}
}

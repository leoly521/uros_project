package com.bamboocloud.uros.common;

import java.io.IOException;
import java.lang.reflect.Type;

import com.bamboocloud.uros.client.api.UrosCallback;
import com.bamboocloud.uros.client.api.UrosCallback1;

public interface UrosInvoker {
	void invoke(String functionName, UrosCallback1<?> callback);
	void invoke(String functionName, UrosCallback1<?> callback, UrosErrorEvent errorEvent);

	void invoke(String functionName, UrosCallback1<?> callback, UrosResultMode resultMode);
	void invoke(String functionName, UrosCallback1<?> callback, UrosErrorEvent errorEvent, UrosResultMode resultMode);

	void invoke(String functionName, UrosCallback1<?> callback, boolean simple);
	void invoke(String functionName, UrosCallback1<?> callback, UrosErrorEvent errorEvent, boolean simple);

	void invoke(String functionName, UrosCallback1<?> callback, UrosResultMode resultMode, boolean simple);
	void invoke(String functionName, UrosCallback1<?> callback, UrosErrorEvent errorEvent, UrosResultMode resultMode, boolean simple);

	void invoke(String functionName, Object[] arguments, UrosCallback1<?> callback);
	void invoke(String functionName, Object[] arguments, UrosCallback1<?> callback, UrosErrorEvent errorEvent);

	void invoke(String functionName, Object[] arguments, UrosCallback1<?> callback, UrosResultMode resultMode);
	void invoke(String functionName, Object[] arguments, UrosCallback1<?> callback, UrosErrorEvent errorEvent, UrosResultMode resultMode);

	void invoke(String functionName, Object[] arguments, UrosCallback1<?> callback, boolean simple);
	void invoke(String functionName, Object[] arguments, UrosCallback1<?> callback, UrosErrorEvent errorEvent, boolean simple);

	void invoke(String functionName, Object[] arguments, UrosCallback1<?> callback, UrosResultMode resultMode, boolean simple);
	void invoke(String functionName, Object[] arguments, UrosCallback1<?> callback, UrosErrorEvent errorEvent, UrosResultMode resultMode, boolean simple);

	<T> void invoke(String functionName, UrosCallback1<T> callback, Class<T> returnType);
	<T> void invoke(String functionName, UrosCallback1<T> callback, UrosErrorEvent errorEvent, Class<T> returnType);

	<T> void invoke(String functionName, UrosCallback1<T> callback, Class<T> returnType, UrosResultMode resultMode);
	<T> void invoke(String functionName, UrosCallback1<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, UrosResultMode resultMode);

	<T> void invoke(String functionName, UrosCallback1<T> callback, Class<T> returnType, boolean simple);
	<T> void invoke(String functionName, UrosCallback1<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, boolean simple);

	<T> void invoke(String functionName, UrosCallback1<T> callback, Class<T> returnType, UrosResultMode resultMode, boolean simple);
	<T> void invoke(String functionName, UrosCallback1<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, UrosResultMode resultMode, boolean simple);

	<T> void invoke(String functionName, Object[] arguments, UrosCallback1<T> callback, Class<T> returnType);
	<T> void invoke(String functionName, Object[] arguments, UrosCallback1<T> callback, UrosErrorEvent errorEvent, Class<T> returnType);

	<T> void invoke(String functionName, Object[] arguments, UrosCallback1<T> callback, Class<T> returnType, UrosResultMode resultMode);
	<T> void invoke(String functionName, Object[] arguments, UrosCallback1<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, UrosResultMode resultMode);

	<T> void invoke(String functionName, Object[] arguments, UrosCallback1<T> callback, Class<T> returnType, boolean simple);
	<T> void invoke(String functionName, Object[] arguments, UrosCallback1<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, boolean simple);

	<T> void invoke(String functionName, Object[] arguments, UrosCallback1<T> callback, Class<T> returnType, UrosResultMode resultMode, boolean simple);
	<T> void invoke(String functionName, Object[] arguments, UrosCallback1<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, UrosResultMode resultMode, boolean simple);

	void invoke(String functionName, Object[] arguments, UrosCallback1<?> callback, UrosErrorEvent errorEvent, Type returnType, UrosResultMode resultMode, boolean simple);

	void invoke(String functionName, Object[] arguments, UrosCallback<?> callback);
	void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, UrosErrorEvent errorEvent);
	void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, boolean byRef);
	void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, UrosErrorEvent errorEvent, boolean byRef);

	void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, boolean byRef, boolean simple);
	void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, UrosErrorEvent errorEvent, boolean byRef, boolean simple);

	void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, UrosResultMode resultMode);
	void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, UrosErrorEvent errorEvent, UrosResultMode resultMode);
	void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, boolean byRef, UrosResultMode resultMode);
	void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, UrosErrorEvent errorEvent, boolean byRef, UrosResultMode resultMode);

	void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, UrosResultMode resultMode, boolean simple);
	void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, UrosErrorEvent errorEvent, UrosResultMode resultMode, boolean simple);
	void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, boolean byRef, UrosResultMode resultMode, boolean simple);
	void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, UrosErrorEvent errorEvent, boolean byRef, UrosResultMode resultMode, boolean simple);

	<T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, Class<T> returnType);
	<T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, UrosErrorEvent errorEvent, Class<T> returnType);
	<T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, Class<T> returnType, boolean byRef);
	<T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, boolean byRef);

	<T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, Class<T> returnType, boolean byRef, boolean simple);
	<T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, boolean byRef, boolean simple);

	<T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, Class<T> returnType, UrosResultMode resultMode);
	<T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, UrosResultMode resultMode);
	<T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, Class<T> returnType, boolean byRef, UrosResultMode resultMode);
	<T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, boolean byRef, UrosResultMode resultMode);

	<T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, Class<T> returnType, UrosResultMode resultMode, boolean simple);
	<T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, UrosResultMode resultMode, boolean simple);
	<T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, Class<T> returnType, boolean byRef, UrosResultMode resultMode, boolean simple);
	<T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, boolean byRef, UrosResultMode resultMode, boolean simple);

	void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, UrosErrorEvent errorEvent, Type returnType, boolean byRef, UrosResultMode resultMode, boolean simple);

	Object invoke(String functionName) throws IOException;
	Object invoke(String functionName, Object[] arguments) throws IOException;
	Object invoke(String functionName, Object[] arguments, boolean byRef) throws IOException;

	Object invoke(String functionName, boolean simple) throws IOException;
	Object invoke(String functionName, Object[] arguments, boolean byRef, boolean simple) throws IOException;

	Object invoke(String functionName, UrosResultMode resultMode) throws IOException;
	Object invoke(String functionName, Object[] arguments, UrosResultMode resultMode) throws IOException;
	Object invoke(String functionName, Object[] arguments, boolean byRef, UrosResultMode resultMode) throws IOException;

	Object invoke(String functionName, UrosResultMode resultMode, boolean simple) throws IOException;
	Object invoke(String functionName, Object[] arguments, UrosResultMode resultMode, boolean simple) throws IOException;
	Object invoke(String functionName, Object[] arguments, boolean byRef, UrosResultMode resultMode, boolean simple) throws IOException;

	<T> T invoke(String functionName, Class<T> returnType) throws IOException;
	<T> T invoke(String functionName, Object[] arguments, Class<T> returnType) throws IOException;
	<T> T invoke(String functionName, Object[] arguments, Class<T> returnType, boolean byRef) throws IOException;

	<T> T invoke(String functionName, Class<T> returnType, boolean simple) throws IOException;
	<T> T invoke(String functionName, Object[] arguments, Class<T> returnType, boolean byRef, boolean simple) throws IOException;

	<T> T invoke(String functionName, Class<T> returnType, UrosResultMode resultMode) throws IOException;
	<T> T invoke(String functionName, Object[] arguments, Class<T> returnType, UrosResultMode resultMode) throws IOException;
	<T> T invoke(String functionName, Object[] arguments, Class<T> returnType, boolean byRef, UrosResultMode resultMode) throws IOException;

	<T> T invoke(String functionName, Class<T> returnType, UrosResultMode resultMode, boolean simple) throws IOException;
	<T> T invoke(String functionName, Object[] arguments, Class<T> returnType, UrosResultMode resultMode, boolean simple) throws IOException;
	<T> T invoke(String functionName, Object[] arguments, Class<T> returnType, boolean byRef, UrosResultMode resultMode, boolean simple) throws IOException;

	Object invoke(String functionName, Object[] arguments, Type returnType, boolean byRef, UrosResultMode resultMode, boolean simple) throws IOException;
}
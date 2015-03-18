package com.bamboocloud.uros.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.bamboocloud.uros.common.UrosFilter;
import com.bamboocloud.uros.common.UrosResultMode;
import com.bamboocloud.uros.common.exception.UrosProtocolException;
import com.bamboocloud.uros.io.ByteBufferStream;
import com.bamboocloud.uros.io.UrosHelper;
import com.bamboocloud.uros.io.UrosMode;
import com.bamboocloud.uros.io.UrosReader;
import com.bamboocloud.uros.io.UrosTags;
import com.bamboocloud.uros.io.UrosWriter;

public abstract class UrosService {

	private final ArrayList<UrosFilter> filters = new ArrayList<UrosFilter>();
	private UrosMode mode = UrosMode.MemberMode;
	private boolean debug = false;
	protected UrosServiceEvent event = null;
	protected UrosMethods globalMethods = null;
	private static final ThreadLocal<Object> currentContext = new ThreadLocal<Object>();

	public static Object getCurrentContext() {
		return currentContext.get();
	}

	public UrosMethods getGlobalMethods() {
		if (this.globalMethods == null) {
			this.globalMethods = new UrosMethods();
		}
		return this.globalMethods;
	}

	public void setGlobalMethods(UrosMethods globalMethods) {
		this.globalMethods = globalMethods;
	}

	public UrosMode getMode() {
		return this.mode;
	}

	public void setMode(UrosMode mode) {
		this.mode = mode;
	}

	public boolean isDebug() {
		return this.debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public UrosServiceEvent getEvent() {
		return this.event;
	}

	public void setEvent(UrosServiceEvent event) {
		this.event = event;
	}

	public UrosFilter getFilter() {
		if (this.filters.isEmpty()) {
			return null;
		}
		return this.filters.get(0);
	}

	public void setFilter(UrosFilter filter) {
		if (!this.filters.isEmpty()) {
			this.filters.clear();
		}
		if (filter != null) {
			this.filters.add(filter);
		}
	}

	public void addFilter(UrosFilter filter) {
		this.filters.add(filter);
	}

	public boolean removeFilter(UrosFilter filter) {
		return this.filters.remove(filter);
	}

	public void add(Method method, Object obj, String aliasName) {
		this.getGlobalMethods().addMethod(method, obj, aliasName);
	}

	public void add(Method method, Object obj, String aliasName,
			UrosResultMode mode) {
		this.getGlobalMethods().addMethod(method, obj, aliasName, mode);
	}

	public void add(Method method, Object obj, String aliasName, boolean simple) {
		this.getGlobalMethods().addMethod(method, obj, aliasName, simple);
	}

	public void add(Method method, Object obj, String aliasName,
			UrosResultMode mode, boolean simple) {
		this.getGlobalMethods().addMethod(method, obj, aliasName, mode, simple);
	}

	public void add(String methodName, Object obj, Class<?>[] paramTypes,
			String aliasName) throws NoSuchMethodException {
		this.getGlobalMethods().addMethod(methodName, obj, paramTypes, aliasName);
	}

	public void add(String methodName, Object obj, Class<?>[] paramTypes,
			String aliasName, UrosResultMode mode) throws NoSuchMethodException {
		this.getGlobalMethods().addMethod(methodName, obj, paramTypes, aliasName,
				mode);
	}

	public void add(String methodName, Object obj, Class<?>[] paramTypes,
			String aliasName, boolean simple) throws NoSuchMethodException {
		this.getGlobalMethods().addMethod(methodName, obj, paramTypes, aliasName,
				simple);
	}

	public void add(String methodName, Object obj, Class<?>[] paramTypes,
			String aliasName, UrosResultMode mode, boolean simple)
			throws NoSuchMethodException {
		this.getGlobalMethods().addMethod(methodName, obj, paramTypes, aliasName,
				mode, simple);
	}

	public void add(String methodName, Class<?> type, Class<?>[] paramTypes,
			String aliasName) throws NoSuchMethodException {
		this.getGlobalMethods().addMethod(methodName, type, paramTypes, aliasName);
	}

	public void add(String methodName, Class<?> type, Class<?>[] paramTypes,
			String aliasName, UrosResultMode mode) throws NoSuchMethodException {
		this.getGlobalMethods().addMethod(methodName, type, paramTypes, aliasName,
				mode);
	}

	public void add(String methodName, Class<?> type, Class<?>[] paramTypes,
			String aliasName, boolean simple) throws NoSuchMethodException {
		this.getGlobalMethods().addMethod(methodName, type, paramTypes, aliasName,
				simple);
	}

	public void add(String methodName, Class<?> type, Class<?>[] paramTypes,
			String aliasName, UrosResultMode mode, boolean simple)
			throws NoSuchMethodException {
		this.getGlobalMethods().addMethod(methodName, type, paramTypes, aliasName,
				mode, simple);
	}

	public void add(String methodName, Object obj, Class<?>[] paramTypes)
			throws NoSuchMethodException {
		this.getGlobalMethods().addMethod(methodName, obj, paramTypes);
	}

	public void add(String methodName, Object obj, Class<?>[] paramTypes,
			UrosResultMode mode) throws NoSuchMethodException {
		this.getGlobalMethods().addMethod(methodName, obj, paramTypes, mode);
	}

	public void add(String methodName, Object obj, Class<?>[] paramTypes,
			boolean simple) throws NoSuchMethodException {
		this.getGlobalMethods().addMethod(methodName, obj, paramTypes, simple);
	}

	public void add(String methodName, Object obj, Class<?>[] paramTypes,
			UrosResultMode mode, boolean simple) throws NoSuchMethodException {
		this.getGlobalMethods().addMethod(methodName, obj, paramTypes, mode, simple);
	}

	public void add(String methodName, Class<?> type, Class<?>[] paramTypes)
			throws NoSuchMethodException {
		this.getGlobalMethods().addMethod(methodName, type, paramTypes);
	}

	public void add(String methodName, Class<?> type, Class<?>[] paramTypes,
			UrosResultMode mode) throws NoSuchMethodException {
		this.getGlobalMethods().addMethod(methodName, type, paramTypes, mode);
	}

	public void add(String methodName, Class<?> type, Class<?>[] paramTypes,
			boolean simple) throws NoSuchMethodException {
		this.getGlobalMethods().addMethod(methodName, type, paramTypes, simple);
	}

	public void add(String methodName, Class<?> type, Class<?>[] paramTypes,
			UrosResultMode mode, boolean simple) throws NoSuchMethodException {
		this.getGlobalMethods()
				.addMethod(methodName, type, paramTypes, mode, simple);
	}

	public void add(String methodName, Object obj, String aliasName) {
		this.getGlobalMethods().addMethod(methodName, obj, aliasName);
	}

	public void add(String methodName, Object obj, String aliasName,
			UrosResultMode mode) {
		this.getGlobalMethods().addMethod(methodName, obj, aliasName, mode);
	}

	public void add(String methodName, Object obj, String aliasName,
			boolean simple) {
		this.getGlobalMethods().addMethod(methodName, obj, aliasName, simple);
	}

	public void add(String methodName, Object obj, String aliasName,
			UrosResultMode mode, boolean simple) {
		this.getGlobalMethods().addMethod(methodName, obj, aliasName, mode, simple);
	}

	public void add(String methodName, Class<?> type, String aliasName) {
		this.getGlobalMethods().addMethod(methodName, type, aliasName);
	}

	public void add(String methodName, Class<?> type, String aliasName,
			UrosResultMode mode) {
		this.getGlobalMethods().addMethod(methodName, type, aliasName, mode);
	}

	public void add(String methodName, Class<?> type, String aliasName,
			boolean simple) {
		this.getGlobalMethods().addMethod(methodName, type, aliasName, simple);
	}

	public void add(String methodName, Class<?> type, String aliasName,
			UrosResultMode mode, boolean simple) {
		this.getGlobalMethods().addMethod(methodName, type, aliasName, mode, simple);
	}

	public void add(String methodName, Object obj) {
		this.getGlobalMethods().addMethod(methodName, obj);
	}

	public void add(String methodName, Object obj, UrosResultMode mode) {
		this.getGlobalMethods().addMethod(methodName, obj, mode);
	}

	public void add(String methodName, Object obj, boolean simple) {
		this.getGlobalMethods().addMethod(methodName, obj, simple);
	}

	public void add(String methodName, Object obj, UrosResultMode mode,
			boolean simple) {
		this.getGlobalMethods().addMethod(methodName, obj, mode, simple);
	}

	public void add(String methodName, Class<?> type) {
		this.getGlobalMethods().addMethod(methodName, type);
	}

	public void add(String methodName, Class<?> type, UrosResultMode mode) {
		this.getGlobalMethods().addMethod(methodName, type, mode);
	}

	public void add(String methodName, Class<?> type, boolean simple) {
		this.getGlobalMethods().addMethod(methodName, type, simple);
	}

	public void add(String methodName, Class<?> type, UrosResultMode mode,
			boolean simple) {
		this.getGlobalMethods().addMethod(methodName, type, mode, simple);
	}

	public void add(String[] methodNames, Object obj, String[] aliasNames) {
		this.getGlobalMethods().addMethods(methodNames, obj, aliasNames);
	}

	public void add(String[] methodNames, Object obj, String[] aliasNames,
			UrosResultMode mode) {
		this.getGlobalMethods().addMethods(methodNames, obj, aliasNames, mode);
	}

	public void add(String[] methodNames, Object obj, String[] aliasNames,
			boolean simple) {
		this.getGlobalMethods().addMethods(methodNames, obj, aliasNames, simple);
	}

	public void add(String[] methodNames, Object obj, String[] aliasNames,
			UrosResultMode mode, boolean simple) {
		this.getGlobalMethods().addMethods(methodNames, obj, aliasNames, mode,
				simple);
	}

	public void add(String[] methodNames, Object obj, String aliasPrefix) {
		this.getGlobalMethods().addMethods(methodNames, obj, aliasPrefix);
	}

	public void add(String[] methodNames, Object obj, String aliasPrefix,
			UrosResultMode mode) {
		this.getGlobalMethods().addMethods(methodNames, obj, aliasPrefix, mode);
	}

	public void add(String[] methodNames, Object obj, String aliasPrefix,
			boolean simple) {
		this.getGlobalMethods().addMethods(methodNames, obj, aliasPrefix, simple);
	}

	public void add(String[] methodNames, Object obj, String aliasPrefix,
			UrosResultMode mode, boolean simple) {
		this.getGlobalMethods().addMethods(methodNames, obj, aliasPrefix, mode,
				simple);
	}

	public void add(String[] methodNames, Object obj) {
		this.getGlobalMethods().addMethods(methodNames, obj);
	}

	public void add(String[] methodNames, Object obj, UrosResultMode mode) {
		this.getGlobalMethods().addMethods(methodNames, obj, mode);
	}

	public void add(String[] methodNames, Object obj, boolean simple) {
		this.getGlobalMethods().addMethods(methodNames, obj, simple);
	}

	public void add(String[] methodNames, Object obj, UrosResultMode mode,
			boolean simple) {
		this.getGlobalMethods().addMethods(methodNames, obj, mode, simple);
	}

	public void add(String[] methodNames, Class<?> type, String[] aliasNames) {
		this.getGlobalMethods().addMethods(methodNames, type, aliasNames);
	}

	public void add(String[] methodNames, Class<?> type, String[] aliasNames,
			UrosResultMode mode) {
		this.getGlobalMethods().addMethods(methodNames, type, aliasNames, mode);
	}

	public void add(String[] methodNames, Class<?> type, String[] aliasNames,
			boolean simple) {
		this.getGlobalMethods().addMethods(methodNames, type, aliasNames, simple);
	}

	public void add(String[] methodNames, Class<?> type, String[] aliasNames,
			UrosResultMode mode, boolean simple) {
		this.getGlobalMethods().addMethods(methodNames, type, aliasNames, mode,
				simple);
	}

	public void add(String[] methodNames, Class<?> type, String aliasPrefix) {
		this.getGlobalMethods().addMethods(methodNames, type, aliasPrefix);
	}

	public void add(String[] methodNames, Class<?> type, String aliasPrefix,
			UrosResultMode mode) {
		this.getGlobalMethods().addMethods(methodNames, type, aliasPrefix, mode);
	}

	public void add(String[] methodNames, Class<?> type, String aliasPrefix,
			boolean simple) {
		this.getGlobalMethods().addMethods(methodNames, type, aliasPrefix, simple);
	}

	public void add(String[] methodNames, Class<?> type, String aliasPrefix,
			UrosResultMode mode, boolean simple) {
		this.getGlobalMethods().addMethods(methodNames, type, aliasPrefix, mode,
				simple);
	}

	public void add(String[] methodNames, Class<?> type) {
		this.getGlobalMethods().addMethods(methodNames, type);
	}

	public void add(String[] methodNames, Class<?> type, UrosResultMode mode) {
		this.getGlobalMethods().addMethods(methodNames, type, mode);
	}

	public void add(String[] methodNames, Class<?> type, boolean simple) {
		this.getGlobalMethods().addMethods(methodNames, type, simple);
	}

	public void add(String[] methodNames, Class<?> type, UrosResultMode mode,
			boolean simple) {
		this.getGlobalMethods().addMethods(methodNames, type, mode, simple);
	}

	public void add(Object obj, Class<?> type, String aliasPrefix) {
		this.getGlobalMethods().addInstanceMethods(obj, type, aliasPrefix);
	}

	public void add(Object obj, Class<?> type, String aliasPrefix,
			UrosResultMode mode) {
		this.getGlobalMethods().addInstanceMethods(obj, type, aliasPrefix, mode);
	}

	public void add(Object obj, Class<?> type, String aliasPrefix,
			boolean simple) {
		this.getGlobalMethods().addInstanceMethods(obj, type, aliasPrefix, simple);
	}

	public void add(Object obj, Class<?> type, String aliasPrefix,
			UrosResultMode mode, boolean simple) {
		this.getGlobalMethods().addInstanceMethods(obj, type, aliasPrefix, mode,
				simple);
	}

	public void add(Object obj, Class<?> type) {
		this.getGlobalMethods().addInstanceMethods(obj, type);
	}

	public void add(Object obj, Class<?> type, UrosResultMode mode) {
		this.getGlobalMethods().addInstanceMethods(obj, type, mode);
	}

	public void add(Object obj, Class<?> type, boolean simple) {
		this.getGlobalMethods().addInstanceMethods(obj, type, simple);
	}

	public void add(Object obj, Class<?> type, UrosResultMode mode,
			boolean simple) {
		this.getGlobalMethods().addInstanceMethods(obj, type, mode, simple);
	}

	public void add(Object obj, String aliasPrefix) {
		this.getGlobalMethods().addInstanceMethods(obj, aliasPrefix);
	}

	public void add(Object obj, String aliasPrefix, UrosResultMode mode) {
		this.getGlobalMethods().addInstanceMethods(obj, aliasPrefix, mode);
	}

	public void add(Object obj, String aliasPrefix, boolean simple) {
		this.getGlobalMethods().addInstanceMethods(obj, aliasPrefix, simple);
	}

	public void add(Object obj, String aliasPrefix, UrosResultMode mode,
			boolean simple) {
		this.getGlobalMethods().addInstanceMethods(obj, aliasPrefix, mode, simple);
	}

	public void add(Object obj) {
		this.getGlobalMethods().addInstanceMethods(obj);
	}

	public void add(Object obj, UrosResultMode mode) {
		this.getGlobalMethods().addInstanceMethods(obj, mode);
	}

	public void add(Object obj, boolean simple) {
		this.getGlobalMethods().addInstanceMethods(obj, simple);
	}

	public void add(Object obj, UrosResultMode mode, boolean simple) {
		this.getGlobalMethods().addInstanceMethods(obj, mode, simple);
	}

	public void add(Class<?> type, String aliasPrefix) {
		this.getGlobalMethods().addStaticMethods(type, aliasPrefix);
	}

	public void add(Class<?> type, String aliasPrefix, UrosResultMode mode) {
		this.getGlobalMethods().addStaticMethods(type, aliasPrefix, mode);
	}

	public void add(Class<?> type, String aliasPrefix, boolean simple) {
		this.getGlobalMethods().addStaticMethods(type, aliasPrefix, simple);
	}

	public void add(Class<?> type, String aliasPrefix, UrosResultMode mode,
			boolean simple) {
		this.getGlobalMethods().addStaticMethods(type, aliasPrefix, mode, simple);
	}

	public void add(Class<?> type) {
		this.getGlobalMethods().addStaticMethods(type);
	}

	public void add(Class<?> type, UrosResultMode mode) {
		this.getGlobalMethods().addStaticMethods(type, mode);
	}

	public void add(Class<?> type, boolean simple) {
		this.getGlobalMethods().addStaticMethods(type, simple);
	}

	public void add(Class<?> type, UrosResultMode mode, boolean simple) {
		this.getGlobalMethods().addStaticMethods(type, mode, simple);
	}

	public void addMissingMethod(String methodName, Object obj)
			throws NoSuchMethodException {
		this.getGlobalMethods().addMissingMethod(methodName, obj);
	}

	public void addMissingMethod(String methodName, Object obj,
			UrosResultMode mode) throws NoSuchMethodException {
		this.getGlobalMethods().addMissingMethod(methodName, obj, mode);
	}

	public void addMissingMethod(String methodName, Object obj, boolean simple)
			throws NoSuchMethodException {
		this.getGlobalMethods().addMissingMethod(methodName, obj, simple);
	}

	public void addMissingMethod(String methodName, Object obj,
			UrosResultMode mode, boolean simple) throws NoSuchMethodException {
		this.getGlobalMethods().addMissingMethod(methodName, obj, mode, simple);
	}

	public void addMissingMethod(String methodName, Class<?> type)
			throws NoSuchMethodException {
		this.getGlobalMethods().addMissingMethod(methodName, type);
	}

	public void addMissingMethod(String methodName, Class<?> type,
			UrosResultMode mode) throws NoSuchMethodException {
		this.getGlobalMethods().addMissingMethod(methodName, type, mode);
	}

	public void addMissingMethod(String methodName, Class<?> type,
			boolean simple) throws NoSuchMethodException {
		this.getGlobalMethods().addMissingMethod(methodName, type, simple);
	}

	public void addMissingMethod(String methodName, Class<?> type,
			UrosResultMode mode, boolean simple) throws NoSuchMethodException {
		this.getGlobalMethods().addMissingMethod(methodName, type, mode, simple);
	}

	private ByteBufferStream responseEnd(ByteBufferStream data, Object context)
			throws IOException {
		data.flip();
		for (int i = 0, n = this.filters.size(); i < n; ++i) {
			data.buffer = this.filters.get(i).outputFilter(data.buffer, context);
			data.flip();
		}
		return data;
	}

	protected Object[] fixArguments(Type[] argumentTypes, Object[] arguments,
			int count, Object context) {
		return arguments;
	}

	private String getErrorMessage(Throwable e) {
		if (this.debug) {
			StackTraceElement[] st = e.getStackTrace();
			StringBuffer es = new StringBuffer(e.toString()).append("\r\n");
			for (int i = 0, n = st.length; i < n; ++i) {
				es.append(st[i].toString()).append("\r\n");
			}
			return es.toString();
		}
		return e.toString();
	}

	protected ByteBufferStream sendError(Throwable e, Object context)
			throws IOException {
		if (this.event != null) {
			this.event.onSendError(e, context);
		}
		ByteBufferStream data = new ByteBufferStream();
		UrosWriter writer = new UrosWriter(data.getOutputStream(), this.mode, true);
		data.write(UrosTags.TagError);
		writer.writeString(getErrorMessage(e));
		data.write(UrosTags.TagEnd);
		return responseEnd(data, context);
	}

	protected ByteBufferStream doInvoke(ByteBufferStream stream,
			UrosMethods methods, Object context) throws Throwable {
		UrosReader reader = new UrosReader(stream.getInputStream(), this.mode);
		ByteBufferStream data = new ByteBufferStream();
		int tag;
		do {
			reader.reset();
			String name = reader.readString();
			String aliasname = name.toLowerCase();
			UrosMethod remoteMethod = null;
			int count = 0;
			Object[] args, arguments;
			boolean byRef = false;
			tag = reader.checkTags((char) UrosTags.TagList + ""
					+ (char) UrosTags.TagEnd + "" + (char) UrosTags.TagCall);
			if (tag == UrosTags.TagList) {
				reader.reset();
				count = reader.readInt(UrosTags.TagOpenbrace);
				if (methods != null) {
					remoteMethod = methods.get(aliasname, count);
				}
				if (remoteMethod == null) {
					remoteMethod = getGlobalMethods().get(aliasname, count);
				}
				if (remoteMethod == null) {
					arguments = reader.readArray(count);
				} else {
					arguments = new Object[count];
					reader.readArray(remoteMethod.paramTypes, arguments, count);
				}
				tag = reader
						.checkTags((char) UrosTags.TagTrue + ""
								+ (char) UrosTags.TagEnd + ""
								+ (char) UrosTags.TagCall);
				if (tag == UrosTags.TagTrue) {
					byRef = true;
					tag = reader.checkTags((char) UrosTags.TagEnd + ""
							+ (char) UrosTags.TagCall);
				}
			} else {
				if (methods != null) {
					remoteMethod = methods.get(aliasname, 0);
				}
				if (remoteMethod == null) {
					remoteMethod = getGlobalMethods().get(aliasname, 0);
				}
				arguments = new Object[0];
			}
			if (this.event != null) {
				this.event.onBeforeInvoke(name, arguments, byRef, context);
			}
			if (remoteMethod == null) {
				args = arguments;
			} else {
				args = fixArguments(remoteMethod.paramTypes, arguments, count,
						context);
			}
			Object result;
			try {
				if (remoteMethod == null) {
					if (methods != null) {
						remoteMethod = methods.get("*", 2);
					}
					if (remoteMethod == null) {
						remoteMethod = getGlobalMethods().get("*", 2);
					}
					if (remoteMethod == null) {
						throw new NoSuchMethodError("Can't find this method "
								+ name);
					}
					result = remoteMethod.method.invoke(remoteMethod.obj,
							new Object[] { name, args });
				} else {
					result = remoteMethod.method.invoke(remoteMethod.obj, args);
				}
			} catch (ExceptionInInitializerError ex1) {
				Throwable e = ex1.getCause();
				if (e != null) {
					throw e;
				}
				throw ex1;
			} catch (InvocationTargetException ex2) {
				Throwable e = ex2.getCause();
				if (e != null) {
					throw e;
				}
				throw ex2;
			}
			if (byRef) {
				System.arraycopy(args, 0, arguments, 0, count);
			}
			if (this.event != null) {
				this.event.onAfterInvoke(name, arguments, byRef, result, context);
			}
			if (remoteMethod.mode == UrosResultMode.RawWithEndTag) {
				data.write((byte[]) result);
				return responseEnd(data, context);
			} else if (remoteMethod.mode == UrosResultMode.Raw) {
				data.write((byte[]) result);
			} else {
				data.write(UrosTags.TagResult);
				boolean simple = remoteMethod.simple;
				UrosWriter writer = new UrosWriter(data.getOutputStream(),
						this.mode, simple);
				if (remoteMethod.mode == UrosResultMode.Serialized) {
					data.write((byte[]) result);
				} else {
					writer.serialize(result);
				}
				if (byRef) {
					data.write(UrosTags.TagArgument);
					writer.reset();
					writer.writeArray(arguments);
				}
			}
		} while (tag == UrosTags.TagCall);
		data.write(UrosTags.TagEnd);
		return responseEnd(data, context);
	}

	protected ByteBufferStream doFunctionList(UrosMethods methods,
			Object context) throws IOException {
		ArrayList<String> names = new ArrayList<String>();
		names.addAll(getGlobalMethods().getAllNames());
		if (methods != null) {
			names.addAll(methods.getAllNames());
		}
		ByteBufferStream data = new ByteBufferStream();
		UrosWriter writer = new UrosWriter(data.getOutputStream(), this.mode, true);
		data.write(UrosTags.TagFunctions);
		writer.writeList(names);
		data.write(UrosTags.TagEnd);
		return responseEnd(data, context);
	}

	protected void fireErrorEvent(Throwable e, Object context) {
		if (this.event != null) {
			this.event.onSendError(e, context);
		}
	}

	protected ByteBufferStream handle(ByteBufferStream stream, Object context)
			throws IOException {
		return handle(stream, null, context);
	}

	protected ByteBufferStream handle(ByteBufferStream stream,
			UrosMethods methods, Object context) throws IOException {
		try {
			currentContext.set(context);
			stream.flip();
			for (int i = this.filters.size() - 1; i >= 0; --i) {
				stream.buffer = this.filters.get(i).inputFilter(stream.buffer,
						context);
				stream.flip();
			}
			int tag = stream.read();
			switch (tag) {
			case UrosTags.TagCall:
				return doInvoke(stream, methods, context);
			case UrosTags.TagEnd:
				return doFunctionList(methods, context);
			default:
				return sendError(new UrosProtocolException("Wrong Request: \r\n"
						+ UrosHelper.readWrongInfo(stream)), context);
			}
		} catch (Throwable e) {
			return sendError(e, context);
		} finally {
			currentContext.remove();
		}
	}
}
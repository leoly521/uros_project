package com.bamboocloud.uros.client;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.bamboocloud.uros.client.api.UrosCallback;
import com.bamboocloud.uros.client.api.UrosCallback1;
import com.bamboocloud.uros.common.UrosErrorEvent;
import com.bamboocloud.uros.common.UrosFilter;
import com.bamboocloud.uros.common.UrosInvocationHandler;
import com.bamboocloud.uros.common.UrosInvoker;
import com.bamboocloud.uros.common.UrosResultMode;
import com.bamboocloud.uros.common.exception.UrosProtocolException;
import com.bamboocloud.uros.io.ByteBufferStream;
import com.bamboocloud.uros.io.UrosHelper;
import com.bamboocloud.uros.io.UrosMode;
import com.bamboocloud.uros.io.UrosReader;
import com.bamboocloud.uros.io.UrosTags;
import com.bamboocloud.uros.io.UrosWriter;

public abstract class UrosClient implements UrosInvoker {

	public static final int DEFAULT_CONNECT_TIMEOUT = 3000;
	public static final int DEFAULT_READ_TIMEOUT = -1;
	public static final int DEFAULT_IDLE_TIMEOUT = 300000;
	
	private final ExecutorService threadPool = Executors.newCachedThreadPool();

	private static final Object[] nullArgs = new Object[0];
	private final ArrayList<UrosFilter> filters = new ArrayList<UrosFilter>();
	private UrosMode mode;
	protected String[] uris;
	protected int lastUriIndex = 0;
	public UrosErrorEvent onError = null;

	protected UrosClient() {
		this(null, UrosMode.MemberMode);
	}

	protected UrosClient(String[] uris) {
		this(uris, UrosMode.MemberMode);
	}

	protected UrosClient(UrosMode mode) {
		this(null, mode);
	}

	protected UrosClient(String[] uris, UrosMode mode) {
		this.mode = mode;
		this.uris = uris;
	}
	
	public void close() {
		if (!threadPool.isShutdown()) {
			threadPool.shutdown();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

	private static final HashMap<String, Class<? extends UrosClient>> clientFactories = new HashMap<String, Class<? extends UrosClient>>();

	public static void registerClientFactory(String scheme, Class<? extends UrosClient> clientClass) {
		synchronized (clientFactories) {
			clientFactories.put(scheme, clientClass);
		}
	}

	static {
		registerClientFactory("tcp", UrosTcpClient.class);
		registerClientFactory("tcp4", UrosTcpClient.class);
		registerClientFactory("tcp6", UrosTcpClient.class);
		registerClientFactory("http", UrosHttpClient.class);
		registerClientFactory("https", UrosHttpClient.class);
	}

	public static UrosClient create(String uri) throws IOException, URISyntaxException {
		return create(uri, UrosMode.MemberMode);
	}

	public static UrosClient create(String uri, UrosMode mode) throws IOException, URISyntaxException {
		String scheme = (new URI(uri)).getScheme().toLowerCase();
		Class<? extends UrosClient> clientClass = clientFactories.get(scheme);
		if (clientClass != null) {
			try {
				UrosClient client = clientClass.newInstance();
				client.mode = mode;
				client.uris = uri.split(",");
				return client;
			} catch (Exception ex) {
				throw new UrosProtocolException("This client doesn't support " + scheme + " scheme.");
			}
		}
		throw new UrosProtocolException("This client doesn't support " + scheme + " scheme.");
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

	public void useService(String uri) {
		this.uris = uri.split(",");
	}

	public final <T> T useService(Class<T> type) {
		return this.useService(type, null);
	}

	public final <T> T useService(String uri, Class<T> type) {
		return this.useService(uri, type, null);
	}

	@SuppressWarnings("unchecked")
	public final <T> T useService(Class<T> type, String ns) {
		UrosInvocationHandler handler = new UrosInvocationHandler(this, ns);
		if (type.isInterface()) {
			return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, handler);
		} else {
			return (T) Proxy.newProxyInstance(type.getClassLoader(), type.getInterfaces(), handler);
		}
	}

	public final <T> T useService(String uri, Class<T> type, String ns) {
		this.useService(uri);
		return this.useService(type, ns);
	}

	public final void invoke(String functionName, UrosCallback1<?> callback) {
		this.invoke(functionName, nullArgs, callback, null, (Type) null, UrosResultMode.Normal, false);
	}

	public final void invoke(String functionName, UrosCallback1<?> callback, UrosErrorEvent errorEvent) {
		this.invoke(functionName, nullArgs, callback, errorEvent, (Type) null, UrosResultMode.Normal, false);
	}

	public final void invoke(String functionName, UrosCallback1<?> callback, UrosResultMode resultMode) {
		this.invoke(functionName, nullArgs, callback, null, (Type) null, resultMode, false);
	}

	public final void invoke(String functionName, UrosCallback1<?> callback, UrosErrorEvent errorEvent, UrosResultMode resultMode) {
		this.invoke(functionName, nullArgs, callback, errorEvent, (Type) null, resultMode, false);
	}

	public final void invoke(String functionName, UrosCallback1<?> callback, boolean simple) {
		this.invoke(functionName, nullArgs, callback, null, (Type) null, UrosResultMode.Normal, simple);
	}

	public final void invoke(String functionName, UrosCallback1<?> callback, UrosErrorEvent errorEvent, boolean simple) {
		this.invoke(functionName, nullArgs, callback, errorEvent, (Type) null, UrosResultMode.Normal, simple);
	}

	public final void invoke(String functionName, UrosCallback1<?> callback, UrosResultMode resultMode, boolean simple) {
		this.invoke(functionName, nullArgs, callback, null, (Type) null, resultMode, simple);
	}

	public final void invoke(String functionName, UrosCallback1<?> callback, UrosErrorEvent errorEvent, UrosResultMode resultMode, boolean simple) {
		this.invoke(functionName, nullArgs, callback, errorEvent, (Type) null, resultMode, simple);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback1<?> callback) {
		this.invoke(functionName, arguments, callback, null, (Type) null, UrosResultMode.Normal, false);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback1<?> callback, UrosErrorEvent errorEvent) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) null, UrosResultMode.Normal, false);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback1<?> callback, UrosResultMode resultMode) {
		this.invoke(functionName, arguments, callback, null, (Type) null, resultMode, false);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback1<?> callback, UrosErrorEvent errorEvent, UrosResultMode resultMode) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) null, resultMode, false);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback1<?> callback, boolean simple) {
		this.invoke(functionName, arguments, callback, null, (Type) null, UrosResultMode.Normal, simple);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback1<?> callback, UrosErrorEvent errorEvent, boolean simple) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) null, UrosResultMode.Normal, simple);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback1<?> callback, UrosResultMode resultMode, boolean simple) {
		this.invoke(functionName, arguments, callback, null, (Type) null, resultMode, simple);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback1<?> callback, UrosErrorEvent errorEvent, UrosResultMode resultMode, boolean simple) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) null, resultMode, simple);
	}

	public final <T> void invoke(String functionName, UrosCallback1<T> callback, Class<T> returnType) {
		this.invoke(functionName, nullArgs, callback, null, (Type) returnType, UrosResultMode.Normal, false);
	}

	public final <T> void invoke(String functionName, UrosCallback1<T> callback, UrosErrorEvent errorEvent, Class<T> returnType) {
		this.invoke(functionName, nullArgs, callback, errorEvent, (Type) returnType, UrosResultMode.Normal, false);
	}

	public final <T> void invoke(String functionName, UrosCallback1<T> callback, Class<T> returnType, UrosResultMode resultMode) {
		this.invoke(functionName, nullArgs, callback, null, (Type) returnType, resultMode, false);
	}

	public final <T> void invoke(String functionName, UrosCallback1<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, UrosResultMode resultMode) {
		this.invoke(functionName, nullArgs, callback, errorEvent, (Type) returnType, resultMode, false);
	}

	public final <T> void invoke(String functionName, UrosCallback1<T> callback, Class<T> returnType, boolean simple) {
		this.invoke(functionName, nullArgs, callback, null, (Type) returnType, UrosResultMode.Normal, simple);
	}

	public final <T> void invoke(String functionName, UrosCallback1<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, boolean simple) {
		this.invoke(functionName, nullArgs, callback, errorEvent, (Type) returnType, UrosResultMode.Normal, simple);
	}

	public final <T> void invoke(String functionName, UrosCallback1<T> callback, Class<T> returnType, UrosResultMode resultMode, boolean simple) {
		this.invoke(functionName, nullArgs, callback, null, (Type) returnType, resultMode, simple);
	}

	public final <T> void invoke(String functionName, UrosCallback1<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, UrosResultMode resultMode, boolean simple) {
		this.invoke(functionName, nullArgs, callback, errorEvent, (Type) returnType, resultMode, simple);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback1<T> callback, Class<T> returnType) {
		this.invoke(functionName, arguments, callback, null, (Type) returnType, UrosResultMode.Normal, false);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback1<T> callback, UrosErrorEvent errorEvent, Class<T> returnType) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) returnType, UrosResultMode.Normal, false);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback1<T> callback, Class<T> returnType, UrosResultMode resultMode) {
		this.invoke(functionName, arguments, callback, null, (Type) returnType, resultMode, false);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback1<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, UrosResultMode resultMode) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) returnType, resultMode, false);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback1<T> callback, Class<T> returnType, boolean simple) {
		this.invoke(functionName, arguments, callback, null, (Type) returnType, UrosResultMode.Normal, simple);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback1<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, boolean simple) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) returnType, UrosResultMode.Normal, simple);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback1<T> callback, Class<T> returnType, UrosResultMode resultMode, boolean simple) {
		this.invoke(functionName, arguments, callback, null, (Type) returnType, resultMode, simple);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback1<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, UrosResultMode resultMode, boolean simple) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) returnType, resultMode, simple);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void invoke(final String functionName, final Object[] arguments, final UrosCallback1 callback, final UrosErrorEvent errorEvent, final Type returnType, final UrosResultMode resultMode, final boolean simple) {
		this.threadPool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Object result = invoke(functionName, arguments, returnType, false, resultMode, simple);
					callback.handler(result);
				} catch (Exception ex) {
					if (errorEvent != null) {
						errorEvent.handler(functionName, ex);
					} else if (onError != null) {
						onError.handler(functionName, ex);
					}
				}
			}
		});
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback<?> callback) {
		this.invoke(functionName, arguments, callback, null, (Type) null, false, UrosResultMode.Normal, false);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, UrosErrorEvent errorEvent) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) null, false, UrosResultMode.Normal, false);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, boolean byRef) {
		this.invoke(functionName, arguments, callback, null, (Type) null, byRef, UrosResultMode.Normal, false);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, UrosErrorEvent errorEvent, boolean byRef) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) null, byRef, UrosResultMode.Normal, false);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, boolean byRef, boolean simple) {
		this.invoke(functionName, arguments, callback, null, (Type) null, byRef, UrosResultMode.Normal, simple);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, UrosErrorEvent errorEvent, boolean byRef, boolean simple) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) null, byRef, UrosResultMode.Normal, simple);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, UrosResultMode resultMode) {
		this.invoke(functionName, arguments, callback, null, (Type) null, false, resultMode, false);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, UrosErrorEvent errorEvent, UrosResultMode resultMode) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) null, false, resultMode, false);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, boolean byRef, UrosResultMode resultMode) {
		this.invoke(functionName, arguments, callback, null, (Type) null, byRef, resultMode, false);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, UrosErrorEvent errorEvent, boolean byRef, UrosResultMode resultMode) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) null, byRef, resultMode, false);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, UrosResultMode resultMode, boolean simple) {
		this.invoke(functionName, arguments, callback, null, (Type) null, false, resultMode, simple);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, UrosErrorEvent errorEvent, UrosResultMode resultMode, boolean simple) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) null, false, resultMode, simple);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, boolean byRef, UrosResultMode resultMode, boolean simple) {
		this.invoke(functionName, arguments, callback, null, (Type) null, byRef, resultMode, simple);
	}

	public final void invoke(String functionName, Object[] arguments, UrosCallback<?> callback, UrosErrorEvent errorEvent, boolean byRef, UrosResultMode resultMode, boolean simple) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) null, byRef, resultMode, simple);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, Class<T> returnType) {
		this.invoke(functionName, arguments, callback, null, (Type) returnType, false, UrosResultMode.Normal, false);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, UrosErrorEvent errorEvent, Class<T> returnType) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) returnType, false, UrosResultMode.Normal, false);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, Class<T> returnType, boolean byRef) {
		this.invoke(functionName, arguments, callback, null, (Type) returnType, byRef, UrosResultMode.Normal, false);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, boolean byRef) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) returnType, byRef, UrosResultMode.Normal, false);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, Class<T> returnType, boolean byRef, boolean simple) {
		this.invoke(functionName, arguments, callback, null, (Type) returnType, byRef, UrosResultMode.Normal, simple);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, boolean byRef, boolean simple) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) returnType, byRef, UrosResultMode.Normal, simple);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, Class<T> returnType, UrosResultMode resultMode) {
		this.invoke(functionName, arguments, callback, null, (Type) returnType, false, resultMode, false);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, UrosResultMode resultMode) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) returnType, false, resultMode, false);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, Class<T> returnType, boolean byRef, UrosResultMode resultMode) {
		this.invoke(functionName, arguments, callback, null, (Type) returnType, byRef, resultMode, false);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, boolean byRef, UrosResultMode resultMode) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) returnType, byRef, resultMode, false);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, Class<T> returnType, UrosResultMode resultMode, boolean simple) {
		this.invoke(functionName, arguments, callback, null, (Type) returnType, false, resultMode, simple);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, UrosResultMode resultMode, boolean simple) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) returnType, false, resultMode, simple);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, Class<T> returnType, boolean byRef, UrosResultMode resultMode, boolean simple) {
		this.invoke(functionName, arguments, callback, null, (Type) returnType, byRef, resultMode, simple);
	}

	public final <T> void invoke(String functionName, Object[] arguments, UrosCallback<T> callback, UrosErrorEvent errorEvent, Class<T> returnType, boolean byRef, UrosResultMode resultMode, boolean simple) {
		this.invoke(functionName, arguments, callback, errorEvent, (Type) returnType, byRef, resultMode, simple);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void invoke(final String functionName, final Object[] arguments, final UrosCallback callback, final UrosErrorEvent errorEvent, final Type returnType, final boolean byRef, final UrosResultMode resultMode, final boolean simple) {
		new Thread() {
			@Override
			public void run() {
				try {
					Object result = invoke(functionName, arguments, returnType, byRef, resultMode, simple);
					callback.handler(result, arguments);
				} catch (Exception ex) {
					if (errorEvent != null) {
						errorEvent.handler(functionName, ex);
					} else if (onError != null) {
						onError.handler(functionName, ex);
					}
				}
			}
		}.start();
	}

	public final Object invoke(String functionName) throws IOException {
		return this.invoke(functionName, nullArgs, (Type) null, false, UrosResultMode.Normal, false);
	}

	public final Object invoke(String functionName, Object[] arguments) throws IOException {
		return this.invoke(functionName, arguments, (Type) null, false, UrosResultMode.Normal, false);
	}

	public final Object invoke(String functionName, Object[] arguments, boolean byRef) throws IOException {
		return this.invoke(functionName, arguments, (Type) null, byRef, UrosResultMode.Normal, false);
	}

	public final Object invoke(String functionName, boolean simple) throws IOException {
		return this.invoke(functionName, nullArgs, (Type) null, false, UrosResultMode.Normal, simple);
	}

	public final Object invoke(String functionName, Object[] arguments, boolean byRef, boolean simple) throws IOException {
		return this.invoke(functionName, arguments, (Type) null, byRef, UrosResultMode.Normal, simple);
	}

	public final Object invoke(String functionName, UrosResultMode resultMode) throws IOException {
		return this.invoke(functionName, nullArgs, (Type) null, false, resultMode, false);
	}

	public final Object invoke(String functionName, Object[] arguments, UrosResultMode resultMode) throws IOException {
		return this.invoke(functionName, arguments, (Type) null, false, resultMode, false);
	}

	public final Object invoke(String functionName, Object[] arguments, boolean byRef, UrosResultMode resultMode) throws IOException {
		return this.invoke(functionName, arguments, (Type) null, byRef, resultMode, false);
	}

	public final Object invoke(String functionName, UrosResultMode resultMode, boolean simple) throws IOException {
		return this.invoke(functionName, nullArgs, (Type) null, false, resultMode, simple);
	}

	public final Object invoke(String functionName, Object[] arguments, UrosResultMode resultMode, boolean simple) throws IOException {
		return this.invoke(functionName, arguments, (Type) null, false, resultMode, simple);
	}

	public final Object invoke(String functionName, Object[] arguments, boolean byRef, UrosResultMode resultMode, boolean simple) throws IOException {
		return this.invoke(functionName, arguments, (Type) null, byRef, resultMode, simple);
	}

	@SuppressWarnings("unchecked")
	public final <T> T invoke(String functionName, Class<T> returnType) throws IOException {
		return (T) this.invoke(functionName, nullArgs, (Type) returnType, false, UrosResultMode.Normal, false);
	}

	@SuppressWarnings("unchecked")
	public final <T> T invoke(String functionName, Object[] arguments, Class<T> returnType) throws IOException {
		return (T) this.invoke(functionName, arguments, (Type) returnType, false, UrosResultMode.Normal, false);
	}

	@SuppressWarnings("unchecked")
	public final <T> T invoke(String functionName, Object[] arguments, Class<T> returnType, boolean byRef) throws IOException {
		return (T) this.invoke(functionName, arguments, (Type) returnType, byRef, UrosResultMode.Normal, false);
	}

	@SuppressWarnings("unchecked")
	public final <T> T invoke(String functionName, Class<T> returnType, boolean simple) throws IOException {
		return (T) this.invoke(functionName, nullArgs, (Type) returnType, false, UrosResultMode.Normal, simple);
	}

	@SuppressWarnings("unchecked")
	public final <T> T invoke(String functionName, Object[] arguments, Class<T> returnType, boolean byRef, boolean simple) throws IOException {
		return (T) this.invoke(functionName, arguments, (Type) returnType, byRef, UrosResultMode.Normal, simple);
	}

	@SuppressWarnings("unchecked")
	public final <T> T invoke(String functionName, Class<T> returnType, UrosResultMode resultMode) throws IOException {
		return (T) this.invoke(functionName, nullArgs, (Type) returnType, false, resultMode, false);
	}

	@SuppressWarnings("unchecked")
	public final <T> T invoke(String functionName, Object[] arguments, Class<T> returnType, UrosResultMode resultMode) throws IOException {
		return (T) this.invoke(functionName, arguments, (Type) returnType, false, resultMode, false);
	}

	@SuppressWarnings("unchecked")
	public final <T> T invoke(String functionName, Object[] arguments, Class<T> returnType, boolean byRef, UrosResultMode resultMode) throws IOException {
		return (T) this.invoke(functionName, arguments, (Type) returnType, byRef, resultMode, false);
	}

	@SuppressWarnings("unchecked")
	public final <T> T invoke(String functionName, Class<T> returnType, UrosResultMode resultMode, boolean simple) throws IOException {
		return (T) this.invoke(functionName, nullArgs, (Type) returnType, false, resultMode, simple);
	}

	@SuppressWarnings("unchecked")
	public final <T> T invoke(String functionName, Object[] arguments, Class<T> returnType, UrosResultMode resultMode, boolean simple) throws IOException {
		return (T) this.invoke(functionName, arguments, (Type) returnType, false, resultMode, simple);
	}

	@SuppressWarnings("unchecked")
	public final <T> T invoke(String functionName, Object[] arguments, Class<T> returnType, boolean byRef, UrosResultMode resultMode, boolean simple) throws IOException {
		return (T) this.invoke(functionName, arguments, (Type) returnType, byRef, resultMode, simple);
	}

	public Object invoke(final String functionName, final Object[] arguments, Type returnType, final boolean byRef, final UrosResultMode resultMode, final boolean simple) throws IOException {
		if (Future.class.equals(UrosHelper.toClass(returnType))) {
			if (returnType instanceof ParameterizedType) {
				returnType = ((ParameterizedType) returnType).getActualTypeArguments()[0];
			} else {
				returnType = null;
			}
			if (void.class.equals(returnType) || Void.class.equals(returnType)) {
				returnType = null;
			}
			final Type _returnType = returnType;
			return this.threadPool.submit(new Callable<Object>() {
				public Object call() throws Exception {
					return invoke(functionName, arguments, _returnType, byRef, resultMode, simple);
				}
			});
		}
		ByteBufferStream os = null;
		ByteBufferStream is = null;
		try {
			os = this.doOutput(functionName, arguments, byRef, simple);
			is = this.sendAndReceive(os);
			Object result = this.doInput(is, arguments, returnType, resultMode);
			if (result instanceof UrosProtocolException) {
				throw (UrosProtocolException) result;
			}
			return result;
		} finally {
			if (os != null)
				os.close();
			if (is != null)
				is.close();
		}
	}

	private ByteBufferStream doOutput(String functionName, Object[] arguments, boolean byRef, boolean simple) throws IOException {
		ByteBufferStream stream = new ByteBufferStream();
		UrosWriter writer = new UrosWriter(stream.getOutputStream(), this.mode, simple);
		stream.write(UrosTags.TagCall);
		writer.writeString(functionName);
		if ((arguments != null) && (arguments.length > 0 || byRef)) {
			writer.reset();
			writer.writeArray(arguments);
			if (byRef) {
				writer.writeBoolean(true);
			}
		}
		stream.write(UrosTags.TagEnd);
		stream.flip();
		for (int i = 0, n = this.filters.size(); i < n; ++i) {
			stream.buffer = this.filters.get(i).outputFilter(stream.buffer, this);
			stream.flip();
		}
		return stream;
	}

	private Object ByteBufferStreamToType(ByteBufferStream stream, Type returnType) throws UrosProtocolException {
		stream.flip();
		if (returnType == null || returnType == Object.class || returnType == ByteBuffer.class || returnType == Buffer.class) {
			return stream.buffer;
		}
		if (returnType == ByteBufferStream.class) {
			return stream;
		} else if (returnType == byte[].class) {
			byte[] bytes = stream.toByteArray();
			stream.close();
			return bytes;
		}
		throw new UrosProtocolException("Can't Convert ByteBuffer to Type: " + returnType.toString());
	}

	private Object doInput(ByteBufferStream stream, Object[] arguments, Type returnType, UrosResultMode resultMode) throws IOException {
		stream.flip();
		for (int i = this.filters.size() - 1; i >= 0; --i) {
			stream.buffer = this.filters.get(i).inputFilter(stream.buffer, this);
			stream.flip();
		}
		int tag = stream.buffer.get(stream.buffer.limit() - 1);
		if (tag != UrosTags.TagEnd) {
			throw new UrosProtocolException("Wrong Response: \r\n" + UrosHelper.readWrongInfo(stream));
		}
		if (resultMode == UrosResultMode.Raw) {
			stream.buffer.limit(stream.buffer.limit() - 1);
		}
		if (resultMode == UrosResultMode.RawWithEndTag || resultMode == UrosResultMode.Raw) {
			return ByteBufferStreamToType(stream, returnType);
		}
		Object result = null;
		UrosReader reader = new UrosReader(stream.getInputStream(), this.mode);
		while ((tag = stream.read()) != UrosTags.TagEnd) {
			switch (tag) {
			case UrosTags.TagResult:
				if (resultMode == UrosResultMode.Normal) {
					reader.reset();
					result = reader.deserialize(returnType);
				} else if (resultMode == UrosResultMode.Serialized) {
					result = ByteBufferStreamToType(reader.readRaw(), returnType);
				}
				break;
			case UrosTags.TagArgument:
				reader.reset();
				Object[] args = reader.readObjectArray();
				int length = arguments.length;
				if (length > args.length) {
					length = args.length;
				}
				System.arraycopy(args, 0, arguments, 0, length);
				break;
			case UrosTags.TagError:
				reader.reset();
				result = new UrosProtocolException(reader.readString());
				break;
			default:
				stream.rewind();
				throw new UrosProtocolException("Wrong Response: \r\n" + UrosHelper.readWrongInfo(stream));
			}
		}
		return result;
	}

	protected abstract ByteBufferStream sendAndReceive(ByteBufferStream buffer) throws IOException;
}
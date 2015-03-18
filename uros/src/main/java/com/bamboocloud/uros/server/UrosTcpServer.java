package com.bamboocloud.uros.server;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bamboocloud.uros.io.ByteBufferStream;
import com.bamboocloud.uros.io.UrosHelper;

public class UrosTcpServer extends UrosService {

	private final ExecutorService threadPool = Executors.newCachedThreadPool();

	class HandlerThread extends Thread {

		private final Selector selector;
		private final UrosTcpServer server;

		public HandlerThread(UrosTcpServer server, Selector selector) {
			this.server = server;
			this.selector = selector;
		}

		@Override
		public void run() {
			while (!interrupted()) {
				try {
					int n = this.selector.select();
					if (n == 0) {
						continue;
					}
					Iterator<?> it = this.selector.selectedKeys().iterator();
					while (it.hasNext()) {
						SelectionKey key = (SelectionKey) it.next();
						it.remove();
						if (key.isAcceptable()) {
							doAccept((ServerSocketChannel) key.channel());
						} else if (key.isReadable()) {
							doRead((SocketChannel) key.channel());
						}
					}
				} catch (IOException ex) {
					this.server.fireErrorEvent(ex, null);
				}
			}
		}

		private void doAccept(ServerSocketChannel serverChannel)
				throws IOException {
			SocketChannel channel = serverChannel.accept();
			if (channel != null) {
				channel.configureBlocking(false);
				channel.register(this.selector, SelectionKey.OP_READ);
			}
		}

		private void doRead(final SocketChannel socketChannel)
				throws IOException {
			if (this.server.isEnabledThreadPool()) {
				execInThreadPool(socketChannel);
			} else {
				execDirectly(socketChannel);
			}
		}

		private void execDirectly(final SocketChannel socketChannel)
				throws IOException {
			ByteBufferStream istream = null;
			ByteBufferStream ostream = null;
			try {
				istream = UrosHelper.receiveDataOverTcp(socketChannel);
				ostream = this.server.handle(istream, socketChannel);
				UrosHelper.sendDataOverTcp(socketChannel, ostream);
				socketChannel.register(this.selector, SelectionKey.OP_READ);
			} catch (IOException e) {
				this.server.fireErrorEvent(e, socketChannel);
				socketChannel.close();
			} finally {
				if (istream != null)
					istream.close();
				if (ostream != null)
					ostream.close();
			}
		}

		private void execInThreadPool(final SocketChannel socketChannel)
				throws IOException {
			try {
				final ByteBufferStream istream = UrosHelper
						.receiveDataOverTcp(socketChannel);
				socketChannel.register(this.selector, SelectionKey.OP_READ);
				threadPool.execute(new Runnable() {
					public void run() {
						ByteBufferStream ostream = null;
						try {
							ostream = server.handle(istream, socketChannel);
							UrosHelper.sendDataOverTcp(socketChannel, ostream);
						} catch (IOException e) {
							server.fireErrorEvent(e, socketChannel);
							try {
								socketChannel.close();
							} catch (IOException ex) {
								server.fireErrorEvent(ex, socketChannel);
							}
						} finally {
							if (istream != null)
								istream.close();
							if (ostream != null)
								ostream.close();
						}
					}
				});
			} catch (IOException e) {
				server.fireErrorEvent(e, socketChannel);
				socketChannel.close();
			}
		}
	}

	private ServerSocketChannel serverChannel = null;
	private ArrayList<Selector> selectors = null;
	private ArrayList<HandlerThread> handlerThreads = null;
	private String host = null;
	private int port = 0;
	private int threadCount = Runtime.getRuntime().availableProcessors() + 2;
	private boolean started = false;
	private boolean enabledThreadPool = false;

	public UrosTcpServer(String uri) throws URISyntaxException {
		URI u = new URI(uri);
		this.host = u.getHost();
		this.port = u.getPort();
	}

	public UrosTcpServer(String host, int port) {
		this.host = host;
		this.port = port;
	}

	@Override
	public UrosMethods getGlobalMethods() {
		if (this.globalMethods == null) {
			this.globalMethods = new UrosTcpMethods();
		}
		return this.globalMethods;
	}

	@Override
	public void setGlobalMethods(UrosMethods methods) {
		if (methods instanceof UrosTcpMethods) {
			this.globalMethods = methods;
		} else {
			throw new ClassCastException(
					"methods must be a UrosTcpMethods instance");
		}
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Get the service thread count. The default value is availableProcessors +
	 * 2.
	 * 
	 * @return count of service threads.
	 */
	public int getThreadCount() {
		return this.threadCount;
	}

	/**
	 * Set the service thread count.
	 * 
	 * @param threadCount
	 *            is the count of service threads.
	 */
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	/**
	 * Is enabled thread pool. This thread pool is not for the service threads,
	 * it is for the user service method. The default value is false.
	 * 
	 * @return is enabled thread pool
	 */
	public boolean isEnabledThreadPool() {
		return this.enabledThreadPool;
	}

	/**
	 * Set enabled thread pool. This thread pool is not for the service threads,
	 * it is for the user service method. If your service method takes a long
	 * time, or will be blocked, please set this property to be true.
	 * 
	 * @param enabledThreadPool
	 *            is enabled thread pool
	 */
	public void setEnabledThreadPool(boolean enabledThreadPool) {
		this.enabledThreadPool = enabledThreadPool;
	}

	@Override
	protected Object[] fixArguments(Type[] argumentTypes, Object[] arguments,
			int count, Object context) {
		SocketChannel channel = (SocketChannel) context;
		if (argumentTypes.length != count) {
			Object[] args = new Object[argumentTypes.length];
			System.arraycopy(arguments, 0, args, 0, count);
			Class<?> argType = (Class<?>) argumentTypes[count];
			if (argType.equals(SocketChannel.class)) {
				args[count] = channel;
			}
			return args;
		}
		return arguments;
	}

	public boolean isStarted() {
		return started;
	}

	public void start() throws IOException {
		if (!isStarted()) {
			this.serverChannel = ServerSocketChannel.open();
			ServerSocket serverSocket = this.serverChannel.socket();
			InetSocketAddress address = (this.host == null) ? new InetSocketAddress(
					this.port) : new InetSocketAddress(this.host, this.port);
			serverSocket.bind(address);
			this.serverChannel.configureBlocking(false);
			this.selectors = new ArrayList<Selector>(this.threadCount);
			this.handlerThreads = new ArrayList<HandlerThread>(this.threadCount);
			for (int i = 0; i < this.threadCount; i++) {
				Selector selector = Selector.open();
				this.serverChannel.register(selector, SelectionKey.OP_ACCEPT);
				HandlerThread handlerThread = new HandlerThread(this, selector);
				handlerThread.start();
				this.selectors.add(selector);
				this.handlerThreads.add(handlerThread);
			}
			this.started = true;
		}
	}

	public void stop() {
		if (isStarted()) {
			for (int i = this.selectors.size() - 1; i >= 0; --i) {
				Selector selector = this.selectors.remove(i);
				HandlerThread handlerThread = this.handlerThreads.remove(i);
				handlerThread.interrupt();
				try {
					selector.close();
					this.serverChannel.close();
				} catch (IOException ex) {
					fireErrorEvent(ex, null);
				}
			}
			try {
				if (!this.threadPool.isShutdown()) {
					this.threadPool.shutdown();
				}
			} catch (SecurityException e) {
				fireErrorEvent(e, null);
			}
			this.selectors = null;
			this.handlerThreads = null;
		}
	}
}

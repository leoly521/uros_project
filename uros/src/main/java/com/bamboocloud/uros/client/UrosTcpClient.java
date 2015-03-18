package com.bamboocloud.uros.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.bamboocloud.uros.common.exception.UrosProtocolException;
import com.bamboocloud.uros.io.ByteBufferStream;
import com.bamboocloud.uros.io.UrosHelper;
import com.bamboocloud.uros.io.UrosMode;

public class UrosTcpClient extends UrosClient {

	private int connectTimeout = DEFAULT_CONNECT_TIMEOUT; // millisecond
	private int readTimeout = DEFAULT_READ_TIMEOUT; // millisecond
	
	enum TcpConnStatus {
		Free, Using, Closing
	}

	class TcpConnEntry {
		public String uri;
		public SocketChannel channel;
		public TcpConnStatus status;
		public long lastUsedTime;

		public TcpConnEntry(String uri) {
			this.uri = uri;
			this.status = TcpConnStatus.Using;
		}

		public void close() {
			this.status = TcpConnStatus.Closing;
		}
	}

	class TcpConnPool {
		private final LinkedList<TcpConnEntry> pool = new LinkedList<TcpConnEntry>();
		private Timer timer;
		private int idleTimeout = DEFAULT_IDLE_TIMEOUT;

		public int getIdleTimeout() {
			return idleTimeout;
		}

		public void setIdleTimeout(int idleTimeout) {
			if (this.timer != null) {
				this.timer.cancel();
			}
			this.idleTimeout = idleTimeout;
			
			if (this.idleTimeout > 0) {
				if (this.timer == null) {
					this.timer = new Timer(true);
				}
				this.timer.schedule(new TimerTask() {
					@Override
					public void run() {
						closeIdleTimeoutConnections();
					}
				}, this.idleTimeout, this.idleTimeout);
			} else {
				this.timer = null;
			}
		}

		private void closeChannel(final SocketChannel channel) {
			new Thread() {
				@Override
				public void run() {
					try {
						channel.close();
					} catch (IOException ex) {
					}
				}
			}.start();
		}

		private void freeChannels(final List<SocketChannel> channels) {
			new Thread() {
				@Override
				public void run() {
					for (SocketChannel channel : channels) {
						try {
							channel.close();
						} catch (IOException ex) {
						}
					}
				}
			}.start();
		}

		private void closeIdleTimeoutConnections() {
			LinkedList<SocketChannel> channels = new LinkedList<SocketChannel>();
			synchronized (pool) {
				for (TcpConnEntry entry : pool) {
					if (entry.status == TcpConnStatus.Free && entry.uri != null) {
						if ((entry.channel != null && !entry.channel.isOpen()) || ((System.currentTimeMillis() - entry.lastUsedTime) > this.idleTimeout)) {
							channels.add(entry.channel);
							entry.channel = null;
							entry.uri = null;
						}
					}
				}
			}
			freeChannels(channels);
		}

		public TcpConnEntry get(String uri) {
			synchronized (pool) {
				for (TcpConnEntry entry : pool) {
					if (entry.status == TcpConnStatus.Free) {
						if (entry.uri != null && entry.uri.equals(uri)) {
							if (!entry.channel.isOpen()) {
								closeChannel(entry.channel);
								entry.channel = null;
							}
							entry.status = TcpConnStatus.Using;
							return entry;
						} else if (entry.uri == null) {
							entry.status = TcpConnStatus.Using;
							entry.uri = uri;
							return entry;
						}
					}
				}
				TcpConnEntry newEntry = new TcpConnEntry(uri);
				pool.add(newEntry);
				return newEntry;
			}
		}

		public void close(String uri) {
			LinkedList<SocketChannel> channels = new LinkedList<SocketChannel>();
			synchronized (pool) {
				for (TcpConnEntry entry : pool) {
					if (entry.uri != null && entry.uri.equals(uri)) {
						if (entry.status == TcpConnStatus.Free) {
							channels.add(entry.channel);
							entry.channel = null;
							entry.uri = null;
						} else {
							entry.close();
						}
					}
				}
			}
			freeChannels(channels);
		}

		public void free(TcpConnEntry entry) {
			if (entry.status == TcpConnStatus.Closing) {
				if (entry.channel != null) {
					closeChannel(entry.channel);
					entry.channel = null;
				}
				entry.uri = null;
			}
			entry.lastUsedTime = System.currentTimeMillis();
			entry.status = TcpConnStatus.Free;
		}
	}

	private final TcpConnPool pool = new TcpConnPool();

	public UrosTcpClient() {
		super();
	}

	public UrosTcpClient(String uri) {
		super(uri.split(","));
	}

	public UrosTcpClient(UrosMode mode) {
		super(mode);
	}

	public UrosTcpClient(String uri, UrosMode mode) {
		super(uri.split(","), mode);
	}

	public static UrosClient create(String uri, UrosMode mode) throws IOException, URISyntaxException {
		String scheme = (new URI(uri)).getScheme().toLowerCase();
		if (!scheme.equals("tcp") && !scheme.equals("tcp4") && !scheme.equals("tcp6")) {
			throw new UrosProtocolException("This client doesn't support " + scheme + " scheme.");
		}
		return new UrosTcpClient(uri, mode);
	}

	@Override
	public void close() {
		for (String uri : this.uris) {
			this.pool.close(uri);
		}
		super.close();
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public int getIdleTimeout() {
		return this.pool.getIdleTimeout();
	}

	public void setIdleTimeout(int idleTimeout) {
		this.pool.setIdleTimeout(idleTimeout);
	}

	private SocketChannel createSocketChannel(String uri) throws IOException {
		try {
			URI u = new URI(uri);
			SocketChannel channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.connect(new InetSocketAddress(u.getHost(), u.getPort()));
			int timeout = this.connectTimeout > 200 && this.connectTimeout <= 5000 ? this.connectTimeout : 3000;
			for (int i=1; i<=timeout/200; i++) {
				if (channel.finishConnect()) break;
				try {
					Thread.sleep(200);
				} catch (Exception e) {
				}
			}
			if (!channel.finishConnect()) throw new IOException("Cannot connect to " + uri);
			return channel;
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

	private TcpConnEntry openConnection(int index) throws IOException {
		String uri = null;
		if (index <= 0) {
			index = -1;
			uri = this.uris[this.lastUriIndex];
		} else {
			uri = this.uris[index];
		}

		TcpConnEntry entry = this.pool.get(uri);
		try {
			if (entry.channel == null) {
				entry.channel = this.createSocketChannel(uri);
			}
		} catch (IOException e) {
			index++;
			while (index == this.lastUriIndex) {
				index++;
			}
			if (this.uris.length == 1 || this.uris.length <= index) {
				throw e;
			}
			entry = this.openConnection(index);
		}
		if (index >= 0) this.lastUriIndex = index;
		return entry;
	}

	@Override
	protected ByteBufferStream sendAndReceive(ByteBufferStream stream) throws IOException {
		TcpConnEntry entry = null;
		try {
			entry = this.openConnection(-1);
			UrosHelper.sendDataOverTcp(entry.channel, stream);
			stream = UrosHelper.receiveDataOverTcp(entry.channel);
		} catch (IOException e) {
			if (entry != null) entry.close();
			throw e;
		} finally {
			if (entry != null) this.pool.free(entry);
		}
		return stream;
	}
}

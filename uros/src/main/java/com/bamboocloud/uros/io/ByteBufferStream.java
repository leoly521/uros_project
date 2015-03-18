package com.bamboocloud.uros.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.bamboocloud.uros.common.exception.UrosProtocolException;

public class ByteBufferStream {

	public ByteBuffer buffer;
	private InputStream is;
	private OutputStream os;

	@SuppressWarnings("unchecked")
	private static final ConcurrentLinkedQueue<ByteBuffer>[] byteBufferPool = new ConcurrentLinkedQueue[] {
			new ConcurrentLinkedQueue<ByteBuffer>(),
			new ConcurrentLinkedQueue<ByteBuffer>(),
			new ConcurrentLinkedQueue<ByteBuffer>(),
			new ConcurrentLinkedQueue<ByteBuffer>(),
			new ConcurrentLinkedQueue<ByteBuffer>(),
			new ConcurrentLinkedQueue<ByteBuffer>(),
			new ConcurrentLinkedQueue<ByteBuffer>(),
			new ConcurrentLinkedQueue<ByteBuffer>(),
			new ConcurrentLinkedQueue<ByteBuffer>(),
			new ConcurrentLinkedQueue<ByteBuffer>(),
			new ConcurrentLinkedQueue<ByteBuffer>(),
			new ConcurrentLinkedQueue<ByteBuffer>(),
			new ConcurrentLinkedQueue<ByteBuffer>(),
			new ConcurrentLinkedQueue<ByteBuffer>(),
			new ConcurrentLinkedQueue<ByteBuffer>(),
			new ConcurrentLinkedQueue<ByteBuffer>() };

	private static final int[] debruijn = new int[] { 0, 1, 28, 2, 29, 14, 24,
			3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26,
			12, 18, 6, 11, 5, 10, 9 };

	private static int log2(int x) {
		return debruijn[(x & -x) * 0x077CB531 >>> 27];
	}

	private static int pow2roundup(int x) {
		--x;
		x |= x >> 1;
		x |= x >> 2;
		x |= x >> 4;
		x |= x >> 8;
		x |= x >> 16;
		return x + 1;
	}

	public static ByteBuffer allocate(int capacity) {
		capacity = pow2roundup(capacity);
		if (capacity < 512)
			capacity = 512;
		int index = log2(capacity) - 9;
		if (index < 16) {
			ByteBuffer byteBuffer = byteBufferPool[index].poll();
			if (byteBuffer != null)
				return byteBuffer;
		}
		return ByteBuffer.allocateDirect(capacity);
	}

	public static void free(ByteBuffer buffer) {
		if (buffer.isDirect()) {
			buffer.clear();
			int capacity = buffer.capacity();
			int index = log2(capacity) - 9;
			if (index >= 0 && index < 16) {
				byteBufferPool[index].offer(buffer);
			}
		}
	}

	public ByteBufferStream() {
		this(512);
	}

	public ByteBufferStream(int capacity) {
		this.buffer = allocate(capacity);
	}

	public ByteBufferStream(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	public static ByteBufferStream wrap(byte[] array, int offset, int length) {
		return new ByteBufferStream(ByteBuffer.wrap(array, offset, length));
	}

	public static ByteBufferStream wrap(byte[] array) {
		return new ByteBufferStream(ByteBuffer.wrap(array));
	}

	public void close() {
		if (this.buffer != null) {
			free(this.buffer);
			this.buffer = null;
		}
	}

	public InputStream getInputStream() {
		if (this.is == null) {
			this.is = new ByteBufferInputStream(this);
		}
		return this.is;
	}

	public OutputStream getOutputStream() {
		if (this.os == null) {
			this.os = new ByteBufferOutputStream(this);
		}
		return this.os;
	}

	public int read() {
		if (this.buffer.hasRemaining()) {
			return this.buffer.get() & 0xff;
		} else {
			return -1;
		}
	}

	public int read(byte b[]) {
		return read(b, 0, b.length);
	}

	public int read(byte b[], int off, int len) {
		if (len <= 0) {
			return 0;
		}
		int remain = this.buffer.remaining();
		if (remain <= 0) {
			return -1;
		}
		if (len >= remain) {
			this.buffer.get(b, off, remain);
			return remain;
		}
		this.buffer.get(b, off, len);
		return len;
	}

	public int read(ByteBuffer b) {
		int len = b.remaining();
		if (len <= 0) {
			return 0;
		}
		int remain = this.buffer.remaining();
		if (remain <= 0) {
			return -1;
		}
		if (len >= remain) {
			b.put(this.buffer);
			return remain;
		}
		int oldlimit = this.buffer.limit();
		this.buffer.limit(this.buffer.position() + len);
		b.put(this.buffer);
		this.buffer.limit(oldlimit);
		return len;
	}

	public long skip(long n) {
		if (n <= 0) {
			return 0;
		}
		int remain = this.buffer.remaining();
		if (remain <= 0) {
			return 0;
		}
		if (n > remain) {
			this.buffer.position(this.buffer.limit());
			return remain;
		}
		this.buffer.position(this.buffer.position() + (int) n);
		return n;
	}

	public int available() {
		return this.buffer.remaining();
	}

	public boolean markSupported() {
		return true;
	}

	public void mark(int readlimit) {
		this.buffer.mark();
	}

	public void reset() {
		this.buffer.reset();
	}

	private void grow(int n) {
		if (this.buffer.remaining() < n) {
			int required = this.buffer.position() + n;
			int size = pow2roundup(required) << 1;
			if (size > this.buffer.capacity()) {
				ByteBuffer buf = allocate(size);
				this.buffer.flip();
				buf.put(this.buffer);
				free(this.buffer);
				this.buffer = buf;
			} else {
				this.buffer.limit(size);
			}
		}
	}

	public void write(int b) {
		grow(1);
		this.buffer.put((byte) b);
	}

	public void write(byte b[]) {
		write(b, 0, b.length);
	}

	public void write(byte b[], int off, int len) {
		grow(len);
		this.buffer.put(b, off, len);
	}

	public void write(ByteBuffer b) {
		grow(b.remaining());
		this.buffer.put(b);
	}

	public void flip() {
		if (this.buffer.position() != 0) {
			this.buffer.flip();
		}
	}

	public void rewind() {
		this.buffer.rewind();
	}

	public byte[] toByteArray() {
		this.flip();
		byte[] data = new byte[this.buffer.limit()];
		this.buffer.get(data);
		return data;
	}

	public byte[] asByteArray() {
		byte[] data = this.toByteArray();
		this.flip();
		return data;
	}

	public void readFrom(InputStream is) throws IOException {
		byte[] b = new byte[8192];
		for (;;) {
			int n = is.read(b);
			if (n == -1) {
				break;
			}
			write(b, 0, n);
		}
	}

	public void writeTo(OutputStream os) throws IOException {
		if (this.buffer.hasArray()) {
			os.write(this.buffer.array(), this.buffer.arrayOffset(), this.buffer.limit());
		} else {
			byte[] b = new byte[8192];
			for (;;) {
				int n = read(b);
				if (n == -1) {
					break;
				}
				os.write(b, 0, n);
			}
		}
	}

	public void readFrom(ByteChannel channel, int length) throws IOException {
		int n = 0;
		grow(length);
		this.buffer.limit(this.buffer.position() + length);
		while (n < length) {
			int nn = channel.read(this.buffer);
			if (nn == -1) {
				break;
			}
			n += nn;
		}
		if (n < length) {
			throw new UrosProtocolException("Unexpected EOF");
		}
	}

	public void writeTo(ByteChannel channel) throws IOException {
		while (this.buffer.hasRemaining()) {
			channel.write(this.buffer);
		}
	}
}

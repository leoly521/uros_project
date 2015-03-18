package com.bamboocloud.uros.io;

import java.io.IOException;
import java.io.InputStream;

class ByteBufferInputStream extends InputStream {

	private ByteBufferStream stream;

	public ByteBufferInputStream(ByteBufferStream stream) {
		this.stream = stream;
	}

	@Override
	public int read() throws IOException {
		return this.stream.read();
	}

	@Override
	public int read(byte b[]) throws IOException {
		return this.stream.read(b);
	}

	@Override
	public int read(byte b[], int off, int len) throws IOException {
		return this.stream.read(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException {
		return this.stream.skip(n);
	}

	@Override
	public int available() throws IOException {
		return this.stream.available();
	}

	@Override
	public boolean markSupported() {
		return this.stream.markSupported();
	}

	@Override
	public synchronized void mark(int readlimit) {
		this.stream.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		this.stream.reset();
	}

	@Override
	public void close() throws IOException {
		this.stream.close();
	}
}

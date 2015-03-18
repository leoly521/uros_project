package com.bamboocloud.uros.io;

import java.io.IOException;
import java.io.OutputStream;

class ByteBufferOutputStream extends OutputStream {
	
	private ByteBufferStream stream;

	public ByteBufferOutputStream(ByteBufferStream stream) {
		this.stream = stream;
	}

	@Override
	public void write(int b) throws IOException {
		this.stream.write(b);
	}

	@Override
	public void write(byte b[]) throws IOException {
		this.stream.write(b);
	}

	@Override
	public void write(byte b[], int off, int len) throws IOException {
		this.stream.write(b, off, len);
	}

	@Override
	public void close() throws IOException {
		this.stream.close();
	}
}

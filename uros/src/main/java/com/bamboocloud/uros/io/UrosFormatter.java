package com.bamboocloud.uros.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public final class UrosFormatter {

	private UrosFormatter() {
	}

	public static OutputStream serialize(byte b, OutputStream stream) throws IOException {
		UrosWriter writer = new UrosWriter(stream, true);
		writer.writeInteger(b);
		return stream;
	}

	public static OutputStream serialize(short s, OutputStream stream) throws IOException {
		UrosWriter writer = new UrosWriter(stream, true);
		writer.writeInteger(s);
		return stream;
	}

	public static OutputStream serialize(int i, OutputStream stream) throws IOException {
		UrosWriter writer = new UrosWriter(stream, true);
		writer.writeInteger(i);
		return stream;
	}

	public static OutputStream serialize(long l, OutputStream stream) throws IOException {
		UrosWriter writer = new UrosWriter(stream, true);
		writer.writeLong(l);
		return stream;
	}

	public static OutputStream serialize(float f, OutputStream stream) throws IOException {
		UrosWriter writer = new UrosWriter(stream, true);
		writer.writeDouble(f);
		return stream;
	}

	public static OutputStream serialize(double d, OutputStream stream) throws IOException {
		UrosWriter writer = new UrosWriter(stream, true);
		writer.writeDouble(d);
		return stream;
	}

	public static OutputStream serialize(boolean b, OutputStream stream) throws IOException {
		UrosWriter writer = new UrosWriter(stream, true);
		writer.writeBoolean(b);
		return stream;
	}

	public static OutputStream serialize(char c, OutputStream stream) throws IOException {
		UrosWriter writer = new UrosWriter(stream, true);
		writer.writeUTF8Char(c);
		return stream;
	}

	public static OutputStream serialize(Object obj, OutputStream stream) throws IOException {
		return serialize(obj, stream, UrosMode.MemberMode, false);
	}

	public static OutputStream serialize(Object obj, OutputStream stream, boolean simple) throws IOException {
		return serialize(obj, stream, UrosMode.MemberMode, simple);
	}

	public static OutputStream serialize(Object obj, OutputStream stream, UrosMode mode) throws IOException {
		return serialize(obj, stream, mode, false);
	}

	public static OutputStream serialize(Object obj, OutputStream stream, UrosMode mode, boolean simple) throws IOException {
		UrosWriter writer = new UrosWriter(stream, mode, simple);
		writer.serialize(obj);
		return stream;
	}

	public static ByteBufferStream serialize(byte b) throws IOException {
		ByteBufferStream bufstream = new ByteBufferStream(8);
		serialize(b, bufstream.getOutputStream());
		bufstream.flip();
		return bufstream;
	}

	public static ByteBufferStream serialize(short s) throws IOException {
		ByteBufferStream bufstream = new ByteBufferStream(8);
		serialize(s, bufstream.getOutputStream());
		bufstream.flip();
		return bufstream;
	}

	public static ByteBufferStream serialize(int i) throws IOException {
		ByteBufferStream bufstream = new ByteBufferStream(16);
		serialize(i, bufstream.getOutputStream());
		bufstream.flip();
		return bufstream;
	}

	public static ByteBufferStream serialize(long l) throws IOException {
		ByteBufferStream bufstream = new ByteBufferStream(32);
		serialize(l, bufstream.getOutputStream());
		bufstream.flip();
		return bufstream;
	}

	public static ByteBufferStream serialize(float f) throws IOException {
		ByteBufferStream bufstream = new ByteBufferStream(32);
		serialize(f, bufstream.getOutputStream());
		bufstream.flip();
		return bufstream;
	}

	public static ByteBufferStream serialize(double d) throws IOException {
		ByteBufferStream bufstream = new ByteBufferStream(32);
		serialize(d, bufstream.getOutputStream());
		bufstream.flip();
		return bufstream;
	}

	public static ByteBufferStream serialize(boolean b) throws IOException {
		ByteBufferStream bufstream = new ByteBufferStream(1);
		serialize(b, bufstream.getOutputStream());
		bufstream.flip();
		return bufstream;
	}

	public static ByteBufferStream serialize(char c) throws IOException {
		ByteBufferStream bufstream = new ByteBufferStream(4);
		serialize(c, bufstream.getOutputStream());
		bufstream.flip();
		return bufstream;
	}

	public static ByteBufferStream serialize(Object obj) throws IOException {
		return serialize(obj, UrosMode.MemberMode, false);
	}

	public static ByteBufferStream serialize(Object obj, UrosMode mode) throws IOException {
		return serialize(obj, mode, false);
	}

	public static ByteBufferStream serialize(Object obj, boolean simple) throws IOException {
		return serialize(obj, UrosMode.MemberMode, simple);
	}

	public static ByteBufferStream serialize(Object obj, UrosMode mode, boolean simple) throws IOException {
		ByteBufferStream bufstream = new ByteBufferStream();
		serialize(obj, bufstream.getOutputStream(), mode, simple);
		bufstream.flip();
		return bufstream;
	}

	public static Object deserialize(ByteBufferStream stream) throws IOException {
		UrosReader reader = new UrosReader(stream.getInputStream());
		return reader.deserialize();
	}

	public static Object deserialize(ByteBufferStream stream, UrosMode mode) throws IOException {
		UrosReader reader = new UrosReader(stream.getInputStream(), mode);
		return reader.deserialize();
	}

	public static Object deserialize(ByteBufferStream stream, boolean simple) throws IOException {
		UrosReader reader = new UrosReader(stream.getInputStream(), simple);
		return reader.deserialize();
	}

	public static Object deserialize(ByteBufferStream stream, UrosMode mode, boolean simple) throws IOException {
		UrosReader reader = new UrosReader(stream.getInputStream(), mode, simple);
		return reader.deserialize();
	}

	public static <T> T deserialize(ByteBufferStream stream, Class<T> type) throws IOException {
		UrosReader reader = new UrosReader(stream.getInputStream());
		return reader.deserialize(type);
	}

	public static <T> T deserialize(ByteBufferStream stream, UrosMode mode, Class<T> type) throws IOException {
		UrosReader reader = new UrosReader(stream.getInputStream(), mode);
		return reader.deserialize(type);
	}

	public static <T> T deserialize(ByteBufferStream stream, boolean simple, Class<T> type) throws IOException {
		UrosReader reader = new UrosReader(stream.getInputStream(), simple);
		return reader.deserialize(type);
	}

	public static <T> T deserialize(ByteBufferStream stream, UrosMode mode, boolean simple, Class<T> type) throws IOException {
		UrosReader reader = new UrosReader(stream.getInputStream(), mode, simple);
		return reader.deserialize(type);
	}

	public static Object deserialize(ByteBuffer data) throws IOException {
		ByteBufferStream stream = new ByteBufferStream(data);
		UrosReader reader = new UrosReader(stream.getInputStream());
		return reader.deserialize();
	}

	public static Object deserialize(ByteBuffer data, UrosMode mode) throws IOException {
		ByteBufferStream stream = new ByteBufferStream(data);
		UrosReader reader = new UrosReader(stream.getInputStream(), mode);
		return reader.deserialize();
	}

	public static Object deserialize(ByteBuffer data, boolean simple) throws IOException {
		ByteBufferStream stream = new ByteBufferStream(data);
		UrosReader reader = new UrosReader(stream.getInputStream(), simple);
		return reader.deserialize();
	}

	public static Object deserialize(ByteBuffer data, UrosMode mode, boolean simple) throws IOException {
		ByteBufferStream stream = new ByteBufferStream(data);
		UrosReader reader = new UrosReader(stream.getInputStream(), mode, simple);
		return reader.deserialize();
	}

	public static <T> T deserialize(ByteBuffer data, Class<T> type) throws IOException {
		ByteBufferStream stream = new ByteBufferStream(data);
		UrosReader reader = new UrosReader(stream.getInputStream());
		return reader.deserialize(type);
	}

	public static <T> T deserialize(ByteBuffer data, UrosMode mode, Class<T> type) throws IOException {
		ByteBufferStream stream = new ByteBufferStream(data);
		UrosReader reader = new UrosReader(stream.getInputStream(), mode);
		return reader.deserialize(type);
	}

	public static <T> T deserialize(ByteBuffer data, boolean simple, Class<T> type) throws IOException {
		ByteBufferStream stream = new ByteBufferStream(data);
		UrosReader reader = new UrosReader(stream.getInputStream(), simple);
		return reader.deserialize(type);
	}

	public static <T> T deserialize(ByteBuffer data, UrosMode mode, boolean simple, Class<T> type) throws IOException {
		ByteBufferStream stream = new ByteBufferStream(data);
		UrosReader reader = new UrosReader(stream.getInputStream(), mode, simple);
		return reader.deserialize(type);
	}

	public static Object deserialize(byte[] data) throws IOException {
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		UrosReader reader = new UrosReader(stream);
		return reader.deserialize();
	}

	public static Object deserialize(byte[] data, UrosMode mode) throws IOException {
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		UrosReader reader = new UrosReader(stream, mode);
		return reader.deserialize();
	}

	public static Object deserialize(byte[] data, boolean simple) throws IOException {
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		UrosReader reader = new UrosReader(stream, simple);
		return reader.deserialize();
	}

	public static Object deserialize(byte[] data, UrosMode mode, boolean simple) throws IOException {
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		UrosReader reader = new UrosReader(stream, mode, simple);
		return reader.deserialize();
	}

	public static <T> T deserialize(byte[] data, Class<T> type) throws IOException {
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		UrosReader reader = new UrosReader(stream);
		return reader.deserialize(type);
	}

	public static <T> T deserialize(byte[] data, UrosMode mode, Class<T> type) throws IOException {
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		UrosReader reader = new UrosReader(stream, mode);
		return reader.deserialize(type);
	}

	public static <T> T deserialize(byte[] data, boolean simple, Class<T> type) throws IOException {
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		UrosReader reader = new UrosReader(stream, simple);
		return reader.deserialize(type);
	}

	public static <T> T deserialize(byte[] data, UrosMode mode, boolean simple, Class<T> type) throws IOException {
		ByteArrayInputStream stream = new ByteArrayInputStream(data);
		UrosReader reader = new UrosReader(stream, mode, simple);
		return reader.deserialize(type);
	}

	public static Object deserialize(InputStream stream) throws IOException {
		UrosReader reader = new UrosReader(stream);
		return reader.deserialize();
	}

	public static Object deserialize(InputStream stream, UrosMode mode) throws IOException {
		UrosReader reader = new UrosReader(stream, mode);
		return reader.deserialize();
	}

	public static Object deserialize(InputStream stream, boolean simple) throws IOException {
		UrosReader reader = new UrosReader(stream, simple);
		return reader.deserialize();
	}

	public static Object deserialize(InputStream stream, UrosMode mode, boolean simple) throws IOException {
		UrosReader reader = new UrosReader(stream, mode, simple);
		return reader.deserialize();
	}

	public static <T> T deserialize(InputStream stream, Class<T> type) throws IOException {
		UrosReader reader = new UrosReader(stream);
		return reader.deserialize(type);
	}

	public static <T> T deserialize(InputStream stream, UrosMode mode, Class<T> type) throws IOException {
		UrosReader reader = new UrosReader(stream, mode);
		return reader.deserialize(type);
	}

	public static <T> T deserialize(InputStream stream, boolean simple, Class<T> type) throws IOException {
		UrosReader reader = new UrosReader(stream, simple);
		return reader.deserialize(type);
	}

	public static <T> T deserialize(InputStream stream, UrosMode mode, boolean simple, Class<T> type) throws IOException {
		UrosReader reader = new UrosReader(stream, mode, simple);
		return reader.deserialize(type);
	}
	
	public static byte[] serializeToByteArray(Object obj) throws IOException {
		return serializeToByteArray(obj, UrosMode.MemberMode, false);
	}

	public static byte[] serializeToByteArray(Object obj, UrosMode mode) throws IOException {
		return serializeToByteArray(obj, mode, false);
	}

	public static byte[] serializeToByteArray(Object obj, boolean simple) throws IOException {
		return serializeToByteArray(obj, UrosMode.MemberMode, simple);
	}

	public static byte[] serializeToByteArray(Object obj, UrosMode mode, boolean simple) throws IOException {
		return serialize(obj, mode, simple).toByteArray();
	}
	
	public static String serializeToString(Object obj) throws IOException {
		return serializeToString(obj, UrosMode.MemberMode, false);
	}

	public static String serializeToString(Object obj, UrosMode mode) throws IOException {
		return serializeToString(obj, mode, false);
	}

	public static String serializeToString(Object obj, boolean simple) throws IOException {
		return serializeToString(obj, UrosMode.MemberMode, simple);
	}

	public static String serializeToString(Object obj, UrosMode mode, boolean simple) throws IOException {
		byte[] bytes = serializeToByteArray(obj, mode, simple);
		return new String(bytes, Charset.forName("UTF-8"));
	}
}

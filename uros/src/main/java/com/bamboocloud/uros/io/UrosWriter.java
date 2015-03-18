package com.bamboocloud.uros.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

import com.bamboocloud.uros.common.exception.UrosProtocolException;
import com.bamboocloud.uros.databind.ser.SerializerFactory;

public final class UrosWriter {
	
	interface WriterRefer {
		void addCount(int count);

		void set(Object obj);

		boolean write(Object obj) throws IOException;

		void reset();
	}

	class FakeWriterRefer implements WriterRefer {
		public void addCount(int count) {
		}

		public void set(Object obj) {
		}

		public boolean write(Object obj) throws IOException {
			return false;
		}

		public void reset() {
		}
	}

	class RealWriterRefer implements WriterRefer {
		private final UrosWriter writer;
		private final ObjectIntMap ref = new ObjectIntMap();
		private int lastref = 0;

		public RealWriterRefer(UrosWriter writer) {
			this.writer = writer;
		}

		public void addCount(int count) {
			this.lastref += count;
		}

		public void set(Object obj) {
			this.ref.put(obj, this.lastref++);
		}

		public boolean write(Object obj) throws IOException {
			if (this.ref.containsKey(obj)) {
				this.writer.stream.write(UrosTags.TagRef);
				this.writer.writeInt(this.ref.get(obj));
				this.writer.stream.write(UrosTags.TagSemicolon);
				return true;
			}
			return false;
		}

		public void reset() {
			this.ref.clear();
			this.lastref = 0;
		}
	}

	private static final EnumMap<UrosMode, ConcurrentHashMap<Class<?>, SerializeCache>> memberCache = new EnumMap<UrosMode, ConcurrentHashMap<Class<?>, SerializeCache>>(UrosMode.class);
	
	static {
		memberCache.put(UrosMode.FieldMode, new ConcurrentHashMap<Class<?>, SerializeCache>());
		memberCache.put(UrosMode.PropertyMode, new ConcurrentHashMap<Class<?>, SerializeCache>());
		memberCache.put(UrosMode.MemberMode, new ConcurrentHashMap<Class<?>, SerializeCache>());
	}
	
	public final OutputStream stream;
	private final UrosMode mode;
	private final WriterRefer refer;
	private final ObjectIntMap classref = new ObjectIntMap();
	private final byte[] buf = new byte[20];
	private static final byte[] minIntBuf = new byte[] { '-', '2', '1', '4', '7', '4', '8', '3', '6', '4', '8' };
	private static final byte[] minLongBuf = new byte[] { '-', '9', '2', '2', '3', '3', '7', '2', '0', '3', '6', '8', '5', '4', '7', '7', '5', '8', '0', '8' };
	private int lastclassref = 0;

	public UrosWriter(OutputStream stream) {
		this(stream, UrosMode.MemberMode, false);
	}

	public UrosWriter(OutputStream stream, boolean simple) {
		this(stream, UrosMode.MemberMode, simple);
	}

	public UrosWriter(OutputStream stream, UrosMode mode) {
		this(stream, mode, false);
	}

	public UrosWriter(OutputStream stream, UrosMode mode, boolean simple) {
		this.stream = stream;
		this.mode = mode;
		this.refer = simple ? new FakeWriterRefer() : new RealWriterRefer(this);
	}

	@SuppressWarnings({ "unchecked" })
	public void serialize(Object obj) throws IOException {
		if (obj == null) {
			writeNull();
		} else {
			SerializerFactory.get(obj.getClass()).write(this, obj);
		}
	}

	public void writeInteger(int i) throws IOException {
		if (i >= 0 && i <= 9) {
			this.stream.write(i + '0');
		} else {
			this.stream.write(UrosTags.TagInteger);
			this.writeInt(i);
			this.stream.write(UrosTags.TagSemicolon);
		}
	}

	public void writeLong(long l) throws IOException {
		if (l >= 0 && l <= 9) {
			this.stream.write((int) l + '0');
		} else {
			this.stream.write(UrosTags.TagLong);
			this.writeInt(l);
			this.stream.write(UrosTags.TagSemicolon);
		}
	}

	public void writeLong(BigInteger l) throws IOException {
		if (l.equals(BigInteger.ZERO)) {
			this.stream.write('0');
		} else if (l.equals(BigInteger.ONE)) {
			this.stream.write('1');
		} else {
			this.stream.write(UrosTags.TagLong);
			this.stream.write(getAscii(l.toString()));
			this.stream.write(UrosTags.TagSemicolon);
		}
	}

	public void writeDouble(float d) throws IOException {
		if (Float.isNaN(d)) {
			this.stream.write(UrosTags.TagNaN);
		} else if (Float.isInfinite(d)) {
			this.stream.write(UrosTags.TagInfinity);
			this.stream.write(d > 0 ? UrosTags.TagPos : UrosTags.TagNeg);
		} else {
			this.stream.write(UrosTags.TagDouble);
			this.stream.write(this.getAscii(Float.toString(d)));
			this.stream.write(UrosTags.TagSemicolon);
		}
	}

	public void writeDouble(double d) throws IOException {
		if (Double.isNaN(d)) {
			this.stream.write(UrosTags.TagNaN);
		} else if (Double.isInfinite(d)) {
			this.stream.write(UrosTags.TagInfinity);
			this.stream.write(d > 0 ? UrosTags.TagPos : UrosTags.TagNeg);
		} else {
			this.stream.write(UrosTags.TagDouble);
			this.stream.write(this.getAscii(Double.toString(d)));
			this.stream.write(UrosTags.TagSemicolon);
		}
	}

	public void writeDouble(BigDecimal d) throws IOException {
		this.stream.write(UrosTags.TagDouble);
		this.stream.write(this.getAscii(d.toString()));
		this.stream.write(UrosTags.TagSemicolon);
	}

	public void writeNaN() throws IOException {
		this.stream.write(UrosTags.TagNaN);
	}

	public void writeInfinity(boolean positive) throws IOException {
		this.stream.write(UrosTags.TagInfinity);
		this.stream.write(positive ? UrosTags.TagPos : UrosTags.TagNeg);
	}

	public void writeNull() throws IOException {
		this.stream.write(UrosTags.TagNull);
	}

	public void writeEmpty() throws IOException {
		this.stream.write(UrosTags.TagEmpty);
	}

	public void writeBoolean(boolean b) throws IOException {
		this.stream.write(b ? UrosTags.TagTrue : UrosTags.TagFalse);
	}

	public void writeDate(Date date) throws IOException {
		this.refer.set(date);
		Calendar calendar = Calendar.getInstance(UrosHelper.DefaultTZ);
		calendar.setTime(date);
		this.writeDateOfCalendar(calendar);
		this.stream.write(UrosTags.TagSemicolon);
	}

	public void writeDateWithRef(Date date) throws IOException {
		if (!this.refer.write(date)) {
			this.writeDate(date);
		}
	}

	public void writeDate(Time time) throws IOException {
		this.refer.set(time);
		Calendar calendar = Calendar.getInstance(UrosHelper.DefaultTZ);
		calendar.setTime(time);
		writeTimeOfCalendar(calendar, false, false);
		this.stream.write(UrosTags.TagSemicolon);
	}

	public void writeDateWithRef(Time time) throws IOException {
		if (!this.refer.write(time)) {
			this.writeDate(time);
		}
	}

	public void writeDate(Timestamp time) throws IOException {
		this.refer.set(time);
		Calendar calendar = Calendar.getInstance(UrosHelper.DefaultTZ);
		calendar.setTime(time);
		writeDateOfCalendar(calendar);
		writeTimeOfCalendar(calendar, false, true);
		int nanosecond = time.getNanos();
		if (nanosecond > 0) {
			this.stream.write(UrosTags.TagPoint);
			this.stream.write((byte) ('0' + (nanosecond / 100000000 % 10)));
			this.stream.write((byte) ('0' + (nanosecond / 10000000 % 10)));
			this.stream.write((byte) ('0' + (nanosecond / 1000000 % 10)));
			if (nanosecond % 1000000 > 0) {
				this.stream.write((byte) ('0' + (nanosecond / 100000 % 10)));
				this.stream.write((byte) ('0' + (nanosecond / 10000 % 10)));
				this.stream.write((byte) ('0' + (nanosecond / 1000 % 10)));
				if (nanosecond % 1000 > 0) {
					this.stream.write((byte) ('0' + (nanosecond / 100 % 10)));
					this.stream.write((byte) ('0' + (nanosecond / 10 % 10)));
					this.stream.write((byte) ('0' + (nanosecond % 10)));
				}
			}
		}
		this.stream.write(UrosTags.TagSemicolon);
	}

	public void writeDateWithRef(Timestamp time) throws IOException {
		if (!this.refer.write(time)) {
			this.writeDate(time);
		}
	}

	public void writeDate(java.util.Date date) throws IOException {
		this.refer.set(date);
		Calendar calendar = Calendar.getInstance(UrosHelper.DefaultTZ);
		calendar.setTime(date);
		this.writeDateOfCalendar(calendar);
		this.writeTimeOfCalendar(calendar, true, false);
		this.stream.write(UrosTags.TagSemicolon);
	}

	public void writeDateWithRef(java.util.Date date) throws IOException {
		if (!this.refer.write(date)) {
			this.writeDate(date);
		}
	}

	public void writeDate(Calendar calendar) throws IOException {
		this.refer.set(calendar);
		TimeZone tz = calendar.getTimeZone();
		if (!(tz.hasSameRules(UrosHelper.DefaultTZ) || tz.hasSameRules(UrosHelper.UTC))) {
			tz = UrosHelper.UTC;
			Calendar c = (Calendar) calendar.clone();
			c.setTimeZone(tz);
			calendar = c;
		}
		writeDateOfCalendar(calendar);
		writeTimeOfCalendar(calendar, true, false);
		this.stream.write(tz.hasSameRules(UrosHelper.UTC) ? UrosTags.TagUTC : UrosTags.TagSemicolon);
	}

	public void writeDateWithRef(Calendar calendar) throws IOException {
		if (!this.refer.write(calendar)) {
			this.writeDate(calendar);
		}
	}

	public void writeTime(Time time) throws IOException {
		this.writeDate(time);
	}

	public void writeTimeWithRef(Time time) throws IOException {
		this.writeDateWithRef(time);
	}

	private void writeDateOfCalendar(Calendar calendar) throws IOException {
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		this.stream.write(UrosTags.TagDate);
		this.stream.write((byte) ('0' + (year / 1000 % 10)));
		this.stream.write((byte) ('0' + (year / 100 % 10)));
		this.stream.write((byte) ('0' + (year / 10 % 10)));
		this.stream.write((byte) ('0' + (year % 10)));
		this.stream.write((byte) ('0' + (month / 10 % 10)));
		this.stream.write((byte) ('0' + (month % 10)));
		this.stream.write((byte) ('0' + (day / 10 % 10)));
		this.stream.write((byte) ('0' + (day % 10)));
	}

	private void writeTimeOfCalendar(Calendar calendar, boolean ignoreZero, boolean ignoreMillisecond) throws IOException {
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		int millisecond = calendar.get(Calendar.MILLISECOND);
		if (ignoreZero && hour == 0 && minute == 0 && second == 0 && millisecond == 0) {
			return;
		}
		this.stream.write(UrosTags.TagTime);
		this.stream.write((byte) ('0' + (hour / 10 % 10)));
		this.stream.write((byte) ('0' + (hour % 10)));
		this.stream.write((byte) ('0' + (minute / 10 % 10)));
		this.stream.write((byte) ('0' + (minute % 10)));
		this.stream.write((byte) ('0' + (second / 10 % 10)));
		this.stream.write((byte) ('0' + (second % 10)));
		if (!ignoreMillisecond && millisecond > 0) {
			this.stream.write(UrosTags.TagPoint);
			this.stream.write((byte) ('0' + (millisecond / 100 % 10)));
			this.stream.write((byte) ('0' + (millisecond / 10 % 10)));
			this.stream.write((byte) ('0' + (millisecond % 10)));
		}
	}

	public void writeBytes(byte[] bytes) throws IOException {
		this.refer.set(bytes);
		this.stream.write(UrosTags.TagBytes);
		if (bytes.length > 0) {
			this.writeInt(bytes.length);
		}
		this.stream.write(UrosTags.TagQuote);
		this.stream.write(bytes);
		this.stream.write(UrosTags.TagQuote);
	}

	public void writeBytesWithRef(byte[] bytes) throws IOException {
		if (!this.refer.write(bytes)) {
			this.writeBytes(bytes);
		}
	}

	public void writeUTF8Char(int c) throws IOException {
		this.stream.write(UrosTags.TagUTF8Char);
		if (c < 0x80) {
			this.stream.write(c);
		} else if (c < 0x800) {
			this.stream.write(0xc0 | (c >>> 6));
			this.stream.write(0x80 | (c & 0x3f));
		} else {
			this.stream.write(0xe0 | (c >>> 12));
			this.stream.write(0x80 | ((c >>> 6) & 0x3f));
			this.stream.write(0x80 | (c & 0x3f));
		}
	}

	public void writeString(String s) throws IOException {
		this.refer.set(s);
		this.stream.write(UrosTags.TagString);
		this.writeUTF8String(s, stream);
	}

	public void writeStringWithRef(String s) throws IOException {
		if (!this.refer.write(s)) {
			this.writeString(s);
		}
	}

	private void writeUTF8String(String s, OutputStream os) throws IOException {
		int length = s.length();
		if (length > 0) {
			this.writeInt(length, os);
		}
		os.write(UrosTags.TagQuote);
		os.write(s.getBytes("UTF-8"));
		os.write(UrosTags.TagQuote);
	}

	public void writeString(StringBuilder s) throws IOException {
		this.refer.set(s);
		this.stream.write(UrosTags.TagString);
		this.writeUTF8String(s.toString(), stream);
	}

	public void writeStringWithRef(StringBuilder s) throws IOException {
		if (!this.refer.write(s)) {
			this.writeString(s);
		}
	}

	public void writeString(StringBuffer s) throws IOException {
		this.refer.set(s);
		this.stream.write(UrosTags.TagString);
		this.writeUTF8String(s.toString(), stream);
	}

	public void writeStringWithRef(StringBuffer s) throws IOException {
		if (!this.refer.write(s)) {
			this.writeString(s);
		}
	}

	public void writeString(char[] s) throws IOException {
		refer.set(s);
		this.stream.write(UrosTags.TagString);
		this.writeUTF8String(s);
	}

	public void writeStringWithRef(char[] s) throws IOException {
		if (!this.refer.write(s)) {
			this.writeString(s);
		}
	}

	private void writeUTF8String(char[] s) throws IOException {
		int length = s.length;
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagQuote);
		this.stream.write(new String(s).getBytes("UTF-8"));
		this.stream.write(UrosTags.TagQuote);
	}

	public void writeUUID(UUID uuid) throws IOException {
		refer.set(uuid);
		this.stream.write(UrosTags.TagGuid);
		this.stream.write(UrosTags.TagOpenbrace);
		this.stream.write(this.getAscii(uuid.toString()));
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeUUIDWithRef(UUID uuid) throws IOException {
		if (!this.refer.write(uuid)) {
			this.writeUUID(uuid);
		}
	}

	public void writeArray(short[] array) throws IOException {
		this.refer.set(array);
		int length = array.length;
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			this.writeInteger(array[i]);
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(short[] array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(int[] array) throws IOException {
		refer.set(array);
		int length = array.length;
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			this.writeInteger(array[i]);
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(int[] array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(long[] array) throws IOException {
		this.refer.set(array);
		int length = array.length;
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			writeLong(array[i]);
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(long[] array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(float[] array) throws IOException {
		this.refer.set(array);
		int length = array.length;
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			this.writeDouble(array[i]);
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(float[] array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(double[] array) throws IOException {
		this.refer.set(array);
		int length = array.length;
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			this.writeDouble(array[i]);
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(double[] array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(boolean[] array) throws IOException {
		this.refer.set(array);
		int length = array.length;
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			this.writeBoolean(array[i]);
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(boolean[] array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(Date[] array) throws IOException {
		this.refer.set(array);
		int length = array.length;
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			if (array[i] == null) {
				this.writeNull();
			} else {
				this.writeDateWithRef(array[i]);
			}
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(Date[] array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(Time[] array) throws IOException {
		this.refer.set(array);
		int length = array.length;
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			if (array[i] == null) {
				this.writeNull();
			} else {
				this.writeDateWithRef(array[i]);
			}
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(Time[] array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(Timestamp[] array) throws IOException {
		this.refer.set(array);
		int length = array.length;
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			if (array[i] == null) {
				this.writeNull();
			} else {
				this.writeDateWithRef(array[i]);
			}
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(Timestamp[] array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(java.util.Date[] array) throws IOException {
		this.refer.set(array);
		int length = array.length;
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			if (array[i] == null) {
				this.writeNull();
			} else {
				this.writeDateWithRef(array[i]);
			}
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(java.util.Date[] array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(Calendar[] array) throws IOException {
		this.refer.set(array);
		int length = array.length;
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			if (array[i] == null) {
				this.writeNull();
			} else {
				this.writeDateWithRef(array[i]);
			}
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(Calendar[] array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(String[] array) throws IOException {
		this.refer.set(array);
		int length = array.length;
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			if (array[i] == null) {
				this.writeNull();
			} else {
				this.writeStringWithRef(array[i]);
			}
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(String[] array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(StringBuilder[] array) throws IOException {
		this.refer.set(array);
		int length = array.length;
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			if (array[i] == null) {
				this.writeNull();
			} else {
				this.writeStringWithRef(array[i]);
			}
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(StringBuilder[] array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(StringBuffer[] array) throws IOException {
		this.refer.set(array);
		int length = array.length;
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			if (array[i] == null) {
				this.writeNull();
			} else {
				this.writeStringWithRef(array[i]);
			}
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(StringBuffer[] array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(UUID[] array) throws IOException {
		this.refer.set(array);
		int length = array.length;
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			if (array[i] == null) {
				this.writeNull();
			} else {
				this.writeUUIDWithRef(array[i]);
			}
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(UUID[] array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(char[][] array) throws IOException {
		this.refer.set(array);
		int length = array.length;
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			if (array[i] == null) {
				this.writeNull();
			} else {
				this.writeStringWithRef(array[i]);
			}
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(char[][] array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(byte[][] array) throws IOException {
		this.refer.set(array);
		int length = array.length;
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			if (array[i] == null) {
				this.writeNull();
			} else {
				this.writeBytesWithRef(array[i]);
			}
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(byte[][] array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(BigInteger[] array) throws IOException {
		this.refer.set(array);
		int length = array.length;
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			if (array[i] == null) {
				this.writeNull();
			} else {
				this.writeLong(array[i]);
			}
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(BigInteger[] array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(BigDecimal[] array) throws IOException {
		this.refer.set(array);
		int length = array.length;
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			if (array[i] == null) {
				this.writeNull();
			} else {
				this.writeDouble(array[i]);
			}
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(BigDecimal[] array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(Object[] array) throws IOException {
		this.refer.set(array);
		int length = array.length;
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			this.serialize(array[i]);
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(Object[] array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(AtomicIntegerArray array) throws IOException {
		this.refer.set(array);
		int length = array.length();
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			this.writeInteger(array.get(i));
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(AtomicIntegerArray array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(AtomicLongArray array) throws IOException {
		this.refer.set(array);
		int length = array.length();
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			this.writeLong(array.get(i));
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(AtomicLongArray array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	@SuppressWarnings("rawtypes")
	public void writeArray(AtomicReferenceArray array) throws IOException {
		this.refer.set(array);
		int length = array.length();
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			this.serialize(array.get(i));
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	@SuppressWarnings("rawtypes")
	public void writeArrayWithRef(AtomicReferenceArray array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeArray(Object array) throws IOException {
		this.refer.set(array);
		int length = Array.getLength(array);
		this.stream.write(UrosTags.TagList);
		if (length > 0) {
			this.writeInt(length);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < length; ++i) {
			this.serialize(Array.get(array, i));
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeArrayWithRef(Object array) throws IOException {
		if (!this.refer.write(array)) {
			this.writeArray(array);
		}
	}

	public void writeCollection(Collection<?> collection) throws IOException {
		this.refer.set(collection);
		int count = collection.size();
		this.stream.write(UrosTags.TagList);
		if (count > 0) {
			this.writeInt(count);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (Object item : collection) {
			this.serialize(item);
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeCollectionWithRef(Collection<?> collection) throws IOException {
		if (!this.refer.write(collection)) {
			this.writeCollection(collection);
		}
	}

	public void writeList(List<?> list) throws IOException {
		this.refer.set(list);
		int count = list.size();
		this.stream.write(UrosTags.TagList);
		if (count > 0) {
			this.writeInt(count);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (int i = 0; i < count; ++i) {
			this.serialize(list.get(i));
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeListWithRef(List<?> list) throws IOException {
		if (!this.refer.write(list)) {
			this.writeList(list);
		}
	}

	public void writeMap(Map<?, ?> map) throws IOException {
		this.refer.set(map);
		int count = map.size();
		this.stream.write(UrosTags.TagMap);
		if (count > 0) {
			this.writeInt(count);
		}
		this.stream.write(UrosTags.TagOpenbrace);
		for (Entry<?, ?> entry : map.entrySet()) {
			this.serialize(entry.getKey());
			this.serialize(entry.getValue());
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeMapWithRef(Map<?, ?> map) throws IOException {
		if (!this.refer.write(map)) {
			this.writeMap(map);
		}
	}

	@SuppressWarnings({ "unchecked" })
	public void writeObject(Object object) throws IOException {
		Class<?> type = object.getClass();
		int cr = this.classref.get(type);
		if (cr < 0) {
			cr = this.writeClass(type);
		}
		this.refer.set(object);
		Map<String, MemberAccessor> members = UrosHelper.getMembers(type, this.mode);
		this.stream.write(UrosTags.TagObject);
		this.writeInt(cr);
		this.stream.write(UrosTags.TagOpenbrace);
		for (Entry<String, MemberAccessor> entry : members.entrySet()) {
			MemberAccessor member = entry.getValue();
			Object value;
			try {
				value = member.get(object);
			} catch (Exception e) {
				throw new UrosProtocolException(e.getMessage());
			}
			if (value == null) {
				this.writeNull();
			} else {
				member.serializer.write(this, value);
			}
		}
		this.stream.write(UrosTags.TagClosebrace);
	}

	public void writeObjectWithRef(Object object) throws IOException {
		if (!this.refer.write(object)) {
			this.writeObject(object);
		}
	}

	private int writeClass(Class<?> type) throws IOException {
		SerializeCache cache = memberCache.get(this.mode).get(type);
		if (cache == null) {
			cache = new SerializeCache();
			ByteArrayOutputStream cachestream = new ByteArrayOutputStream();
			Map<String, MemberAccessor> members = UrosHelper.getMembers(type, this.mode);
			int count = members.size();
			cachestream.write(UrosTags.TagClass);
			this.writeUTF8String(UrosHelper.getClassName(type), cachestream);
			if (count > 0) {
				this.writeInt(count, cachestream);
			}
			cachestream.write(UrosTags.TagOpenbrace);
			for (Entry<String, MemberAccessor> member : members.entrySet()) {
				cachestream.write(UrosTags.TagString);
				this.writeUTF8String(member.getKey(), cachestream);
				++cache.refcount;
			}
			cachestream.write(UrosTags.TagClosebrace);
			cache.data = cachestream.toByteArray();
			memberCache.get(this.mode).put(type, cache);
		}
		this.stream.write(cache.data);
		this.refer.addCount(cache.refcount);
		int cr = this.lastclassref++;
		this.classref.put(type, cr);
		return cr;
	}

	private byte[] getAscii(String s) {
		int size = s.length();
		byte[] b = new byte[size--];
		for (; size >= 0; --size) {
			b[size] = (byte) s.charAt(size);
		}
		return b;
	}

	private void writeInt(int i) throws IOException {
		this.writeInt(i, this.stream);
	}

	private void writeInt(int i, OutputStream os) throws IOException {
		if ((i >= 0) && (i <= 9)) {
			os.write((byte) ('0' + i));
		} else if (i == Integer.MIN_VALUE) {
			os.write(minIntBuf);
		} else {
			int off = 20;
			int len = 0;
			boolean neg = false;
			if (i < 0) {
				neg = true;
				i = -i;
			}
			while (i != 0) {
				this.buf[--off] = (byte) (i % 10 + '0');
				++len;
				i /= 10;
			}
			if (neg) {
				this.buf[--off] = '-';
				++len;
			}
			os.write(buf, off, len);
		}
	}

	private void writeInt(long i) throws IOException {
		if ((i >= 0) && (i <= 9)) {
			this.stream.write((byte) ('0' + i));
		} else if (i == Long.MIN_VALUE) {
			this.stream.write(minLongBuf);
		} else {
			int off = 20;
			int len = 0;
			boolean neg = false;
			if (i < 0) {
				neg = true;
				i = -i;
			}
			while (i != 0) {
				this.buf[--off] = (byte) (i % 10 + '0');
				++len;
				i /= 10;
			}
			if (neg) {
				this.buf[--off] = '-';
				++len;
			}
			this.stream.write(this.buf, off, len);
		}
	}

	public void reset() {
		this.refer.reset();
		this.classref.clear();
		this.lastclassref = 0;
	}

	private class SerializeCache {
		byte[] data;
		int refcount;
	}
}

package com.bamboocloud.uros.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

import com.bamboocloud.uros.common.exception.UrosProtocolException;
import com.bamboocloud.uros.databind.deser.UrosDeserializer;
import com.bamboocloud.uros.databind.deser.DeserializerFactory;

public final class UrosReader {

	interface ReaderRefer {
		void set(Object obj);

		Object read(int index);

		void reset();
	}

	final class FakeReaderRefer implements ReaderRefer {
		public void set(Object obj) {
		}

		public Object read(int index) {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void reset() {
		}
	}

	final class RealReaderRefer implements ReaderRefer {
		private final ArrayList<Object> ref = new ArrayList<Object>();

		public void set(Object obj) {
			this.ref.add(obj);
		}

		public Object read(int index) {
			return this.ref.get(index);
		}

		public void reset() {
			this.ref.clear();
		}
	}

	public final InputStream stream;
	private final UrosMode mode;
	private final ReaderRefer refer;
	private final ArrayList<Object> classref = new ArrayList<Object>();
	private final IdentityHashMap<Object, String[]> membersref = new IdentityHashMap<Object, String[]>();

	public UrosReader(InputStream stream) {
		this(stream, UrosMode.MemberMode, false);
	}

	public UrosReader(InputStream stream, boolean simple) {
		this(stream, UrosMode.MemberMode, simple);
	}

	public UrosReader(InputStream stream, UrosMode mode) {
		this(stream, mode, false);
	}

	public UrosReader(InputStream stream, UrosMode mode, boolean simple) {
		this.stream = stream;
		this.mode = mode;
		this.refer = simple ? new FakeReaderRefer() : new RealReaderRefer();
	}

	public UrosProtocolException unexpectedTag(int tag) {
		return unexpectedTag(tag, null);
	}

	public UrosProtocolException unexpectedTag(int tag, String expectTags) {
		if (tag == -1) {
			return new UrosProtocolException("No byte found in stream");
		} else if (expectTags == null) {
			return new UrosProtocolException("Unexpected serialize tag '" + (char) tag + "' in stream");
		} else {
			return new UrosProtocolException("Tag '" + expectTags + "' expected, but '" + (char) tag + "' found in stream");
		}
	}

	private UrosProtocolException castError(String srctype, Type desttype) {
		return new UrosProtocolException(srctype + " can't change to " + desttype.toString());
	}

	private UrosProtocolException castError(Object obj, Type type) {
		return new UrosProtocolException(obj.getClass().toString() + " can't change to " + type.toString());
	}

	public void checkTag(int tag, int expectTag) throws UrosProtocolException {
		if (tag != expectTag) {
			throw unexpectedTag(tag, new String(new char[] { (char) expectTag }));
		}
	}

	public void checkTag(int expectTag) throws IOException {
		this.checkTag(this.stream.read(), expectTag);
	}

	public int checkTags(int tag, String expectTags) throws IOException {
		if (expectTags.indexOf(tag) == -1) {
			throw unexpectedTag(tag, expectTags);
		}
		return tag;
	}

	public int checkTags(String expectTags) throws IOException {
		return this.checkTags(this.stream.read(), expectTags);
	}

	private StringBuilder readUntil(int tag) throws IOException {
		StringBuilder sb = new StringBuilder();
		int i = this.stream.read();
		while ((i != tag) && (i != -1)) {
			sb.append((char) i);
			i = this.stream.read();
		}
		return sb;
	}

	@SuppressWarnings({ "fallthrough" })
	public byte readByte(int tag) throws IOException {
		byte result = 0;
		int i = this.stream.read();
		if (i == tag) {
			return result;
		}
		byte sign = 1;
		switch (i) {
		case '-':
			sign = -1; // NO break HERE
		case '+':
			i = this.stream.read();
			break;
		}
		while ((i != tag) && (i != -1)) {
			result *= 10;
			result += (i - '0') * sign;
			i = this.stream.read();
		}
		return result;
	}

	@SuppressWarnings({ "fallthrough" })
	public short readShort(int tag) throws IOException {
		short result = 0;
		int i = this.stream.read();
		if (i == tag) {
			return result;
		}
		short sign = 1;
		switch (i) {
		case '-':
			sign = -1; // NO break HERE
		case '+':
			i = this.stream.read();
			break;
		}
		while ((i != tag) && (i != -1)) {
			result *= 10;
			result += (i - '0') * sign;
			i = this.stream.read();
		}
		return result;
	}

	@SuppressWarnings({ "fallthrough" })
	public int readInt(int tag) throws IOException {
		int result = 0;
		int i = this.stream.read();
		if (i == tag) {
			return result;
		}
		int sign = 1;
		switch (i) {
		case '-':
			sign = -1; // NO break HERE
		case '+':
			i = this.stream.read();
			break;
		}
		while ((i != tag) && (i != -1)) {
			result *= 10;
			result += (i - '0') * sign;
			i = this.stream.read();
		}
		return result;
	}

	@SuppressWarnings({ "fallthrough" })
	public long readLong(int tag) throws IOException {
		long result = 0;
		int i = this.stream.read();
		if (i == tag) {
			return result;
		}
		long sign = 1;
		switch (i) {
		case '-':
			sign = -1; // NO break HERE
		case '+':
			i = this.stream.read();
			break;
		}
		while ((i != tag) && (i != -1)) {
			result *= 10;
			result += (i - '0') * sign;
			i = this.stream.read();
		}
		return result;
	}

	@SuppressWarnings({ "fallthrough" })
	public float readIntAsFloat() throws IOException {
		float result = 0.0f;
		float sign = 1.0f;
		int i = this.stream.read();
		switch (i) {
		case '-':
			sign = -1.0f; // NO BREAK HERE
		case '+':
			i = this.stream.read();
			break;
		}
		while ((i != UrosTags.TagSemicolon) && (i != -1)) {
			result *= 10.0f;
			result += (i - '0') * sign;
			i = this.stream.read();
		}
		return result;
	}

	@SuppressWarnings({ "fallthrough" })
	public double readIntAsDouble() throws IOException {
		double result = 0.0;
		double sign = 1.0;
		int i = this.stream.read();
		switch (i) {
		case '-':
			sign = -1.0; // NO BREAK HERE
		case '+':
			i = this.stream.read();
			break;
		}
		while ((i != UrosTags.TagSemicolon) && (i != -1)) {
			result *= 10.0;
			result += (i - '0') * sign;
			i = this.stream.read();
		}
		return result;
	}

	private float parseFloat(String value) {
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException e) {
			return Float.NaN;
		}
	}

	private float parseFloat(StringBuilder value) {
		return this.parseFloat(value.toString());
	}

	private double parseDouble(String value) {
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return Double.NaN;
		}
	}

	private double parseDouble(StringBuilder value) {
		return this.parseDouble(value.toString());
	}

	private Object readDateAs(Class<?> type) throws IOException {
		int hour = 0, minute = 0, second = 0, nanosecond = 0;
		int year = this.stream.read() - '0';
		year = year * 10 + this.stream.read() - '0';
		year = year * 10 + this.stream.read() - '0';
		year = year * 10 + this.stream.read() - '0';
		int month = this.stream.read() - '0';
		month = month * 10 + this.stream.read() - '0';
		int day = this.stream.read() - '0';
		day = day * 10 + this.stream.read() - '0';
		int tag = this.stream.read();
		if (tag == UrosTags.TagTime) {
			hour = this.stream.read() - '0';
			hour = hour * 10 + this.stream.read() - '0';
			minute = this.stream.read() - '0';
			minute = minute * 10 + this.stream.read() - '0';
			second = this.stream.read() - '0';
			second = second * 10 + this.stream.read() - '0';
			tag = this.stream.read();
			if (tag == UrosTags.TagPoint) {
				nanosecond = this.stream.read() - '0';
				nanosecond = nanosecond * 10 + this.stream.read() - '0';
				nanosecond = nanosecond * 10 + this.stream.read() - '0';
				nanosecond = nanosecond * 1000000;
				tag = this.stream.read();
				if (tag >= '0' && tag <= '9') {
					nanosecond += (tag - '0') * 100000;
					nanosecond += (this.stream.read() - '0') * 10000;
					nanosecond += (this.stream.read() - '0') * 1000;
					tag = this.stream.read();
					if (tag >= '0' && tag <= '9') {
						nanosecond += (tag - '0') * 100;
						nanosecond += (this.stream.read() - '0') * 10;
						nanosecond += this.stream.read() - '0';
						tag = this.stream.read();
					}
				}
			}
		}
		Calendar calendar = Calendar.getInstance(tag == UrosTags.TagUTC ? UrosHelper.UTC : UrosHelper.DefaultTZ);
		calendar.set(year, month - 1, day, hour, minute, second);
		calendar.set(Calendar.MILLISECOND, nanosecond / 1000000);
		if (Timestamp.class.equals(type)) {
			Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
			timestamp.setNanos(nanosecond);
			this.refer.set(timestamp);
			return timestamp;
		}
		this.refer.set(calendar);
		return calendar;
	}

	private Object readTimeAs(Class<?> type) throws IOException {
		int hour = this.stream.read() - '0';
		hour = hour * 10 + this.stream.read() - '0';
		int minute = this.stream.read() - '0';
		minute = minute * 10 + this.stream.read() - '0';
		int second = this.stream.read() - '0';
		second = second * 10 + this.stream.read() - '0';
		int nanosecond = 0;
		int tag = this.stream.read();
		if (tag == UrosTags.TagPoint) {
			nanosecond = this.stream.read() - '0';
			nanosecond = nanosecond * 10 + this.stream.read() - '0';
			nanosecond = nanosecond * 10 + this.stream.read() - '0';
			nanosecond = nanosecond * 1000000;
			tag = this.stream.read();
			if (tag >= '0' && tag <= '9') {
				nanosecond += (tag - '0') * 100000;
				nanosecond += (this.stream.read() - '0') * 10000;
				nanosecond += (this.stream.read() - '0') * 1000;
				tag = this.stream.read();
				if (tag >= '0' && tag <= '9') {
					nanosecond += (tag - '0') * 100;
					nanosecond += (this.stream.read() - '0') * 10;
					nanosecond += this.stream.read() - '0';
					tag = this.stream.read();
				}
			}
		}
		Calendar calendar = Calendar.getInstance(tag == UrosTags.TagUTC ? UrosHelper.UTC : UrosHelper.DefaultTZ);
		calendar.set(1970, 0, 1, hour, minute, second);
		calendar.set(Calendar.MILLISECOND, nanosecond / 1000000);
		if (Timestamp.class.equals(type)) {
			Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
			timestamp.setNanos(nanosecond);
			this.refer.set(timestamp);
			return timestamp;
		}
		this.refer.set(calendar);
		return calendar;
	}

	private static UrosProtocolException badEncoding(int c) {
		return new UrosProtocolException("bad utf-8 encoding at " + ((c < 0) ? "end of stream" : "0x" + Integer.toHexString(c & 0xff)));
	}

	private char readUTF8CharAsChar() throws IOException {
		char u;
		int c = this.stream.read();
		switch (c >>> 4) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7: {
			// 0xxx xxxx
			u = (char) c;
			break;
		}
		case 12:
		case 13: {
			// 110x xxxx 10xx xxxx
			int c2 = this.stream.read();
			u = (char) (((c & 0x1f) << 6) | (c2 & 0x3f));
			break;
		}
		case 14: {
			// 1110 xxxx 10xx xxxx 10xx xxxx
			int c2 = this.stream.read();
			int c3 = this.stream.read();
			u = (char) (((c & 0x0f) << 12) | ((c2 & 0x3f) << 6) | (c3 & 0x3f));
			break;
		}
		default:
			throw badEncoding(c);
		}
		return u;
	}

	@SuppressWarnings({ "fallthrough" })
	private char[] readChars() throws IOException {
		int count = readInt(UrosTags.TagQuote);
		char[] buf = new char[count];
		byte[] b = new byte[count];
		this.stream.read(b, 0, count);
		int p = -1;
		int n = count >> 2;
		for (int i = 0; i < n; ++i) {
			int c = b[++p] & 0xff;
			switch (c >>> 4) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7: {
				// 0xxx xxxx
				buf[i] = (char) c;
				break;
			}
			case 12:
			case 13: {
				// 110x xxxx 10xx xxxx
				int c2 = b[++p] & 0xff;
				buf[i] = (char) (((c & 0x1f) << 6) | (c2 & 0x3f));
				break;
			}
			case 14: {
				// 1110 xxxx 10xx xxxx 10xx xxxx
				int c2 = b[++p] & 0xff;
				int c3 = b[++p] & 0xff;
				buf[i] = (char) (((c & 0x0f) << 12) | ((c2 & 0x3f) << 6) | (c3 & 0x3f));
				break;
			}
			case 15: {
				// 1111 0xxx 10xx xxxx 10xx xxxx 10xx xxxx
				if ((c & 0xf) <= 4) {
					int c2 = b[++p] & 0xff;
					int c3 = b[++p] & 0xff;
					int c4 = b[++p] & 0xff;
					int s = ((c & 0x07) << 18) | ((c2 & 0x3f) << 12) | ((c3 & 0x3f) << 6) | (c4 & 0x3f) - 0x10000;
					if (0 <= s && s <= 0xfffff) {
						buf[i] = (char) (((s >> 10) & 0x03ff) | 0xd800);
						buf[++i] = (char) ((s & 0x03ff) | 0xdc00);
						++n;
						break;
					}
				}
			}
			// NO break here
			default:
				throw badEncoding(c);
			}
		}
		int last = count - 1;
		for (int i = n; i < count; ++i) {
			int c = p < last ? b[++p] & 0xff : this.stream.read();
			switch (c >>> 4) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7: {
				// 0xxx xxxx
				buf[i] = (char) c;
				break;
			}
			case 12:
			case 13: {
				// 110x xxxx 10xx xxxx
				int c2 = p < last ? b[++p] & 0xff : this.stream.read();
				buf[i] = (char) (((c & 0x1f) << 6) | (c2 & 0x3f));
				break;
			}
			case 14: {
				// 1110 xxxx 10xx xxxx 10xx xxxx
				int c2 = p < last ? b[++p] & 0xff : this.stream.read();
				int c3 = p < last ? b[++p] & 0xff : this.stream.read();
				buf[i] = (char) (((c & 0x0f) << 12) | ((c2 & 0x3f) << 6) | (c3 & 0x3f));
				break;
			}
			case 15: {
				// 1111 0xxx 10xx xxxx 10xx xxxx 10xx xxxx
				if ((c & 0xf) <= 4) {
					int c2 = p < last ? b[++p] & 0xff : this.stream.read();
					int c3 = p < last ? b[++p] & 0xff : this.stream.read();
					int c4 = p < last ? b[++p] & 0xff : this.stream.read();
					int s = ((c & 0x07) << 18) | ((c2 & 0x3f) << 12) | ((c3 & 0x3f) << 6) | (c4 & 0x3f) - 0x10000;
					if (0 <= s && s <= 0xfffff) {
						buf[i] = (char) (((s >> 10) & 0x03ff) | 0xd800);
						buf[++i] = (char) ((s & 0x03ff) | 0xdc00);
						break;
					}
				}
			}
			// NO break here
			default:
				throw badEncoding(c);
			}
		}
		this.stream.read();
		return buf;
	}

	private String readCharsAsString() throws IOException {
		return new String(readChars());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map readObjectAsMap(Map map) throws IOException {
		Object c = classref.get(readInt(UrosTags.TagOpenbrace));
		String[] memberNames = membersref.get(c);
		this.refer.set(map);
		int count = memberNames.length;
		for (int i = 0; i < count; ++i) {
			map.put(memberNames[i], deserialize());
		}
		this.stream.read();
		return map;
	}

	private <T> T readMapAsObject(Class<T> type) throws IOException {
		T obj = UrosHelper.newInstance(type);
		if (obj == null) {
			throw new UrosProtocolException("Can not make an instance of type: " + type.toString());
		}
		this.refer.set(obj);
		Map<String, MemberAccessor> members = UrosHelper.getMembers(type, mode);
		int count = readInt(UrosTags.TagOpenbrace);
		for (int i = 0; i < count; ++i) {
			MemberAccessor member = members.get(readString());
			if (member != null) {
				Object value = member.deserializer.read(this, member.cls, member.type);
				try {
					member.set(obj, value);
				} catch (Exception e) {
					throw new UrosProtocolException(e.getMessage());
				}
			} else {
				deserialize();
			}
		}
		this.stream.read();
		return obj;
	}

	private void readClass() throws IOException {
		String className = readCharsAsString();
		int count = readInt(UrosTags.TagOpenbrace);
		String[] memberNames = new String[count];
		for (int i = 0; i < count; ++i) {
			memberNames[i] = readString();
		}
		this.stream.read();
		Type type = UrosHelper.getClass(className);
		Object key = (type.equals(void.class)) ? new Object() : type;
		classref.add(key);
		membersref.put(key, memberNames);
	}

	private Object readRef() throws IOException {
		return refer.read(readIntWithoutTag());
	}

	@SuppressWarnings({ "unchecked" })
	private <T> T readRef(Class<T> type) throws IOException {
		Object obj = readRef();
		Class<?> objType = obj.getClass();
		if (objType.equals(type) || type.isAssignableFrom(objType)) {
			return (T) obj;
		}
		throw this.castError(objType.toString(), type);
	}

	public int readIntWithoutTag() throws IOException {
		return readInt(UrosTags.TagSemicolon);
	}

	public BigInteger readBigIntegerWithoutTag() throws IOException {
		return new BigInteger(readUntil(UrosTags.TagSemicolon).toString(), 10);
	}

	public long readLongWithoutTag() throws IOException {
		return readLong(UrosTags.TagSemicolon);
	}

	public double readDoubleWithoutTag() throws IOException {
		return parseDouble(readUntil(UrosTags.TagSemicolon));
	}

	public double readInfinityWithoutTag() throws IOException {
		return ((this.stream.read() == UrosTags.TagNeg) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
	}

	public Calendar readDateWithoutTag() throws IOException {
		return (Calendar) readDateAs(Calendar.class);
	}

	public Calendar readTimeWithoutTag() throws IOException {
		return (Calendar) readTimeAs(Calendar.class);
	}

	public byte[] readBytesWithoutTag() throws IOException {
		int len = readInt(UrosTags.TagQuote);
		int off = 0;
		byte[] b = new byte[len];
		while (len > 0) {
			int size = this.stream.read(b, off, len);
			off += size;
			len -= size;
		}
		this.stream.read();
		this.refer.set(b);
		return b;
	}

	public String readUTF8CharWithoutTag() throws IOException {
		return new String(new char[] { readUTF8CharAsChar() });
	}

	public String readStringWithoutTag() throws IOException {
		String str = readCharsAsString();
		this.refer.set(str);
		return str;
	}

	public char[] readCharsWithoutTag() throws IOException {
		char[] chars = readChars();
		this.refer.set(chars);
		return chars;
	}

	public UUID readUUIDWithoutTag() throws IOException {
		checkTag(UrosTags.TagOpenbrace);
		char[] buf = new char[36];
		for (int i = 0; i < 36; ++i) {
			buf[i] = (char) this.stream.read();
		}
		checkTag(UrosTags.TagClosebrace);
		UUID uuid = UUID.fromString(new String(buf));
		this.refer.set(uuid);
		return uuid;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList readListWithoutTag() throws IOException {
		int count = readInt(UrosTags.TagOpenbrace);
		ArrayList a = new ArrayList(count);
		this.refer.set(a);
		for (int i = 0; i < count; ++i) {
			a.add(deserialize());
		}
		this.stream.read();
		return a;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public HashMap readMapWithoutTag() throws IOException {
		int count = readInt(UrosTags.TagOpenbrace);
		HashMap map = new HashMap(count);
		this.refer.set(map);
		for (int i = 0; i < count; ++i) {
			Object key = deserialize();
			Object value = deserialize();
			map.put(key, value);
		}
		this.stream.read();
		return map;
	}

	public Object readObjectWithoutTag(Class<?> type) throws IOException {
		Object c = classref.get(readInt(UrosTags.TagOpenbrace));
		String[] memberNames = membersref.get(c);
		int count = memberNames.length;
		if (Class.class.equals(c.getClass())) {
			Class<?> cls = (Class<?>) c;
			if ((type == null) || type.isAssignableFrom(cls)) {
				type = cls;
			}
		}
		if (type == null) {
			HashMap<String, Object> map = new HashMap<String, Object>(count);
			this.refer.set(map);
			for (int i = 0; i < count; ++i) {
				map.put(memberNames[i], deserialize());
			}
			this.stream.read();
			return map;
		} else {
			Object obj = UrosHelper.newInstance(type);
			this.refer.set(obj);
			Map<String, MemberAccessor> members = UrosHelper.getMembers(type, mode);
			for (int i = 0; i < count; ++i) {
				MemberAccessor member = members.get(memberNames[i]);
				if (member != null) {
					Object value = member.deserializer.read(this, member.cls, member.type);
					try {
						member.set(obj, value);
					} catch (Exception e) {
						throw new UrosProtocolException(e.getMessage());
					}
				} else {
					deserialize();
				}
			}
			this.stream.read();
			return obj;
		}
	}

	private Object unserialize(int tag) throws IOException {
		switch (tag) {
		case '0':
			return 0;
		case '1':
			return 1;
		case '2':
			return 2;
		case '3':
			return 3;
		case '4':
			return 4;
		case '5':
			return 5;
		case '6':
			return 6;
		case '7':
			return 7;
		case '8':
			return 8;
		case '9':
			return 9;
		case UrosTags.TagInteger:
			return readIntWithoutTag();
		case UrosTags.TagLong:
			return readBigIntegerWithoutTag();
		case UrosTags.TagDouble:
			return readDoubleWithoutTag();
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagEmpty:
			return "";
		case UrosTags.TagTrue:
			return true;
		case UrosTags.TagFalse:
			return false;
		case UrosTags.TagNaN:
			return Double.NaN;
		case UrosTags.TagInfinity:
			return readInfinityWithoutTag();
		case UrosTags.TagDate:
			return readDateWithoutTag();
		case UrosTags.TagTime:
			return readTimeWithoutTag();
		case UrosTags.TagBytes:
			return readBytesWithoutTag();
		case UrosTags.TagUTF8Char:
			return readUTF8CharWithoutTag();
		case UrosTags.TagString:
			return readStringWithoutTag();
		case UrosTags.TagGuid:
			return readUUIDWithoutTag();
		case UrosTags.TagList:
			return readListWithoutTag();
		case UrosTags.TagMap:
			return readMapWithoutTag();
		case UrosTags.TagClass:
			readClass();
			return readObject(null);
		case UrosTags.TagObject:
			return readObjectWithoutTag(null);
		case UrosTags.TagRef:
			return readRef();
		case UrosTags.TagError:
			throw new UrosProtocolException(readString());
		default:
			throw unexpectedTag(tag);
		}
	}

	public Object deserialize() throws IOException {
		return unserialize(this.stream.read());
	}

	private String tagToString(int tag) throws IOException {
		switch (tag) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case UrosTags.TagInteger:
			return "Integer";
		case UrosTags.TagLong:
			return "BigInteger";
		case UrosTags.TagDouble:
			return "Double";
		case UrosTags.TagNull:
			return "Null";
		case UrosTags.TagEmpty:
			return "Empty String";
		case UrosTags.TagTrue:
			return "Boolean True";
		case UrosTags.TagFalse:
			return "Boolean False";
		case UrosTags.TagNaN:
			return "NaN";
		case UrosTags.TagInfinity:
			return "Infinity";
		case UrosTags.TagDate:
			return "DateTime";
		case UrosTags.TagTime:
			return "DateTime";
		case UrosTags.TagBytes:
			return "Byte[]";
		case UrosTags.TagUTF8Char:
			return "Char";
		case UrosTags.TagString:
			return "String";
		case UrosTags.TagGuid:
			return "Guid";
		case UrosTags.TagList:
			return "IList";
		case UrosTags.TagMap:
			return "IDictionary";
		case UrosTags.TagClass:
			return "Class";
		case UrosTags.TagObject:
			return "Object";
		case UrosTags.TagRef:
			return "Object Reference";
		case UrosTags.TagError:
			throw new UrosProtocolException(readString());
		default:
			throw unexpectedTag(tag);
		}
	}

	private boolean readBooleanWithTag(int tag) throws IOException {
		switch (tag) {
		case '0':
			return false;
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			return true;
		case UrosTags.TagInteger:
			return readIntWithoutTag() != 0;
		case UrosTags.TagLong:
			return !(BigInteger.ZERO.equals(readBigIntegerWithoutTag()));
		case UrosTags.TagDouble:
			return readDoubleWithoutTag() != 0.0;
		case UrosTags.TagEmpty:
			return false;
		case UrosTags.TagTrue:
			return true;
		case UrosTags.TagFalse:
			return false;
		case UrosTags.TagNaN:
			return true;
		case UrosTags.TagInfinity:
			this.stream.read();
			return true;
		case UrosTags.TagUTF8Char:
			return "\00".indexOf(readUTF8CharAsChar()) == -1;
		case UrosTags.TagString:
			return Boolean.parseBoolean(readStringWithoutTag());
		case UrosTags.TagRef:
			return Boolean.parseBoolean(readRef(String.class));
		default:
			throw this.castError(tagToString(tag), boolean.class);
		}
	}

	public boolean readBoolean() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return false;
		default:
			return readBooleanWithTag(tag);
		}
	}

	public Boolean readBooleanObject() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		default:
			return readBooleanWithTag(tag);
		}
	}

	private char readCharWithTag(int tag) throws IOException {
		switch (tag) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			return (char) tag;
		case UrosTags.TagInteger:
			return (char) readIntWithoutTag();
		case UrosTags.TagLong:
			return (char) readLongWithoutTag();
		case UrosTags.TagDouble:
			return (char) Double.valueOf(readDoubleWithoutTag()).intValue();
		case UrosTags.TagUTF8Char:
			return readUTF8CharAsChar();
		case UrosTags.TagString:
			return readStringWithoutTag().charAt(0);
		case UrosTags.TagRef:
			return readRef(String.class).charAt(0);
		default:
			throw this.castError(tagToString(tag), char.class);
		}
	}

	public char readChar() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return (char) 0;
		default:
			return readCharWithTag(tag);
		}
	}

	public Character readCharObject() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		default:
			return readCharWithTag(tag);
		}
	}

	private byte readByteWithTag(int tag) throws IOException {
		switch (tag) {
		case '0':
			return 0;
		case '1':
			return 1;
		case '2':
			return 2;
		case '3':
			return 3;
		case '4':
			return 4;
		case '5':
			return 5;
		case '6':
			return 6;
		case '7':
			return 7;
		case '8':
			return 8;
		case '9':
			return 9;
		case UrosTags.TagInteger:
			return readByte(UrosTags.TagSemicolon);
		case UrosTags.TagLong:
			return readByte(UrosTags.TagSemicolon);
		case UrosTags.TagDouble:
			return Double.valueOf(readDoubleWithoutTag()).byteValue();
		case UrosTags.TagEmpty:
			return 0;
		case UrosTags.TagTrue:
			return 1;
		case UrosTags.TagFalse:
			return 0;
		case UrosTags.TagUTF8Char:
			return Byte.parseByte(readUTF8CharWithoutTag());
		case UrosTags.TagString:
			return Byte.parseByte(readStringWithoutTag());
		case UrosTags.TagRef:
			return Byte.parseByte(readRef(String.class));
		default:
			throw this.castError(tagToString(tag), byte.class);
		}
	}

	public byte readByte() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return 0;
		default:
			return readByteWithTag(tag);
		}
	}

	public Byte readByteObject() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		default:
			return readByteWithTag(tag);
		}
	}

	private short readShortWithTag(int tag) throws IOException {
		switch (tag) {
		case '0':
			return 0;
		case '1':
			return 1;
		case '2':
			return 2;
		case '3':
			return 3;
		case '4':
			return 4;
		case '5':
			return 5;
		case '6':
			return 6;
		case '7':
			return 7;
		case '8':
			return 8;
		case '9':
			return 9;
		case UrosTags.TagInteger:
			return readShort(UrosTags.TagSemicolon);
		case UrosTags.TagLong:
			return readShort(UrosTags.TagSemicolon);
		case UrosTags.TagDouble:
			return Double.valueOf(readDoubleWithoutTag()).shortValue();
		case UrosTags.TagEmpty:
			return 0;
		case UrosTags.TagTrue:
			return 1;
		case UrosTags.TagFalse:
			return 0;
		case UrosTags.TagUTF8Char:
			return Short.parseShort(readUTF8CharWithoutTag());
		case UrosTags.TagString:
			return Short.parseShort(readStringWithoutTag());
		case UrosTags.TagRef:
			return Short.parseShort(readRef(String.class));
		default:
			throw this.castError(tagToString(tag), short.class);
		}
	}

	public short readShort() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return 0;
		default:
			return readShortWithTag(tag);
		}
	}

	public Short readShortObject() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		default:
			return readShortWithTag(tag);
		}
	}

	private int readIntWithTag(int tag) throws IOException {
		switch (tag) {
		case '0':
			return 0;
		case '1':
			return 1;
		case '2':
			return 2;
		case '3':
			return 3;
		case '4':
			return 4;
		case '5':
			return 5;
		case '6':
			return 6;
		case '7':
			return 7;
		case '8':
			return 8;
		case '9':
			return 9;
		case UrosTags.TagInteger:
			return readInt(UrosTags.TagSemicolon);
		case UrosTags.TagLong:
			return readInt(UrosTags.TagSemicolon);
		case UrosTags.TagDouble:
			return Double.valueOf(readDoubleWithoutTag()).intValue();
		case UrosTags.TagEmpty:
			return 0;
		case UrosTags.TagTrue:
			return 1;
		case UrosTags.TagFalse:
			return 0;
		case UrosTags.TagUTF8Char:
			return Integer.parseInt(readUTF8CharWithoutTag());
		case UrosTags.TagString:
			return Integer.parseInt(readStringWithoutTag());
		case UrosTags.TagRef:
			return Integer.parseInt(readRef(String.class));
		default:
			throw this.castError(tagToString(tag), int.class);
		}
	}

	public int readInt() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return 0;
		default:
			return readIntWithTag(tag);
		}
	}

	public Integer readIntObject() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		default:
			return readIntWithTag(tag);
		}
	}

	private long readLongWithTag(int tag) throws IOException {
		switch (tag) {
		case '0':
			return 0L;
		case '1':
			return 1L;
		case '2':
			return 2L;
		case '3':
			return 3L;
		case '4':
			return 4L;
		case '5':
			return 5L;
		case '6':
			return 6L;
		case '7':
			return 7L;
		case '8':
			return 8L;
		case '9':
			return 9L;
		case UrosTags.TagInteger:
			return readLong(UrosTags.TagSemicolon);
		case UrosTags.TagLong:
			return readLong(UrosTags.TagSemicolon);
		case UrosTags.TagDouble:
			return Double.valueOf(readDoubleWithoutTag()).longValue();
		case UrosTags.TagEmpty:
			return 0l;
		case UrosTags.TagTrue:
			return 1l;
		case UrosTags.TagFalse:
			return 0l;
		case UrosTags.TagDate:
			return readDateWithoutTag().getTimeInMillis();
		case UrosTags.TagTime:
			return readTimeWithoutTag().getTimeInMillis();
		case UrosTags.TagUTF8Char:
			return Long.parseLong(readUTF8CharWithoutTag());
		case UrosTags.TagString:
			return Long.parseLong(readStringWithoutTag());
		case UrosTags.TagRef:
			return Long.parseLong(readRef(String.class));
		default:
			throw this.castError(tagToString(tag), long.class);
		}
	}

	public long readLong() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return 0l;
		default:
			return readLongWithTag(tag);
		}
	}

	public Long readLongObject() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		default:
			return readLongWithTag(tag);
		}
	}

	private float readFloatWithTag(int tag) throws IOException {
		switch (tag) {
		case '0':
			return 0.0f;
		case '1':
			return 1.0f;
		case '2':
			return 2.0f;
		case '3':
			return 3.0f;
		case '4':
			return 4.0f;
		case '5':
			return 5.0f;
		case '6':
			return 6.0f;
		case '7':
			return 7.0f;
		case '8':
			return 8.0f;
		case '9':
			return 9.0f;
		case UrosTags.TagInteger:
			return readIntAsFloat();
		case UrosTags.TagLong:
			return readIntAsFloat();
		case UrosTags.TagDouble:
			return parseFloat(readUntil(UrosTags.TagSemicolon));
		case UrosTags.TagEmpty:
			return 0.0f;
		case UrosTags.TagTrue:
			return 1.0f;
		case UrosTags.TagFalse:
			return 0.0f;
		case UrosTags.TagNaN:
			return Float.NaN;
		case UrosTags.TagInfinity:
			return (this.stream.read() == UrosTags.TagPos) ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
		case UrosTags.TagUTF8Char:
			return Float.parseFloat(readUTF8CharWithoutTag());
		case UrosTags.TagString:
			return Float.parseFloat(readStringWithoutTag());
		case UrosTags.TagRef:
			return Float.parseFloat(readRef(String.class));
		default:
			throw this.castError(tagToString(tag), float.class);
		}
	}

	public float readFloat() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return 0.0f;
		default:
			return readFloatWithTag(tag);
		}
	}

	public Float readFloatObject() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		default:
			return readFloatWithTag(tag);
		}
	}

	private double readDoubleWithTag(int tag) throws IOException {
		switch (tag) {
		case '0':
			return 0.0;
		case '1':
			return 1.0;
		case '2':
			return 2.0;
		case '3':
			return 3.0;
		case '4':
			return 4.0;
		case '5':
			return 5.0;
		case '6':
			return 6.0;
		case '7':
			return 7.0;
		case '8':
			return 8.0;
		case '9':
			return 9.0;
		case UrosTags.TagInteger:
			return readIntAsDouble();
		case UrosTags.TagLong:
			return readIntAsDouble();
		case UrosTags.TagDouble:
			return readDoubleWithoutTag();
		case UrosTags.TagEmpty:
			return 0.0;
		case UrosTags.TagTrue:
			return 1.0;
		case UrosTags.TagFalse:
			return 0.0;
		case UrosTags.TagNaN:
			return Double.NaN;
		case UrosTags.TagInfinity:
			return readInfinityWithoutTag();
		case UrosTags.TagUTF8Char:
			return Double.parseDouble(readUTF8CharWithoutTag());
		case UrosTags.TagString:
			return Double.parseDouble(readStringWithoutTag());
		case UrosTags.TagRef:
			return Double.parseDouble(readRef(String.class));
		default:
			throw this.castError(tagToString(tag), double.class);
		}
	}

	public double readDouble() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return 0.0;
		default:
			return readDoubleWithTag(tag);
		}
	}

	public Double readDoubleObject() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		default:
			return readDoubleWithTag(tag);
		}
	}

	public <T> T readEnum(Class<T> type) throws UrosProtocolException {
		String a = null;
		try {
			a = this.readString();
		} catch (Exception e) {
			throw new UrosProtocolException(e.getMessage());
		}
		
		T[] enums = type.getEnumConstants();
		for (T e : enums) {
			if (((Enum<?>) e).name().equals(a)){
				return e;
			}
		}
		throw new UrosProtocolException("bad enum.");
		
//		try {
//			return type.getEnumConstants()[readInt()];
//		} catch (Exception e) {
//			throw new UrosProtocolException(e.getMessage());
//		}
	}

	public String readString() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case '0':
			return "0";
		case '1':
			return "1";
		case '2':
			return "2";
		case '3':
			return "3";
		case '4':
			return "4";
		case '5':
			return "5";
		case '6':
			return "6";
		case '7':
			return "7";
		case '8':
			return "8";
		case '9':
			return "9";
		case UrosTags.TagInteger:
			return readUntil(UrosTags.TagSemicolon).toString();
		case UrosTags.TagLong:
			return readUntil(UrosTags.TagSemicolon).toString();
		case UrosTags.TagDouble:
			return readUntil(UrosTags.TagSemicolon).toString();
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagEmpty:
			return "";
		case UrosTags.TagTrue:
			return "true";
		case UrosTags.TagFalse:
			return "false";
		case UrosTags.TagNaN:
			return "NaN";
		case UrosTags.TagInfinity:
			return (this.stream.read() == UrosTags.TagPos) ? "Infinity" : "-Infinity";
		case UrosTags.TagDate:
			return readDateWithoutTag().toString();
		case UrosTags.TagTime:
			return readTimeWithoutTag().toString();
		case UrosTags.TagUTF8Char:
			return readUTF8CharWithoutTag();
		case UrosTags.TagString:
			return readStringWithoutTag();
		case UrosTags.TagGuid:
			return readUUIDWithoutTag().toString();
		case UrosTags.TagList:
			return readListWithoutTag().toString();
		case UrosTags.TagMap:
			return readMapWithoutTag().toString();
		case UrosTags.TagClass:
			readClass();
			return readObject(null).toString();
		case UrosTags.TagObject:
			return readObjectWithoutTag(null).toString();
		case UrosTags.TagRef: {
			Object obj = readRef();
			if (obj instanceof char[]) {
				return new String((char[]) obj);
			}
			return obj.toString();
		}
		default:
			throw this.castError(tagToString(tag), String.class);
		}
	}

	public BigInteger readBigInteger() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case '0':
			return BigInteger.ZERO;
		case '1':
			return BigInteger.ONE;
		case '2':
			return BigInteger.valueOf(2);
		case '3':
			return BigInteger.valueOf(3);
		case '4':
			return BigInteger.valueOf(4);
		case '5':
			return BigInteger.valueOf(5);
		case '6':
			return BigInteger.valueOf(6);
		case '7':
			return BigInteger.valueOf(7);
		case '8':
			return BigInteger.valueOf(8);
		case '9':
			return BigInteger.valueOf(9);
		case UrosTags.TagInteger:
			return BigInteger.valueOf(readIntWithoutTag());
		case UrosTags.TagLong:
			return readBigIntegerWithoutTag();
		case UrosTags.TagDouble:
			return BigInteger.valueOf(Double.valueOf(readDoubleWithoutTag()).longValue());
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagEmpty:
			return BigInteger.ZERO;
		case UrosTags.TagTrue:
			return BigInteger.ONE;
		case UrosTags.TagFalse:
			return BigInteger.ZERO;
		case UrosTags.TagDate:
			return BigInteger.valueOf(readDateWithoutTag().getTimeInMillis());
		case UrosTags.TagTime:
			return BigInteger.valueOf(readTimeWithoutTag().getTimeInMillis());
		case UrosTags.TagUTF8Char:
			return new BigInteger(readUTF8CharWithoutTag());
		case UrosTags.TagString:
			return new BigInteger(readStringWithoutTag());
		case UrosTags.TagRef:
			return new BigInteger(readRef(String.class));
		default:
			throw this.castError(tagToString(tag), BigInteger.class);
		}
	}

	public Date readDate() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case '0':
			return new Date(0l);
		case '1':
			return new Date(1l);
		case '2':
			return new Date(2l);
		case '3':
			return new Date(3l);
		case '4':
			return new Date(4l);
		case '5':
			return new Date(5l);
		case '6':
			return new Date(6l);
		case '7':
			return new Date(7l);
		case '8':
			return new Date(8l);
		case '9':
			return new Date(9l);
		case UrosTags.TagInteger:
		case UrosTags.TagLong:
			return new Date(readLongWithoutTag());
		case UrosTags.TagDouble:
			return new Date(Double.valueOf(readDoubleWithoutTag()).longValue());
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagEmpty:
			return null;
		case UrosTags.TagDate:
			return new Date(readDateWithoutTag().getTimeInMillis());
		case UrosTags.TagTime:
			return new Date(readTimeWithoutTag().getTimeInMillis());
		case UrosTags.TagRef: {
			Object obj = readRef();
			if (obj instanceof Calendar) {
				return new Date(((Calendar) obj).getTimeInMillis());
			}
			if (obj instanceof Timestamp) {
				return new Date(((Timestamp) obj).getTime());
			}
			throw this.castError(obj, Date.class);
		}
		default:
			throw this.castError(tagToString(tag), Date.class);
		}
	}

	public Time readTime() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case '0':
			return new Time(0l);
		case '1':
			return new Time(1l);
		case '2':
			return new Time(2l);
		case '3':
			return new Time(3l);
		case '4':
			return new Time(4l);
		case '5':
			return new Time(5l);
		case '6':
			return new Time(6l);
		case '7':
			return new Time(7l);
		case '8':
			return new Time(8l);
		case '9':
			return new Time(9l);
		case UrosTags.TagInteger:
		case UrosTags.TagLong:
			return new Time(readLongWithoutTag());
		case UrosTags.TagDouble:
			return new Time(Double.valueOf(readDoubleWithoutTag()).longValue());
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagEmpty:
			return null;
		case UrosTags.TagDate:
			return new Time(readDateWithoutTag().getTimeInMillis());
		case UrosTags.TagTime:
			return new Time(readTimeWithoutTag().getTimeInMillis());
		case UrosTags.TagRef: {
			Object obj = readRef();
			if (obj instanceof Calendar) {
				return new Time(((Calendar) obj).getTimeInMillis());
			}
			if (obj instanceof Timestamp) {
				return new Time(((Timestamp) obj).getTime());
			}
			throw this.castError(obj, Time.class);
		}
		default:
			throw this.castError(tagToString(tag), Time.class);
		}
	}

	public java.util.Date readDateTime() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case '0':
			return new java.util.Date(0l);
		case '1':
			return new java.util.Date(1l);
		case '2':
			return new java.util.Date(2l);
		case '3':
			return new java.util.Date(3l);
		case '4':
			return new java.util.Date(4l);
		case '5':
			return new java.util.Date(5l);
		case '6':
			return new java.util.Date(6l);
		case '7':
			return new java.util.Date(7l);
		case '8':
			return new java.util.Date(8l);
		case '9':
			return new java.util.Date(9l);
		case UrosTags.TagInteger:
		case UrosTags.TagLong:
			return new java.util.Date(readLongWithoutTag());
		case UrosTags.TagDouble:
			return new java.util.Date(Double.valueOf(readDoubleWithoutTag()).longValue());
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagEmpty:
			return null;
		case UrosTags.TagDate:
			return new java.util.Date(readDateWithoutTag().getTimeInMillis());
		case UrosTags.TagTime:
			return new java.util.Date(readTimeWithoutTag().getTimeInMillis());
		case UrosTags.TagRef: {
			Object obj = readRef();
			if (obj instanceof Calendar) {
				return new java.util.Date(((Calendar) obj).getTimeInMillis());
			}
			if (obj instanceof Timestamp) {
				return new java.util.Date(((Timestamp) obj).getTime());
			}
			throw this.castError(obj, java.util.Date.class);
		}
		default:
			throw this.castError(tagToString(tag), java.util.Date.class);
		}
	}

	public Timestamp readTimestamp() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case '0':
			return new Timestamp(0l);
		case '1':
			return new Timestamp(1l);
		case '2':
			return new Timestamp(2l);
		case '3':
			return new Timestamp(3l);
		case '4':
			return new Timestamp(4l);
		case '5':
			return new Timestamp(5l);
		case '6':
			return new Timestamp(6l);
		case '7':
			return new Timestamp(7l);
		case '8':
			return new Timestamp(8l);
		case '9':
			return new Timestamp(9l);
		case UrosTags.TagInteger:
		case UrosTags.TagLong:
			return new Timestamp(readLongWithoutTag());
		case UrosTags.TagDouble:
			return new Timestamp(Double.valueOf(readDoubleWithoutTag()).longValue());
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagEmpty:
			return null;
		case UrosTags.TagDate:
			return (Timestamp) readDateAs(Timestamp.class);
		case UrosTags.TagTime:
			return (Timestamp) readTimeAs(Timestamp.class);
		case UrosTags.TagRef: {
			Object obj = readRef();
			if (obj instanceof Calendar) {
				return new Timestamp(((Calendar) obj).getTimeInMillis());
			}
			if (obj instanceof Timestamp) {
				return (Timestamp) obj;
			}
			throw this.castError(obj, Timestamp.class);
		}
		default:
			throw this.castError(tagToString(tag), Timestamp.class);
		}
	}

	public Calendar readCalendar() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9': {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(tag - '0');
			return calendar;
		}
		case UrosTags.TagInteger:
		case UrosTags.TagLong: {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(readLongWithoutTag());
			return calendar;
		}
		case UrosTags.TagDouble: {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(Double.valueOf(readDoubleWithoutTag()).longValue());
			return calendar;
		}
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagEmpty:
			return null;
		case UrosTags.TagDate:
			return readDateWithoutTag();
		case UrosTags.TagTime:
			return readTimeWithoutTag();
		case UrosTags.TagRef: {
			Object obj = readRef();
			if (obj instanceof Calendar) {
				return (Calendar) obj;
			}
			if (obj instanceof Timestamp) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(((Timestamp) obj).getTime());
				return calendar;
			}
			throw this.castError(obj, Calendar.class);
		}
		default:
			throw this.castError(tagToString(tag), Calendar.class);
		}
	}

	public BigDecimal readBigDecimal() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case '0':
			return BigDecimal.ZERO;
		case '1':
			return BigDecimal.ONE;
		case '2':
			return BigDecimal.valueOf(2);
		case '3':
			return BigDecimal.valueOf(3);
		case '4':
			return BigDecimal.valueOf(4);
		case '5':
			return BigDecimal.valueOf(5);
		case '6':
			return BigDecimal.valueOf(6);
		case '7':
			return BigDecimal.valueOf(7);
		case '8':
			return BigDecimal.valueOf(8);
		case '9':
			return BigDecimal.valueOf(9);
		case UrosTags.TagInteger:
			return new BigDecimal(readIntWithoutTag());
		case UrosTags.TagLong:
			return new BigDecimal(readLongWithoutTag());
		case UrosTags.TagDouble:
			return new BigDecimal(readUntil(UrosTags.TagSemicolon).toString());
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagEmpty:
			return BigDecimal.ZERO;
		case UrosTags.TagTrue:
			return BigDecimal.ONE;
		case UrosTags.TagFalse:
			return BigDecimal.ZERO;
		case UrosTags.TagUTF8Char:
			return new BigDecimal(readUTF8CharWithoutTag());
		case UrosTags.TagString:
			return new BigDecimal(readStringWithoutTag());
		case UrosTags.TagRef:
			return new BigDecimal(readRef(String.class));
		default:
			throw this.castError(tagToString(tag), BigDecimal.class);
		}
	}

	public StringBuilder readStringBuilder() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case '0':
			return new StringBuilder("0");
		case '1':
			return new StringBuilder("1");
		case '2':
			return new StringBuilder("2");
		case '3':
			return new StringBuilder("3");
		case '4':
			return new StringBuilder("4");
		case '5':
			return new StringBuilder("5");
		case '6':
			return new StringBuilder("6");
		case '7':
			return new StringBuilder("7");
		case '8':
			return new StringBuilder("8");
		case '9':
			return new StringBuilder("9");
		case UrosTags.TagInteger:
			return readUntil(UrosTags.TagSemicolon);
		case UrosTags.TagLong:
			return readUntil(UrosTags.TagSemicolon);
		case UrosTags.TagDouble:
			return readUntil(UrosTags.TagSemicolon);
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagEmpty:
			return new StringBuilder();
		case UrosTags.TagTrue:
			return new StringBuilder("true");
		case UrosTags.TagFalse:
			return new StringBuilder("false");
		case UrosTags.TagNaN:
			return new StringBuilder("NaN");
		case UrosTags.TagInfinity:
			return new StringBuilder((this.stream.read() == UrosTags.TagPos) ? "Infinity" : "-Infinity");
		case UrosTags.TagDate:
			return new StringBuilder(readDateWithoutTag().toString());
		case UrosTags.TagTime:
			return new StringBuilder(readTimeWithoutTag().toString());
		case UrosTags.TagUTF8Char:
			return new StringBuilder(1).append(readUTF8CharAsChar());
		case UrosTags.TagString:
			return new StringBuilder(readStringWithoutTag());
		case UrosTags.TagGuid:
			return new StringBuilder(readUUIDWithoutTag().toString());
		case UrosTags.TagRef: {
			Object obj = readRef();
			if (obj instanceof char[]) {
				return new StringBuilder(new String((char[]) obj));
			}
			return new StringBuilder(obj.toString());
		}
		default:
			throw this.castError(tagToString(tag), StringBuilder.class);
		}
	}

	public StringBuffer readStringBuffer() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case '0':
			return new StringBuffer("0");
		case '1':
			return new StringBuffer("1");
		case '2':
			return new StringBuffer("2");
		case '3':
			return new StringBuffer("3");
		case '4':
			return new StringBuffer("4");
		case '5':
			return new StringBuffer("5");
		case '6':
			return new StringBuffer("6");
		case '7':
			return new StringBuffer("7");
		case '8':
			return new StringBuffer("8");
		case '9':
			return new StringBuffer("9");
		case UrosTags.TagInteger:
			return new StringBuffer(readUntil(UrosTags.TagSemicolon));
		case UrosTags.TagLong:
			return new StringBuffer(readUntil(UrosTags.TagSemicolon));
		case UrosTags.TagDouble:
			return new StringBuffer(readUntil(UrosTags.TagSemicolon));
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagEmpty:
			return new StringBuffer();
		case UrosTags.TagTrue:
			return new StringBuffer("true");
		case UrosTags.TagFalse:
			return new StringBuffer("false");
		case UrosTags.TagNaN:
			return new StringBuffer("NaN");
		case UrosTags.TagInfinity:
			return new StringBuffer((this.stream.read() == UrosTags.TagPos) ? "Infinity" : "-Infinity");
		case UrosTags.TagDate:
			return new StringBuffer(readDateWithoutTag().toString());
		case UrosTags.TagTime:
			return new StringBuffer(readTimeWithoutTag().toString());
		case UrosTags.TagUTF8Char:
			return new StringBuffer(1).append(readUTF8CharAsChar());
		case UrosTags.TagString:
			return new StringBuffer(readStringWithoutTag());
		case UrosTags.TagGuid:
			return new StringBuffer(readUUIDWithoutTag().toString());
		case UrosTags.TagRef: {
			Object obj = readRef();
			if (obj instanceof char[]) {
				return new StringBuffer(new String((char[]) obj));
			}
			return new StringBuffer(obj.toString());
		}
		default:
			throw this.castError(tagToString(tag), StringBuffer.class);
		}
	}

	public UUID readUUID() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagEmpty:
			return null;
		case UrosTags.TagBytes:
			return UUID.nameUUIDFromBytes(readBytesWithoutTag());
		case UrosTags.TagGuid:
			return readUUIDWithoutTag();
		case UrosTags.TagString:
			return UUID.fromString(readStringWithoutTag());
		case UrosTags.TagRef: {
			Object obj = readRef();
			if (obj instanceof UUID) {
				return (UUID) obj;
			}
			if (obj instanceof byte[]) {
				return UUID.nameUUIDFromBytes((byte[]) obj);
			}
			if (obj instanceof String) {
				return UUID.fromString((String) obj);
			}
			if (obj instanceof char[]) {
				return UUID.fromString(new String((char[]) obj));
			}
			throw this.castError(obj, UUID.class);
		}
		default:
			throw this.castError(tagToString(tag), UUID.class);
		}
	}

	public void readArray(Type[] types, Object[] a, int count) throws IOException {
		this.refer.set(a);
		for (int i = 0; i < count; ++i) {
			a[i] = deserialize(types[i]);
		}
		this.stream.read();
	}

	public Object[] readArray(int count) throws IOException {
		Object[] a = new Object[count];
		this.refer.set(a);
		for (int i = 0; i < count; ++i) {
			a[i] = deserialize();
		}
		this.stream.read();
		return a;
	}

	public Object[] readObjectArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList:
			return readArray(readInt(UrosTags.TagOpenbrace));
		case UrosTags.TagRef:
			return (Object[]) readRef();
		default:
			throw this.castError(tagToString(tag), Object[].class);
		}
	}

	public boolean[] readBooleanArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			boolean[] a = new boolean[count];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readBoolean();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef:
			return (boolean[]) readRef();
		default:
			throw this.castError(tagToString(tag), boolean[].class);
		}
	}

	public char[] readCharArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagUTF8Char:
			return new char[] { readUTF8CharAsChar() };
		case UrosTags.TagString:
			return readCharsWithoutTag();
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			char[] a = new char[count];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readChar();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef: {
			Object obj = readRef();
			if (obj instanceof char[]) {
				return (char[]) obj;
			}
			if (obj instanceof String) {
				return ((String) obj).toCharArray();
			}
			throw this.castError(obj, char[].class);
		}
		default:
			throw this.castError(tagToString(tag), char[].class);
		}
	}

	public byte[] readByteArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagEmpty:
			return new byte[0];
		case UrosTags.TagUTF8Char:
			return readUTF8CharWithoutTag().getBytes("UTF-8");
		case UrosTags.TagString:
			return readStringWithoutTag().getBytes("UTF-8");
		case UrosTags.TagBytes:
			return readBytesWithoutTag();
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			byte[] a = new byte[count];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readByte();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef: {
			Object obj = readRef();
			if (obj instanceof byte[]) {
				return (byte[]) obj;
			}
			if (obj instanceof String) {
				return ((String) obj).getBytes("UTF-8");
			}
			throw this.castError(obj, byte[].class);
		}
		default:
			throw this.castError(tagToString(tag), byte[].class);
		}
	}

	public short[] readShortArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			short[] a = new short[count];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readShort();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef:
			return (short[]) readRef();
		default:
			throw this.castError(tagToString(tag), short[].class);
		}
	}

	public int[] readIntArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			int[] a = new int[count];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readInt();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef:
			return (int[]) readRef();
		default:
			throw this.castError(tagToString(tag), int[].class);
		}
	}

	public long[] readLongArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			long[] a = new long[count];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readLong();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef:
			return (long[]) readRef();
		default:
			throw this.castError(tagToString(tag), long[].class);
		}
	}

	public float[] readFloatArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			float[] a = new float[count];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readFloat();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef:
			return (float[]) readRef();
		default:
			throw this.castError(tagToString(tag), float[].class);
		}
	}

	public double[] readDoubleArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			double[] a = new double[count];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readDouble();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef:
			return (double[]) readRef();
		default:
			throw this.castError(tagToString(tag), double[].class);
		}
	}

	public String[] readStringArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			String[] a = new String[count];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readString();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef:
			return (String[]) readRef();
		default:
			throw this.castError(tagToString(tag), String[].class);
		}
	}

	public BigInteger[] readBigIntegerArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			BigInteger[] a = new BigInteger[count];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readBigInteger();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef:
			return (BigInteger[]) readRef();
		default:
			throw this.castError(tagToString(tag), BigInteger[].class);
		}
	}

	public Date[] readDateArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			Date[] a = new Date[count];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readDate();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef:
			return (Date[]) readRef();
		default:
			throw this.castError(tagToString(tag), Date[].class);
		}
	}

	public Time[] readTimeArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			Time[] a = new Time[count];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readTime();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef:
			return (Time[]) readRef();
		default:
			throw this.castError(tagToString(tag), Time[].class);
		}
	}

	public Timestamp[] readTimestampArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			Timestamp[] a = new Timestamp[count];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readTimestamp();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef:
			return (Timestamp[]) readRef();
		default:
			throw this.castError(tagToString(tag), Timestamp[].class);
		}
	}

	public java.util.Date[] readDateTimeArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			java.util.Date[] a = new java.util.Date[count];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readDateTime();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef:
			return (java.util.Date[]) readRef();
		default:
			throw this.castError(tagToString(tag), java.util.Date[].class);
		}
	}

	public Calendar[] readCalendarArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			Calendar[] a = new Calendar[count];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readCalendar();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef:
			return (Calendar[]) readRef();
		default:
			throw this.castError(tagToString(tag), Calendar[].class);
		}
	}

	public BigDecimal[] readBigDecimalArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			BigDecimal[] a = new BigDecimal[count];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readBigDecimal();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef:
			return (BigDecimal[]) readRef();
		default:
			throw this.castError(tagToString(tag), BigDecimal[].class);
		}
	}

	public StringBuilder[] readStringBuilderArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			StringBuilder[] a = new StringBuilder[count];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readStringBuilder();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef:
			return (StringBuilder[]) readRef();
		default:
			throw this.castError(tagToString(tag), StringBuilder[].class);
		}
	}

	public StringBuffer[] readStringBufferArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			StringBuffer[] a = new StringBuffer[count];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readStringBuffer();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef:
			return (StringBuffer[]) readRef();
		default:
			throw this.castError(tagToString(tag), StringBuffer[].class);
		}
	}

	public UUID[] readUUIDArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			UUID[] a = new UUID[count];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readUUID();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef:
			return (UUID[]) readRef();
		default:
			throw this.castError(tagToString(tag), UUID[].class);
		}
	}

	public char[][] readCharsArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			char[][] a = new char[count][];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readCharArray();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef:
			return (char[][]) readRef();
		default:
			throw this.castError(tagToString(tag), char[][].class);
		}
	}

	public byte[][] readBytesArray() throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			byte[][] a = new byte[count][];
			this.refer.set(a);
			for (int i = 0; i < count; ++i) {
				a[i] = readByteArray();
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef:
			return (byte[][]) readRef();
		default:
			throw this.castError(tagToString(tag), byte[][].class);
		}
	}

	@SuppressWarnings({ "unchecked" })
	public <T> T[] readOtherTypeArray(Class<T> componentClass, Type componentType) throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			T[] a = (T[]) Array.newInstance(componentClass, count);
			this.refer.set(a);
			UrosDeserializer deserializer = DeserializerFactory.get(componentClass);
			for (int i = 0; i < count; ++i) {
				a[i] = (T) deserializer.read(this, componentClass, componentType);
			}
			this.stream.read();
			return a;
		}
		case UrosTags.TagRef:
			return (T[]) readRef();
		default:
			throw this.castError(tagToString(tag), Array.newInstance(componentClass, 0).getClass());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public AtomicReference<?> readAtomicReference(Type type) throws IOException {
		return new AtomicReference(deserialize(type));
	}

	public <T> AtomicReferenceArray<T> readAtomicReferenceArray(Class<T> componentClass, Type componentType) throws IOException {
		return new AtomicReferenceArray<T>(readOtherTypeArray(componentClass, componentType));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection readCollection(Class<?> cls, Type type) throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList: {
			int count = readInt(UrosTags.TagOpenbrace);
			Collection values = (Collection) UrosHelper.newInstance(cls);
			this.refer.set(values);
			Type componentType;
			Class<?> componentClass;
			if (type instanceof ParameterizedType) {
				componentType = ((ParameterizedType) type).getActualTypeArguments()[0];
				componentClass = UrosHelper.toClass(componentType);
			} else {
				componentType = Object.class;
				componentClass = Object.class;
			}
			UrosDeserializer deserializer = DeserializerFactory.get(componentClass);
			for (int i = 0; i < count; ++i) {
				values.add(deserializer.read(this, componentClass, componentType));
			}
			this.stream.read();
			return values;
		}
		case UrosTags.TagRef:
			return (Collection) readRef();
		default:
			throw this.castError(tagToString(tag), cls);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map readListAsMap(Class<?> cls, Type type) throws IOException {
		int count = readInt(UrosTags.TagOpenbrace);
		Map m = (Map) UrosHelper.newInstance(cls);
		this.refer.set(m);
		if (count > 0) {
			Type keyType, valueType;
			Class<?> keyClass, valueClass;
			if (type instanceof ParameterizedType) {
				Type[] argsType = ((ParameterizedType) type).getActualTypeArguments();
				keyType = argsType[0];
				valueType = argsType[1];
				keyClass = UrosHelper.toClass(keyType);
				valueClass = UrosHelper.toClass(valueType);
			} else {
				valueType = Object.class;
				keyClass = Object.class;
				valueClass = Object.class;
			}
			if (keyClass.equals(int.class) && keyClass.equals(Integer.class) && keyClass.equals(String.class) && keyClass.equals(Object.class)) {
				throw this.castError(tagToString(UrosTags.TagList), cls);
			}
			UrosDeserializer valueDeserializer = DeserializerFactory.get(valueClass);
			for (int i = 0; i < count; ++i) {
				Object key = (keyClass.equals(String.class) ? String.valueOf(i) : i);
				Object value = valueDeserializer.read(this, valueClass, valueType);
				m.put(key, value);
			}
		}
		this.stream.read();
		return m;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map readMapWithoutTag(Class<?> cls, Type type) throws IOException {
		int count = readInt(UrosTags.TagOpenbrace);
		Map m = (Map) UrosHelper.newInstance(cls);
		this.refer.set(m);
		Type keyType, valueType;
		Class<?> keyClass, valueClass;
		if (type instanceof ParameterizedType) {
			Type[] argsType = ((ParameterizedType) type).getActualTypeArguments();
			keyType = argsType[0];
			valueType = argsType[1];
			keyClass = UrosHelper.toClass(keyType);
			valueClass = UrosHelper.toClass(valueType);
		} else {
			keyType = Object.class;
			valueType = Object.class;
			keyClass = Object.class;
			valueClass = Object.class;
		}
		UrosDeserializer keyDeserializer = DeserializerFactory.get(keyClass);
		UrosDeserializer valueDeserializer = DeserializerFactory.get(valueClass);
		for (int i = 0; i < count; ++i) {
			Object key = keyDeserializer.read(this, keyClass, keyType);
			Object value = valueDeserializer.read(this, valueClass, valueType);
			m.put(key, value);
		}
		this.stream.read();
		return m;
	}

	@SuppressWarnings({ "rawtypes" })
	public Map readMap(Class<?> cls, Type type) throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagList:
			return this.readListAsMap(cls, type);
		case UrosTags.TagMap:
			return this.readMapWithoutTag(cls, type);
		case UrosTags.TagClass:
			readClass();
			return this.readMap(cls, type);
		case UrosTags.TagObject:
			return this.readObjectAsMap((Map) UrosHelper.newInstance(cls));
		case UrosTags.TagRef:
			return (Map) this.readRef();
		default:
			throw this.castError(tagToString(tag), cls);
		}
	}

	public Object readObject(Class<?> type) throws IOException {
		int tag = this.stream.read();
		switch (tag) {
		case UrosTags.TagNull:
			return null;
		case UrosTags.TagMap:
			return this.readMapAsObject(type);
		case UrosTags.TagClass:
			readClass();
			return this.readObject(type);
		case UrosTags.TagObject:
			return this.readObjectWithoutTag(type);
		case UrosTags.TagRef:
			return this.readRef(type);
		default:
			throw this.castError(tagToString(tag), type);
		}
	}

	public Object deserialize(Type type) throws IOException {
		if (type == null) {
			return this.deserialize();
		}
		Class<?> cls = UrosHelper.toClass(type);
		return this.deserialize(cls, type);
	}

	@SuppressWarnings({ "unchecked" })
	public <T> T deserialize(Class<T> type) throws IOException {
		return (T) this.deserialize(type, type);
	}

	private Object deserialize(Class<?> cls, Type type) throws IOException {
		return DeserializerFactory.get(cls).read(this, cls, type);
	}

	public ByteBufferStream readRaw() throws IOException {
		ByteBufferStream rs = new ByteBufferStream();
		this.readRaw(rs.getOutputStream());
		rs.flip();
		return rs;
	}

	public void readRaw(OutputStream os) throws IOException {
		this.readRaw(os, this.stream.read());
	}

	private void readRaw(OutputStream os, int tag) throws IOException {
		os.write(tag);
		switch (tag) {
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case UrosTags.TagNull:
		case UrosTags.TagEmpty:
		case UrosTags.TagTrue:
		case UrosTags.TagFalse:
		case UrosTags.TagNaN:
			break;
		case UrosTags.TagInfinity:
			os.write(this.stream.read());
			break;
		case UrosTags.TagInteger:
		case UrosTags.TagLong:
		case UrosTags.TagDouble:
		case UrosTags.TagRef:
			this.readNumberRaw(os);
			break;
		case UrosTags.TagDate:
		case UrosTags.TagTime:
			this.readDateTimeRaw(os);
			break;
		case UrosTags.TagUTF8Char:
			this.readUTF8CharRaw(os);
			break;
		case UrosTags.TagBytes:
			this.readBytesRaw(os);
			break;
		case UrosTags.TagString:
			this.readStringRaw(os);
			break;
		case UrosTags.TagGuid:
			this.readGuidRaw(os);
			break;
		case UrosTags.TagList:
		case UrosTags.TagMap:
		case UrosTags.TagObject:
			this.readComplexRaw(os);
			break;
		case UrosTags.TagClass:
			this.readComplexRaw(os);
			this.readRaw(os);
			break;
		case UrosTags.TagError:
			this.readRaw(os);
			break;
		case -1:
			throw new UrosProtocolException("No byte found in stream");
		default:
			throw new UrosProtocolException("Unexpected serialize tag '" + (char) tag + "' in stream");
		}
	}

	private void readNumberRaw(OutputStream os) throws IOException {
		int tag;
		do {
			tag = this.stream.read();
			os.write(tag);
		} while (tag != UrosTags.TagSemicolon);
	}

	private void readDateTimeRaw(OutputStream os) throws IOException {
		int tag;
		do {
			tag = this.stream.read();
			os.write(tag);
		} while (tag != UrosTags.TagSemicolon && tag != UrosTags.TagUTC);
	}

	private void readUTF8CharRaw(OutputStream os) throws IOException {
		int tag = this.stream.read();
		switch (tag >>> 4) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7: {
			// 0xxx xxxx
			os.write(tag);
			break;
		}
		case 12:
		case 13: {
			// 110x xxxx 10xx xxxx
			os.write(tag);
			os.write(this.stream.read());
			break;
		}
		case 14: {
			// 1110 xxxx 10xx xxxx 10xx xxxx
			os.write(tag);
			os.write(this.stream.read());
			os.write(this.stream.read());
			break;
		}
		default:
			throw badEncoding(tag);
		}
	}

	private void readBytesRaw(OutputStream os) throws IOException {
		int len = 0;
		int tag = '0';
		do {
			len *= 10;
			len += tag - '0';
			tag = this.stream.read();
			os.write(tag);
		} while (tag != UrosTags.TagQuote);
		int off = 0;
		byte[] b = new byte[len];
		while (len > 0) {
			int size = this.stream.read(b, off, len);
			off += size;
			len -= size;
		}
		os.write(b);
		os.write(this.stream.read());
	}

	@SuppressWarnings({ "fallthrough" })
	private void readStringRaw(OutputStream os) throws IOException {
		int count = 0;
		int tag = '0';
		do {
			count *= 10;
			count += tag - '0';
			tag = this.stream.read();
			os.write(tag);
		} while (tag != UrosTags.TagQuote);
		for (int i = 0; i < count; ++i) {
			tag = this.stream.read();
			switch (tag >>> 4) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7: {
				// 0xxx xxxx
				os.write(tag);
				break;
			}
			case 12:
			case 13: {
				// 110x xxxx 10xx xxxx
				os.write(tag);
				os.write(this.stream.read());
				break;
			}
			case 14: {
				// 1110 xxxx 10xx xxxx 10xx xxxx
				os.write(tag);
				os.write(this.stream.read());
				os.write(this.stream.read());
				break;
			}
			case 15: {
				// 1111 0xxx 10xx xxxx 10xx xxxx 10xx xxxx
				if ((tag & 0xf) <= 4) {
					os.write(tag);
					os.write(this.stream.read());
					os.write(this.stream.read());
					os.write(this.stream.read());
					++i;
					break;
				}
			}
			// No break here
			default:
				throw badEncoding(tag);
			}
		}
		os.write(this.stream.read());
	}

	private void readGuidRaw(OutputStream os) throws IOException {
		int len = 38;
		int off = 0;
		byte[] b = new byte[len];
		while (len > 0) {
			int size = this.stream.read(b, off, len);
			off += size;
			len -= size;
		}
		os.write(b);
	}

	private void readComplexRaw(OutputStream os) throws IOException {
		int tag;
		do {
			tag = this.stream.read();
			os.write(tag);
		} while (tag != UrosTags.TagOpenbrace);
		while ((tag = this.stream.read()) != UrosTags.TagClosebrace) {
			readRaw(os, tag);
		}
		os.write(tag);
	}

	public void reset() {
		this.refer.reset();
		this.classref.clear();
		this.membersref.clear();
	}
}
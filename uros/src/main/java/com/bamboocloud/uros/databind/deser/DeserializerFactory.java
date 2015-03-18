package com.bamboocloud.uros.databind.deser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSequentialList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public final class DeserializerFactory {
    private static final ConcurrentHashMap<Class<?>, UrosDeserializer> deserializers = new ConcurrentHashMap<Class<?>, UrosDeserializer>();
    static {
        deserializers.put(void.class, DefaultDeserializer.instance);
        deserializers.put(boolean.class, BooleanDeserializer.instance);
        deserializers.put(char.class, CharDeserializer.instance);
        deserializers.put(byte.class, ByteDeserializer.instance);
        deserializers.put(short.class, ShortDeserializer.instance);
        deserializers.put(int.class, IntDeserializer.instance);
        deserializers.put(long.class, LongDeserializer.instance);
        deserializers.put(float.class, FloatDeserializer.instance);
        deserializers.put(double.class, DoubleDeserializer.instance);
        deserializers.put(Object.class, DefaultDeserializer.instance);
        deserializers.put(Void.class, DefaultDeserializer.instance);
        deserializers.put(Boolean.class, BooleanObjectDeserializer.instance);
        deserializers.put(Character.class, CharObjectDeserializer.instance);
        deserializers.put(Byte.class, ByteObjectDeserializer.instance);
        deserializers.put(Short.class, ShortObjectDeserializer.instance);
        deserializers.put(Integer.class, IntObjectDeserializer.instance);
        deserializers.put(Long.class, LongObjectDeserializer.instance);
        deserializers.put(Float.class, FloatObjectDeserializer.instance);
        deserializers.put(Double.class, DoubleObjectDeserializer.instance);
        deserializers.put(String.class, StringDeserializer.instance);
        deserializers.put(BigInteger.class, BigIntegerDeserializer.instance);
        deserializers.put(Date.class, DateDeserializer.instance);
        deserializers.put(Time.class, TimeDeserializer.instance);
        deserializers.put(Timestamp.class, TimestampDeserializer.instance);
        deserializers.put(java.util.Date.class, DateTimeDeserializer.instance);
        deserializers.put(Calendar.class, CalendarDeserializer.instance);
        deserializers.put(BigDecimal.class, BigDecimalDeserializer.instance);
        deserializers.put(StringBuilder.class, StringBuilderDeserializer.instance);
        deserializers.put(StringBuffer.class, StringBufferDeserializer.instance);
        deserializers.put(UUID.class, UUIDDeserializer.instance);
        deserializers.put(boolean[].class, BooleanArrayDeserializer.instance);
        deserializers.put(char[].class, CharArrayDeserializer.instance);
        deserializers.put(byte[].class, ByteArrayDeserializer.instance);
        deserializers.put(short[].class, ShortArrayDeserializer.instance);
        deserializers.put(int[].class, IntArrayDeserializer.instance);
        deserializers.put(long[].class, LongArrayDeserializer.instance);
        deserializers.put(float[].class, FloatArrayDeserializer.instance);
        deserializers.put(double[].class, DoubleArrayDeserializer.instance);
        deserializers.put(String[].class, StringArrayDeserializer.instance);
        deserializers.put(BigInteger[].class, BigIntegerArrayDeserializer.instance);
        deserializers.put(Date[].class, DateArrayDeserializer.instance);
        deserializers.put(Time[].class, TimeArrayDeserializer.instance);
        deserializers.put(Timestamp[].class, TimestampArrayDeserializer.instance);
        deserializers.put(java.util.Date[].class, DateTimeArrayDeserializer.instance);
        deserializers.put(Calendar[].class, CalendarArrayDeserializer.instance);
        deserializers.put(BigDecimal[].class, BigDecimalArrayDeserializer.instance);
        deserializers.put(StringBuilder[].class, StringBuilderArrayDeserializer.instance);
        deserializers.put(StringBuffer[].class, StringBufferArrayDeserializer.instance);
        deserializers.put(UUID[].class, UUIDArrayDeserializer.instance);
        deserializers.put(char[][].class, CharsArrayDeserializer.instance);
        deserializers.put(byte[][].class, BytesArrayDeserializer.instance);
        deserializers.put(ArrayList.class, ArrayListDeserializer.instance);
        deserializers.put(AbstractList.class, ArrayListDeserializer.instance);
        deserializers.put(AbstractCollection.class, ArrayListDeserializer.instance);
        deserializers.put(List.class, ArrayListDeserializer.instance);
        deserializers.put(Collection.class, ArrayListDeserializer.instance);
        deserializers.put(LinkedList.class, LinkedListDeserializer.instance);
        deserializers.put(AbstractSequentialList.class, LinkedListDeserializer.instance);
        deserializers.put(HashSet.class, HashSetDeserializer.instance);
        deserializers.put(AbstractSet.class, HashSetDeserializer.instance);
        deserializers.put(Set.class, HashSetDeserializer.instance);
        deserializers.put(TreeSet.class, TreeSetDeserializer.instance);
        deserializers.put(SortedSet.class, TreeSetDeserializer.instance);
        deserializers.put(HashMap.class, HashMapDeserializer.instance);
        deserializers.put(AbstractMap.class, HashMapDeserializer.instance);
        deserializers.put(Map.class, HashMapDeserializer.instance);
        deserializers.put(TreeMap.class, TreeMapDeserializer.instance);
        deserializers.put(SortedMap.class, TreeMapDeserializer.instance);
        deserializers.put(AtomicBoolean.class, AtomicBooleanDeserializer.instance);
        deserializers.put(AtomicInteger.class, AtomicIntegerDeserializer.instance);
        deserializers.put(AtomicLong.class, AtomicLongDeserializer.instance);
        deserializers.put(AtomicReference.class, AtomicReferenceDeserializer.instance);
        deserializers.put(AtomicIntegerArray.class, AtomicIntegerArrayDeserializer.instance);
        deserializers.put(AtomicLongArray.class, AtomicLongArrayDeserializer.instance);
        deserializers.put(AtomicReferenceArray.class, AtomicReferenceArrayDeserializer.instance);
    }
    
    public static UrosDeserializer get(Class<?> type) {
        UrosDeserializer deserializer = deserializers.get(type);
        if (deserializer == null) {
            if (type.isEnum()) {
                deserializer = EnumDeserializer.instance;
            }
            else if (type.isArray()) {
                deserializer = OtherTypeArrayDeserializer.instance;
            }
            else if (Collection.class.isAssignableFrom(type)) {
                deserializer = CollectionDeserializer.instance;
            }
            else if (Map.class.isAssignableFrom(type)) {
                deserializer = MapDeserializer.instance;
            }
            else {
                deserializer = OtherTypeDeserializer.instance;
            }
            deserializers.putIfAbsent(type, deserializer);
        }
        return deserializer;
    }
}

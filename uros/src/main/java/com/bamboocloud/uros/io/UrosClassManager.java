package com.bamboocloud.uros.io;

import java.util.concurrent.ConcurrentHashMap;

public final class UrosClassManager {
    private static final ConcurrentHashMap<Class<?>, String> classCache1 = new ConcurrentHashMap<Class<?>, String>();
    private static final ConcurrentHashMap<String, Class<?>> classCache2 = new ConcurrentHashMap<String, Class<?>>();

    private UrosClassManager() {
    }

    public static void register(Class<?> type, String alias) {
        classCache1.put(type, alias);
        classCache2.put(alias, type);
    }

    public static String getClassAlias(Class<?> type) {
        return classCache1.get(type);
    }

    public static Class<?> getClass(String alias) {
        return classCache2.get(alias);
    }

    public static boolean containsClass(String alias) {
        return classCache2.containsKey(alias);
    }
}
package com.bamboocloud.uros.server;

import java.lang.reflect.Type;
import java.nio.channels.SocketChannel;


public class UrosTcpMethods extends UrosMethods {

    @Override
    protected int getCount(Type[] paramTypes) {
        int i = paramTypes.length;
        if ((i > 0) && (paramTypes[i - 1] instanceof Class<?>)) {
            Class<?> paramType = (Class<?>) paramTypes[i - 1];
            if (paramType.equals(SocketChannel.class)) {
                --i;
            }
        }
        return i;
    }
}

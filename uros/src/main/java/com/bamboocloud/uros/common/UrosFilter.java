package com.bamboocloud.uros.common;

import java.nio.ByteBuffer;

public interface UrosFilter {

	ByteBuffer inputFilter(ByteBuffer istream, Object context);

	ByteBuffer outputFilter(ByteBuffer ostream, Object context);

}
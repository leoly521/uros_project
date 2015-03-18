package com.bamboocloud.uros.common;

public interface UrosErrorEvent {

	void handler(String functionName, Throwable error);

}

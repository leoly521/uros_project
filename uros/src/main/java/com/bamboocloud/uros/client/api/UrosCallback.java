package com.bamboocloud.uros.client.api;

public interface UrosCallback<T> {

	void handler(T result, Object[] arguments);
}

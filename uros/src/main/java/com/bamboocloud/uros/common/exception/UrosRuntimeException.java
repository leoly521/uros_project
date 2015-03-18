package com.bamboocloud.uros.common.exception;

public class UrosRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -2575109816107184140L;

	public UrosRuntimeException() {
		super();
	}

	public UrosRuntimeException(String msg) {
		super(msg);
	}

	public UrosRuntimeException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public UrosRuntimeException(Throwable cause) {
		super(cause);
	}
}

package com.bamboocloud.uros.common.exception;

import java.io.IOException;

public class UrosProtocolException extends IOException {

	private static final long serialVersionUID = -5824361553914277130L;

	public UrosProtocolException() {
		super();
	}

	public UrosProtocolException(String msg) {
		super(msg);
	}

	public UrosProtocolException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public UrosProtocolException(Throwable cause) {
		super(cause);
	}
}

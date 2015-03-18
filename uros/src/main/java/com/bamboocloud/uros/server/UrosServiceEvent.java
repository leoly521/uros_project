package com.bamboocloud.uros.server;

public interface UrosServiceEvent {
	void onBeforeInvoke(String name, Object[] args, boolean byRef,
			Object context);

	void onAfterInvoke(String name, Object[] args, boolean byRef,
			Object result, Object context);

	void onSendError(Throwable e, Object context);
}

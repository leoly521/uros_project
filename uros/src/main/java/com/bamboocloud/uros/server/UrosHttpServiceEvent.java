package com.bamboocloud.uros.server;

public interface UrosHttpServiceEvent extends UrosServiceEvent {
	void onSendHeader(HttpContext httpContext);
}

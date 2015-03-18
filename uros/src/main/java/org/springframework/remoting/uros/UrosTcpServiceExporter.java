package org.springframework.remoting.uros;

import java.io.IOException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteExporter;

import com.bamboocloud.uros.common.UrosFilter;
import com.bamboocloud.uros.io.UrosMode;
import com.bamboocloud.uros.server.UrosServiceEvent;
import com.bamboocloud.uros.server.UrosTcpServer;

public class UrosTcpServiceExporter extends RemoteExporter implements
		InitializingBean {
	private UrosTcpServer tcpServer;
	private String host;
	private int port = 0;
	private boolean debug = true;
	private UrosServiceEvent event = null;
	private UrosMode mode = UrosMode.MemberMode;
	private UrosFilter filter = null;

	@Override
	public void afterPropertiesSet() throws Exception {
		checkService();
		checkServiceInterface();
		Object service = getService();
		Class<?> cls = getServiceInterface();
		this.tcpServer = new UrosTcpServer(this.host, this.port);
		this.tcpServer.add(service, cls);
		this.tcpServer.setDebug(debug);
		this.tcpServer.setEvent(event);
		this.tcpServer.setMode(mode);
		this.tcpServer.setFilter(filter);
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setEvent(UrosServiceEvent event) {
		this.event = event;
	}

	public void setMode(UrosMode mode) {
		this.mode = mode;
	}

	public void setFilter(UrosFilter filter) {
		this.filter = filter;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void start() throws IOException {
		this.tcpServer.start();
	}

	public void stop() {
		this.tcpServer.stop();
	}
}

package org.springframework.remoting.uros;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;

import com.bamboocloud.uros.client.UrosClient;
import com.bamboocloud.uros.client.UrosHttpClient;
import com.bamboocloud.uros.client.UrosTcpClient;
import com.bamboocloud.uros.common.UrosFilter;
import com.bamboocloud.uros.io.UrosMode;

@SuppressWarnings("rawtypes")
public class UrosProxyFactoryBean extends UrlBasedRemoteAccessor implements
		FactoryBean {

	private UrosClient client = null;
	private Exception exception = null;
	private int connectTimeout = UrosClient.DEFAULT_CONNECT_TIMEOUT;
	private int readTimeout = UrosClient.DEFAULT_READ_TIMEOUT;
	private int idleTimeout = UrosClient.DEFAULT_IDLE_TIMEOUT;
	private String proxyHost = null;
	private int proxyPort = 80;
	private String proxyUser = null;
	private String proxyPass = null;
	private UrosMode mode = UrosMode.MemberMode;
	private UrosFilter filter = null;

	@Override
	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		
		try {
			this.client = UrosClient.create(getServiceUrl(), this.mode);
		} catch (Exception ex) {
			this.exception = ex;
		}
		if (this.client instanceof UrosHttpClient) {
			UrosHttpClient httpClient = (UrosHttpClient) this.client;
			httpClient.setConnectTimeout(this.connectTimeout);
			httpClient.setReadTimeout(this.readTimeout);
			httpClient.setIdleTimeout(this.idleTimeout);
			httpClient.setProxyHost(this.proxyHost);
			httpClient.setProxyPort(this.proxyPort);
			httpClient.setProxyUser(this.proxyUser);
			httpClient.setProxyPass(this.proxyPass);
		}
		if (this.client instanceof UrosTcpClient) {
			UrosTcpClient tcpClient = (UrosTcpClient) this.client;
			tcpClient.setConnectTimeout(this.connectTimeout);
			tcpClient.setReadTimeout(this.readTimeout);
			tcpClient.setIdleTimeout(this.idleTimeout);
		}
		this.client.setFilter(this.filter);
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public void setProxyUser(String proxyUser) {
		this.proxyUser = proxyUser;
	}

	public void setProxyPass(String proxyPass) {
		this.proxyPass = proxyPass;
	}

	public void setMode(UrosMode mode) {
		this.mode = mode;
	}

	public void setFilter(UrosFilter filter) {
		this.filter = filter;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object getObject() throws Exception {
		if (this.exception != null) {
			throw this.exception;
		}
		return client.useService(this.getServiceInterface());
	}

	@Override
	public Class<?> getObjectType() {
		return this.getServiceInterface();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
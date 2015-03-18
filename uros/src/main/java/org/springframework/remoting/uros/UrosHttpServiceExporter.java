package org.springframework.remoting.uros;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.web.HttpRequestHandler;

import com.bamboocloud.uros.common.UrosFilter;
import com.bamboocloud.uros.io.UrosMode;
import com.bamboocloud.uros.server.UrosHttpService;
import com.bamboocloud.uros.server.UrosServiceEvent;
import com.bamboocloud.uros.server.HttpContext;

public class UrosHttpServiceExporter extends RemoteExporter implements
		InitializingBean, HttpRequestHandler {
	private UrosHttpService httpService;
	private boolean crossDomain = true;
	private boolean get = true;
	private boolean p3p = true;
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
		this.httpService = new UrosHttpService();
		this.httpService.add(service, cls);
		this.httpService.setCrossDomain(crossDomain);
		this.httpService.setGet(get);
		this.httpService.setP3p(p3p);
		this.httpService.setDebug(debug);
		this.httpService.setEvent(event);
		this.httpService.setMode(mode);
		this.httpService.setFilter(filter);
	}

	public void setCrossDomain(boolean crossDomain) {
		this.crossDomain = crossDomain;
	}

	public void setGet(boolean get) {
		this.get = get;
	}

	public void setP3p(boolean p3p) {
		this.p3p = p3p;
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

	@Override
	public void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.httpService.handle(new HttpContext(request, response, null, null));
	}

}

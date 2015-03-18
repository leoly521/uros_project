package com.bamboocloud.uros.server;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bamboocloud.uros.io.ByteBufferStream;

public class UrosHttpService extends UrosService {
	private boolean crossDomain = false;
	private boolean p3p = false;
	private boolean get = true;
	private final HashMap<String, Boolean> origins = new HashMap<String, Boolean>();
	private static final ThreadLocal<HttpContext> currentContext = new ThreadLocal<HttpContext>();

	public static HttpContext getCurrentContext() {
		return currentContext.get();
	}

	@Override
	public UrosMethods getGlobalMethods() {
		if (this.globalMethods == null) {
			this.globalMethods = new UrosHttpMethods();
		}
		return this.globalMethods;
	}

	@Override
	public void setGlobalMethods(UrosMethods methods) {
		if (methods instanceof UrosHttpMethods) {
			this.globalMethods = methods;
		} else {
			throw new ClassCastException("methods must be a UrosHttpMethods instance");
		}
	}

	public boolean isCrossDomain() {
		return this.crossDomain;
	}

	public void setCrossDomain(boolean crossDomain) {
		this.crossDomain = crossDomain;
	}

	public boolean isP3p() {
		return this.p3p;
	}

	public void setP3p(boolean p3p) {
		this.p3p = p3p;
	}

	public boolean isGet() {
		return this.get;
	}

	public void setGet(boolean get) {
		this.get = get;
	}

	public void addAccessControlAllowOrigin(String origin) {
		this.origins.put(origin, true);
	}

	public void removeAccessControlAllowOrigin(String origin) {
		this.origins.remove(origin);
	}

	@Override
	protected Object[] fixArguments(Type[] argumentTypes, Object[] arguments, int count, Object context) {
		HttpContext httpContext = (HttpContext) context;
		if (argumentTypes.length != count) {
			Object[] args = new Object[argumentTypes.length];
			System.arraycopy(arguments, 0, args, 0, count);
			Class<?> argType = (Class<?>) argumentTypes[count];
			if (argType.equals(HttpContext.class)) {
				args[count] = httpContext;
			} else if (argType.equals(HttpServletRequest.class)) {
				args[count] = httpContext.getRequest();
			} else if (argType.equals(HttpServletResponse.class)) {
				args[count] = httpContext.getResponse();
			} else if (argType.equals(HttpSession.class)) {
				args[count] = httpContext.getSession();
			} else if (argType.equals(ServletContext.class)) {
				args[count] = httpContext.getApplication();
			} else if (argType.equals(ServletConfig.class)) {
				args[count] = httpContext.getConfig();
			}
			return args;
		}
		return arguments;
	}

	protected void sendHeader(HttpContext httpContext) throws IOException {
		if (this.event != null && UrosHttpServiceEvent.class.isInstance(this.event)) {
			((UrosHttpServiceEvent) this.event).onSendHeader(httpContext);
		}
		HttpServletRequest request = httpContext.getRequest();
		HttpServletResponse response = httpContext.getResponse();
		response.setContentType(this.isDebug() ? "text/plain" : "application/uros");
		if (this.p3p) {
			response.setHeader("P3P", "CP=\"CAO DSP COR CUR ADM DEV TAI PSA PSD " + "IVAi IVDi CONi TELo OTPi OUR DELi SAMi "
					+ "OTRi UNRi PUBi IND PHY ONL UNI PUR FIN " + "COM NAV INT DEM CNT STA POL HEA PRE GOV\"");
		}
		if (this.crossDomain) {
			String origin = request.getHeader("Origin");
			if (origin != null && !origin.equals("null")) {
				if (this.origins.isEmpty() || this.origins.containsKey(origin)) {
					response.setHeader("Access-Control-Allow-Origin", origin);
					response.setHeader("Access-Control-Allow-Credentials", "true");
				}
			} else {
				response.setHeader("Access-Control-Allow-Origin", "*");
			}
		}
	}

	public void handle(HttpContext httpContext) throws IOException {
		handle(httpContext, null);
	}

	public void handle(HttpContext httpContext, UrosHttpMethods methods) throws IOException {
		ByteBufferStream ostream = null;
		try {
			currentContext.set(httpContext);
			sendHeader(httpContext);
			String method = httpContext.getRequest().getMethod();
			if (method.equals("GET")) {
				if (this.get) {
					ostream = doFunctionList(methods, httpContext);
					ostream.writeTo(httpContext.getResponse().getOutputStream());
				} else {
					httpContext.getResponse().sendError(HttpServletResponse.SC_FORBIDDEN);
				}
			} else if (method.equals("POST")) {
				ByteBufferStream istream = new ByteBufferStream();
				istream.readFrom(httpContext.getRequest().getInputStream());
				ostream = handle(istream, methods, httpContext);
				istream.close();
				ostream.writeTo(httpContext.getResponse().getOutputStream());
			}
		} finally {
			currentContext.remove();
			if (ostream != null) {
				ostream.close();
			}
		}
	}
}

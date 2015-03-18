package com.bamboocloud.uros.server;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bamboocloud.uros.Constants;
import com.bamboocloud.uros.common.UrosFilter;
import com.bamboocloud.uros.io.UrosClassManager;
import com.bamboocloud.uros.io.UrosHelper;
import com.bamboocloud.uros.io.UrosMode;

public class UrosServlet extends HttpServlet {

	private static final long serialVersionUID = 4464797507094499882L;

	private static final String servletInfo = Constants.name + Constants.version + " - Servlet";

	private final UrosHttpService service = new UrosHttpService();

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String param = config.getInitParameter("mode");
		if (param != null) {
			param = param.toLowerCase();
			if (param.equals("propertymode")) {
				this.service.setMode(UrosMode.PropertyMode);
			} else if (param.equals("fieldmode")) {
				this.service.setMode(UrosMode.FieldMode);
			} else if (param.equals("membermode")) {
				this.service.setMode(UrosMode.MemberMode);
			}
		}
		param = config.getInitParameter("debug");
		if (param != null) {
			param = param.toLowerCase();
			if (param.equals("true")) {
				this.service.setDebug(true);
			}
		}
		param = config.getInitParameter("crossDomain");
		if (param != null) {
			param = param.toLowerCase();
			if (param.equals("true")) {
				this.service.setCrossDomain(true);
			}
		}
		param = config.getInitParameter("p3p");
		if (param != null) {
			param = param.toLowerCase();
			if (param.equals("true")) {
				this.service.setP3p(true);
			}
		}
		param = config.getInitParameter("get");
		if (param != null) {
			param = param.toLowerCase();
			if (param.equals("false")) {
				this.service.setGet(false);
			}
		}
		param = config.getInitParameter("event");
		if (param != null) {
			try {
				Class<?> type = Class.forName(param);
				if (UrosServiceEvent.class.isAssignableFrom(type)) {
					this.service.setEvent((UrosServiceEvent) type.newInstance());
				}
			} catch (Exception ex) {
				throw new ServletException(ex);
			}
		}
		param = config.getInitParameter("filter");
		if (param != null) {
			try {
				Class<?> type = Class.forName(param);
				if (UrosFilter.class.isAssignableFrom(type)) {
					this.service.setFilter((UrosFilter) type.newInstance());
				}
			} catch (Exception ex) {
				throw new ServletException(ex);
			}
		}
		UrosMethods methods = this.service.getGlobalMethods();
		param = config.getInitParameter("class");
		if (param != null) {
			try {
				String[] classNames = UrosHelper.split(param, ',', 0);
				for (int i = 0, n = classNames.length; i < n; ++i) {
					String[] name = UrosHelper.split(classNames[i], '|', 3);
					Class<?> type = Class.forName(name[0]);
					Object obj = type.newInstance();
					Class<?> ancestorType;
					if (name.length == 1) {
						methods.addInstanceMethods(obj, type);
					} else if (name.length == 2) {
						for (ancestorType = Class.forName(name[1]); ancestorType.isAssignableFrom(type); type = type.getSuperclass()) {
							methods.addInstanceMethods(obj, type);
						}
					} else if (name.length == 3) {
						if (name[1].equals("")) {
							methods.addInstanceMethods(obj, type, name[2]);
						} else {
							for (ancestorType = Class.forName(name[1]); ancestorType.isAssignableFrom(type); type = type.getSuperclass()) {
								methods.addInstanceMethods(obj, type, name[2]);
							}
						}
					}
				}
			} catch (Exception ex) {
				throw new ServletException(ex);
			}
		}
		param = config.getInitParameter("staticClass");
		if (param != null) {
			try {
				String[] classNames = UrosHelper.split(param, ',', 0);
				for (int i = 0, n = classNames.length; i < n; ++i) {
					String[] name = UrosHelper.split(classNames[i], '|', 2);
					Class<?> type = Class.forName(name[0]);
					if (name.length == 1) {
						methods.addStaticMethods(type);
					} else {
						methods.addStaticMethods(type, name[1]);
					}
				}
			} catch (ClassNotFoundException ex) {
				throw new ServletException(ex);
			}
		}
		param = config.getInitParameter("type");
		if (param != null) {
			try {
				String[] classNames = UrosHelper.split(param, ',', 0);
				for (int i = 0, n = classNames.length; i < n; ++i) {
					String[] name = UrosHelper.split(classNames[i], '|', 2);
					UrosClassManager.register(Class.forName(name[0]), name[1]);
				}
			} catch (ClassNotFoundException ex) {
				throw new ServletException(ex);
			}
		}
		setGlobalMethods(methods);
	}

	protected void setGlobalMethods(UrosMethods methods) {
	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.service.handle(new HttpContext(request, response, this.getServletConfig(), this.getServletContext()));
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	public String getServletInfo() {
		return servletInfo;
	}
}

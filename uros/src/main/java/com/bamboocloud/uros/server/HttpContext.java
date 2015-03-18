package com.bamboocloud.uros.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HttpContext {
    private final ServletContext application;
    private final ServletConfig config;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public HttpContext(HttpServletRequest request,
                       HttpServletResponse response,
                       ServletConfig config,
                       ServletContext application) {
        this.request = request;
        this.response = response;
        this.config = config;
        this.application = application;
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public HttpServletResponse getResponse() {
        return this.response;
    }

    public HttpSession getSession() {
        return this.request.getSession();
    }

    public HttpSession getSession(boolean create) {
        return this.request.getSession(create);
    }

    public ServletConfig getConfig() {
        return this.config;
    }

    public ServletContext getApplication() {
        return this.application;
    }
}
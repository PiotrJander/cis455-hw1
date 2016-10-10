package edu.upenn.cis.cis455.webserver.servlet;

import javax.servlet.*;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ServletConfig implements javax.servlet.ServletConfig {

    private String servletName;
    private HashMap<String, String> initParameters = new HashMap<>();
    private ServletContext servletContext;

    // TODO constructor

    @Override
    public String getServletName() {
        return servletName;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getInitParameter(String s) {
        return initParameters.get(s);
    }

    @Override
    public Enumeration getInitParameterNames() {
        return new InitParametersEnumeration(initParameters);
    }
}

class InitParametersEnumeration implements Enumeration {

    private Iterator<String> iterator;

    InitParametersEnumeration(Map<String, String> map) {
        iterator = map.keySet().iterator();
    }

    @Override
    public boolean hasMoreElements() {
        return iterator.hasNext();
    }

    @Override
    public String nextElement() {
        return iterator.next();
    }
}
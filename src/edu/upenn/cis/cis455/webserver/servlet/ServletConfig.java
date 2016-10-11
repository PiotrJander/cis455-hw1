package edu.upenn.cis.cis455.webserver.servlet;

import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ServletConfig implements javax.servlet.ServletConfig {

    private String servletName;
    private HashMap<String, String> initParameters = new HashMap<>();
    private ServletContext servletContext;

    public ServletConfig(String servletName, HashMap<String, String> initParameters, ServletContext servletContext) {
        this.servletName = servletName;
        this.initParameters = initParameters;
        this.servletContext = servletContext;
    }

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
        return new MapKeysEnumeration(initParameters);
    }
}

class MapKeysEnumeration implements Enumeration {

    private Iterator<String> iterator;

    MapKeysEnumeration(Map<String, ?> map) {
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
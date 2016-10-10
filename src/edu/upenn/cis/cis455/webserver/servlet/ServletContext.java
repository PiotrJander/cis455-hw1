package edu.upenn.cis.cis455.webserver.servlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;

public class ServletContext implements javax.servlet.ServletContext {

    private String displayName;
    private HashMap<String, String> initParameters = new HashMap<>();

    public ServletContext(String displayName, HashMap<String, String> initParameters) {
        this.displayName = displayName;
        this.initParameters = initParameters;
    }

    @Override
    public javax.servlet.ServletContext getContext(String s) {
        return null;
    }

    @Override
    public String getRealPath(String s) {
        return "";
    }

    @Override
    public String getServerInfo() {
        return "Piotr's Server/0.0";
    }

    @Override
    public String getInitParameter(String s) {
        return initParameters.get(s);
    }

    @Override
    public Enumeration getInitParameterNames() {
        return new InitParametersEnumeration(initParameters);
    }

    // start attrs

    @Override
    public Object getAttribute(String s) {
        return null;
    }

    @Override
    public Enumeration getAttributeNames() {
        return null;
    }

    @Override
    public void setAttribute(String s, Object o) {  }

    @Override
    public void removeAttribute(String s) {  }

    // end attrs

    @Override
    public String getServletContextName() {
        return displayName;
    }

    @Override
    public int getMajorVersion() {
        return 2;
    }

    @Override
    public int getMinorVersion() {
        return 4;
    }

    /**
     * @NotRequired
     */
    @Override
    public String getMimeType(String s) {
        return null;
    }

    /**
     * @NotRequired
     */
    @Override
    public Set getResourcePaths(String s) {
        return null;
    }

    /**
     * @NotRequired
     */
    @Override
    public URL getResource(String s) throws MalformedURLException {
        return null;
    }

    /**
     * @NotRequired
     */
    @Override
    public InputStream getResourceAsStream(String s) {
        return null;
    }

    /**
     * @NotRequired
     */
    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

    /**
     * @NotRequired
     */
    @Override
    public RequestDispatcher getNamedDispatcher(String s) {
        return null;
    }

    /**
     * @deprecated
     */
    @Override
    public Servlet getServlet(String s) throws ServletException {
        return null;
    }

    /**
     * @deprecated
     */
    @Override
    public Enumeration getServlets() {
        return null;
    }

    /**
     * @deprecated
     */
    @Override
    public Enumeration getServletNames() {
        return null;
    }

    /**
     * @NotRequired
     */
    @Override
    public void log(String s) {

    }

    /**
     * @deprecated
     */
    @Override
    public void log(Exception e, String s) {

    }

    /**
     * @NotRequired
     */
    @Override
    public void log(String s, Throwable throwable) {

    }
}

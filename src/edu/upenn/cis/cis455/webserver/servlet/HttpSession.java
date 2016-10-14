package edu.upenn.cis.cis455.webserver.servlet;

import javax.servlet.*;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

public class HttpSession implements javax.servlet.http.HttpSession {
    @Override
    public long getCreationTime() {
        return 0;
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public long getLastAccessedTime() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public void setMaxInactiveInterval(int i) {

    }

    @Override
    public int getMaxInactiveInterval() {
        return 0;
    }

    @Override
    public Object getAttribute(String s) {
        return null;
    }

    @Override
    public Enumeration getAttributeNames() {
        return null;
    }

    @Override
    public void setAttribute(String s, Object o) {

    }

    @Override
    public void removeAttribute(String s) {

    }

    @Override
    public void invalidate() {

    }

    @Override
    public boolean isNew() {
        return false;
    }

    /**
     * @deprecated
     */
    @SuppressWarnings("deprecation")
    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    /**
     * @deprecated
     */
    @Override
    public Object getValue(String s) {
        return null;
    }

    /**
     * @deprecated
     */
    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    /**
     * @deprecated
     */
    @Override
    public void removeValue(String s) {

    }

    /**
     * @deprecated
     */
    @Override
    public void putValue(String s, Object o) {

    }
}

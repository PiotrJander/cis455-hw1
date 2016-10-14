package edu.upenn.cis.cis455.webserver.servlet;

import edu.upenn.cis.cis455.webserver.HttpRequest;
import edu.upenn.cis.cis455.webserver.HttpServer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.*;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class HttpServletRequest implements javax.servlet.http.HttpServletRequest {

    private HashMap<String, Object> attributes = new HashMap<>();
    private String encoding = "ISO-8859-1";
    private Socket socket;
    private HttpRequest baseRequest;

    public HttpServletRequest(HttpRequest baseRequest) {
        this.baseRequest = baseRequest;
    }

    public HttpServletRequest(Socket socket, HttpRequest baseRequest) {
        this.socket = socket;
        this.baseRequest = baseRequest;
    }

    /**
     * Returns: a String containing the name or path of the servlet being called, as specified in the request URL,
     * decoded, or an empty string if the servlet used to process the request is matched using the "/*" pattern.
     */
    @Override
    public String getServletPath() {
        return null;
    }

    /**
     * TODO should always return the remainder of the URL request after the portion matched by the url-pattern in web-xml. It starts with a “/”.
     */
    @Override
    public String getPathInfo() {
        return null;
    }

    // START readers and streams

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    // END readers and streams

    // START params

    @Override
    public String getParameter(String s) {
        return null;
    }

    @Override
    public Enumeration getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String s) {
        return new String[0];
    }

    @Override
    public Map getParameterMap() {
        return null;
    }

    // END params

    // START need socket for those

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalAddr() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getLocalPort() {
        return HttpServer.getPortNumber();
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    // END need socket for those

    // START session

    @Override
    public HttpSession getSession(boolean b) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public String getRequestedSessionId() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return new Cookie[0];
    }

    // END session

    // DONE

    /**
     * Should always return false.
     */
    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    /**
     * Should return “http”.
     */
    @Override
    public String getScheme() {
        return "http";
    }

    @Override
    public String getServerName() {
        return "Piotr's Server";
    }

    @Override
    public String getLocalName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Should return “ISO-8859-1” by default, and the results of setCharacterEncoding if it was previously called.
     */
    @Override
    public String getCharacterEncoding() {
        return encoding;
    }

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
        encoding = s;
    }

    /**
     * Returns null since I don't implement multiple apps.
     */
    @Override
    public String getContextPath() {
        return null;
    }

    /**
     * Should always return BASIC AUTH (“BASIC”)
     */
    @Override
    public String getAuthType() {
        return "BASIC";
    }

    /**
     * Returns null since we don't implement HTTP authentication.
     */
    @Override
    public String getRemoteUser() {
        return null;
    }

    // START attrs

    @Override
    public Object getAttribute(String s) {
        return attributes.get(s);
    }

    @Override
    public void setAttribute(String s, Object o) {
        attributes.put(s, o);
    }

    @Override
    public void removeAttribute(String s) {
        attributes.remove(s);
    }

    @Override
    public Enumeration getAttributeNames() {
        return new MapKeysEnumeration(attributes);
    }

    // END attrs

    // START headers

    @Override
    public long getDateHeader(String s) {
        String dateString = getHeader(s);
        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
        ZonedDateTime date = ZonedDateTime.parse(dateString, formatter);
        return date.toInstant().toEpochMilli();
    }

    @Override
    public String getHeader(String s) {
        return baseRequest.getHeaderValue(s);
    }

    @Override
    public Enumeration getHeaders(String s) {
        return Collections.enumeration(baseRequest.getHeaders().get(s));
    }

    @Override
    public Enumeration getHeaderNames() {
        return new MapKeysEnumeration(baseRequest.getHeaders());
    }

    @Override
    public int getIntHeader(String s) throws NumberFormatException {
        return Integer.parseInt(getHeader(s));
    }

    // END headers

    // START HTTP

    /**
     * Return e.g. HTTP/1.1
     */
    @Override
    public String getProtocol() {
        return baseRequest.getVersion().getName();
    }

    @Override
    public String getMethod() {
        return baseRequest.getMethod().toString();
    }

    @Override
    public int getContentLength() {
        try {
            return getIntHeader("Content-Length");
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public String getContentType() {
        return getHeader("Content-Type");
    }

    // END HTTP

    // START url parts

    /**
     * Should return the HTTP GET query string, i.e., the portion after the “?” when a GET form is posted.
     */
    @Override
    public String getQueryString() {
        return baseRequest.getUrl().getQuery();
    }

    /**
     * Returns the part of this request's URL from the protocol name up to the query string in the first line of the HTTP request.
     */
    @Override
    public String getRequestURI() {
        try {
            return baseRequest.getUrl().toURI().toString();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(baseRequest.getUrl().toString());
    }

    // END url parts

    /**
     * @NotRequired
     */
    @Override
    public Locale getLocale() {
        return null;
    }

    /**
     * @NotRequired
     */
    @Override
    public Enumeration getLocales() {
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
    public String getPathTranslated() {
        return null;
    }

    /**
     * @NotRequired
     */
    @Override
    public boolean isUserInRole(String s) {
        return false;
    }

    /**
     * @NotRequired
     */
    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    /**
     * @deprecated
     */
    @Override
    public String getRealPath(String s) {
        return null;
    }

    /**
     * @deprecated
     */
    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }
}

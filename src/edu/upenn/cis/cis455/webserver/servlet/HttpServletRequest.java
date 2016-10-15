package edu.upenn.cis.cis455.webserver.servlet;

import edu.upenn.cis.cis455.webserver.HttpMethod;
import edu.upenn.cis.cis455.webserver.HttpRequest;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.*;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;


public class HttpServletRequest implements javax.servlet.http.HttpServletRequest {

    private HashMap<String, Object> attributes = new HashMap<>();
    private String encoding = "ISO-8859-1";
    private Socket socket;
    private HttpRequest baseRequest;
    private Map<String, List<String>> parameters;
    @SuppressWarnings("FieldCanBeLocal")
    private boolean isPostParametersRead = false;
    private String servletPath;
    private String pathInfo;

    HttpServletRequest(HttpRequest baseRequest) {
        this.baseRequest = baseRequest;
        parameters = splitQuery(baseRequest.getUrl().getQuery());
    }

    public HttpServletRequest(Socket socket, HttpRequest baseRequest, Match match) {
        this.socket = socket;
        this.baseRequest = baseRequest;
        parameters = splitQuery(baseRequest.getUrl().getQuery());
        servletPath = match.getServletPath();
        pathInfo = match.getPathInfo();
    }

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

    // ****************************************************************************************************************
    // DONE
    // ****************************************************************************************************************

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
        return Collections.enumeration(attributes.keySet());
    }

    // END attrs

    // START headers

    @Override
    public long getDateHeader(String s) {
        String dateString = getHeader(s);
        ZonedDateTime date = ZonedDateTime.parse(dateString, DateTimeFormatter.RFC_1123_DATE_TIME);
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
        return Collections.enumeration(baseRequest.getHeaders().keySet());
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
        return baseRequest.getUrl().getPath();
    }

    @Override
    public StringBuffer getRequestURL() {
        return new StringBuffer(baseRequest.getUrl().toString());
    }

    // END url parts

    // START borrowed from http://stackoverflow.com/questions/13592236/parse-a-uri-string-into-name-value-collection

    private Map<String, List<String>> splitQuery(String query) {
        if (query == null || query.isEmpty()) {
            return Collections.emptyMap();
        }
        return Arrays.stream(query.split("&"))
                .map(this::splitQueryParameter)
                .collect(Collectors.groupingBy(AbstractMap.SimpleImmutableEntry::getKey, LinkedHashMap::new, mapping(Map.Entry::getValue, toList())));
    }

    private AbstractMap.SimpleImmutableEntry<String, String> splitQueryParameter(String it) {
        final int idx = it.indexOf("=");
        final String key = idx > 0 ? it.substring(0, idx) : it;
        final String value = idx > 0 && it.length() > idx + 1 ? it.substring(idx + 1) : null;
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

    // END borrowed

    // START params

    /**
     * POST form data is only parsed on demand, as it inferferes with `getReader`.
     * <p>
     * We assume that form data is sent as application/x-www-form-urlencoded.
     * <p>
     * We also assume that all data is sent on a single line.
     * <p>
     * POST parameters are merged with possible GET (query) parameters and will override them in the case of conflict.
     */
    private void makePostParameters() {
        if (!isPostParametersRead && baseRequest.getMethod() == HttpMethod.POST) {
            try {
                BufferedReader reader = getReader();
                String line = reader.readLine();
                parameters.putAll(splitQuery(line));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getParameter(String s) {
        makePostParameters();
        return parameters.get(s).get(0);
    }

    @Override
    public Enumeration getParameterNames() {
        makePostParameters();
        return Collections.enumeration(parameters.keySet());
    }

    @Override
    public String[] getParameterValues(String s) {
        makePostParameters();
        List<String> ret = parameters.get(s);
        return ret.toArray(new String[ret.size()]);
    }

    @Override
    public Map getParameterMap() {
        makePostParameters();
        return parameters;
    }

    // END params

    // START meta

    @Override
    public String getRemoteAddr() {
        return socket.getRemoteSocketAddress().toString();
    }

    @Override
    public String getRemoteHost() {
        return getRemoteAddr();
    }

    @Override
    public int getRemotePort() {
        return socket.getPort();
    }

    @Override
    public String getLocalAddr() {
        return socket.getLocalAddress().toString();
    }

    @Override
    public int getLocalPort() {
        return socket.getLocalPort();
    }

    @Override
    public int getServerPort() {
        return getLocalPort();
    }

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
        return socket.getLocalAddress().toString();
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

    // END meta

    // START readers and streams

    @Override
    public BufferedReader getReader() throws IOException {
        return baseRequest.getIn();
    }

    // END readers and streams

    // START pattern matching in paths

    /**
     * Returns: a String containing the name or path of the servlet being called, as specified in the request URL,
     * decoded, or an empty string if the servlet used to process the request is matched using the "/*" pattern.
     */
    @Override
    public String getServletPath() {
        return servletPath;
    }

    /**
     * Should always return the remainder of the URL request after the portion matched by the url-pattern in web-xml. It starts with a “/”.
     */
    @Override
    public String getPathInfo() {
        return pathInfo;
    }

    // END pattern matching in paths

    /**
     * @NotRequired
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

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

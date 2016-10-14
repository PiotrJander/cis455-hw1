package edu.upenn.cis.cis455.webserver.servlet;

import edu.upenn.cis.cis455.webserver.HttpResponse;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HttpServletResponse implements javax.servlet.http.HttpServletResponse {

    private HttpResponse baseResponse;

    public HttpServletResponse(HttpResponse baseResponse) {
        this.baseResponse = baseResponse;
    }

    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public boolean containsHeader(String s) {
        return baseResponse.containsHeader(s);
    }

    @Override
    public void sendError(int i, String s) throws IOException {

    }

    @Override
    public void sendError(int i) throws IOException {

    }

    @Override
    public void sendRedirect(String s) throws IOException {

    }

    @Override
    public void setStatus(int i) {

    }

    /**
     * TODO should return “ISO-8859-1”.
     */
    @Override
    public String getCharacterEncoding() {
        return null;
    }

    /**
     * TODO should return “text/html” by default, and the results of setContentType if it was previously called.
     */
    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return null;
    }

    @Override
    public void setCharacterEncoding(String s) {

    }

    @Override
    public void setContentLength(int i) {

    }

    @Override
    public void setContentType(String s) {

    }

    @Override
    public void setBufferSize(int i) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale locale) {

    }

    /**
     * TODO should return null by default, or the results of setLocale if it was previously called.
     */
    @Override
    public Locale getLocale() {
        return null;
    }

    // START headers

    @Override
    public void setDateHeader(String s, long l) {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneOffset.UTC);
        setHeader(s, date.format(DateTimeFormatter.RFC_1123_DATE_TIME));
    }

    @Override
    public void addDateHeader(String s, long l) {
        ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneOffset.UTC);
        addHeader(s, date.format(DateTimeFormatter.RFC_1123_DATE_TIME));
    }

    @Override
    public void setHeader(String s, String s1) {
        baseResponse.setHeader(s, s1);
    }

    @Override
    public void addHeader(String s, String s1) {
        baseResponse.addHeader(s, s1);
    }

    @Override
    public void setIntHeader(String s, int i) {
        setHeader(s, Integer.toString(i));
    }

    @Override
    public void addIntHeader(String s, int i) {
        addHeader(s, Integer.toString(i));
    }

    // END headers

    /**
     * @NotRequired
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return null;
    }

    /**
     * @NotRequired
     */
    @Override
    public String encodeURL(String s) {
        return null;
    }

    /**
     * @NotRequired
     */
    @Override
    public String encodeRedirectURL(String s) {
        return null;
    }

    /**
     * @deprecated
     */
    @Override
    public void setStatus(int i, String s) {

    }

    /**
     * @deprecated
     */
    @Override
    public String encodeUrl(String s) {
        return null;
    }

    /**
     * @deprecated
     */
    @Override
    public String encodeRedirectUrl(String s) {
        return null;
    }
}

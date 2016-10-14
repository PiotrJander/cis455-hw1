package edu.upenn.cis.cis455.webserver.servlet;

import edu.upenn.cis.cis455.webserver.HttpResponse;
import edu.upenn.cis.cis455.webserver.HttpStatus;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HttpServletResponse implements javax.servlet.http.HttpServletResponse {

    private HttpResponse baseResponse;
    private String characterEncoding = "ISO-8859-1";
    private String contentType = "text/html";
    private Locale locale;

    public HttpServletResponse(HttpResponse baseResponse) {
        this.baseResponse = baseResponse;
    }

    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return null;
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

    // ****************************************************************************************************************
    // DONE
    // ****************************************************************************************************************

    /**
     * Should return “text/html” by default, and the results of setContentType if it was previously called.
     */
    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public void setCharacterEncoding(String s) {
        characterEncoding = s;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * Should return null by default, or the results of setLocale if it was previously called.
     */
    @Override
    public Locale getLocale() {
        return locale;
    }

    // START headers

    @Override
    public boolean containsHeader(String s) {
        return baseResponse.containsHeader(s);
    }

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

    @Override
    public void setContentLength(int i) {
        setIntHeader("Content-Length", i);
    }

    /**
     * TODO no effect if writing started
     */
    @Override
    public void setContentType(String s) {
        contentType = s;
        setHeader("Content-Type", s);
    }

    // END headers

    // START set status and send

    @Override
    public void setStatus(int i) {
        baseResponse.setStatus(HttpStatus.getByCode(i));
    }

    @Override
    public void sendError(int i, String s) throws IOException, IllegalStateException {
        baseResponse.error(HttpStatus.getByCode(i), s);
    }

    @Override
    public void sendError(int i) throws IOException, IllegalStateException {
        baseResponse.error(HttpStatus.getByCode(i));
    }

    @Override
    public void sendRedirect(String s) throws IOException, IllegalStateException {
        setHeader("Location", (new URL(baseResponse.getRequest().getUrl(), s)).toString());
    }

    // END set status and send

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

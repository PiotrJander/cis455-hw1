package edu.upenn.cis.cis455.webserver.servlet;

import edu.upenn.cis.cis455.webserver.HttpResponse;
import edu.upenn.cis.cis455.webserver.HttpStatus;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HttpServletResponse implements javax.servlet.http.HttpServletResponse {

    private HttpResponse baseResponse;
    private OutputStream out;
    private String characterEncoding = "ISO-8859-1";
    private String contentType = "text/html";
    private Locale locale;
    private int bufferSize = 0;
    private StringWriter stringWriter;
    private boolean isCommited = false;

    public HttpServletResponse(HttpResponse baseResponse, OutputStream out) {
        this.baseResponse = baseResponse;
        this.out = out;
    }

    @Override
    public void addCookie(Cookie cookie) {

    }

    // ****************************************************************************************************************
    // DONE
    // ****************************************************************************************************************

    // START committing

    private void commit() {
        isCommited = true;
    }

    @Override
    public boolean isCommitted() {
        return isCommited;
    }

    // END committing

    // START writing

    @Override
    public int getBufferSize() {
        return bufferSize;
    }

    @Override
    public void setBufferSize(int i) throws IllegalStateException {
        if (isCommitted())  throw new IllegalStateException();
        bufferSize = i;
    }

    @Override
    public PrintWriter getWriter() {
        if (stringWriter == null) {
            stringWriter = bufferSize == 0 ? new StringWriter() : new StringWriter(bufferSize);
        }
        return new ResponsePrintWriter(stringWriter, this);
    }

    /**
     * Listens for flushing the PrintWriter.
     */
    void notifyFlush() {
        baseResponse.setPayload(stringWriter.toString());
        commit();
    }

    @Override
    public void flushBuffer() {
        baseResponse.setPayload(stringWriter.toString());
        commit();
    }

    @Override
    public void resetBuffer() throws IllegalStateException {
        if (isCommitted())  throw new IllegalStateException();
        stringWriter = new StringWriter();
    }

    @Override
    public void reset() throws IllegalStateException {
        if (isCommitted())  throw new IllegalStateException();
        baseResponse.setStatus(HttpStatus.OK);
        baseResponse.resetHeaders();
        resetBuffer();
    }

    // END writing

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

    @Override
    public void setContentType(String s) {
        contentType = s;
        setHeader("Content-Type", s);
    }

    /**
     * Should return “text/html” by default, and the results of setContentType if it was previously called.
     */
    @Override
    public String getContentType() {
        return contentType;
    }

    // END headers

    // START set status and send

    @Override
    public void setStatus(int i) {
        baseResponse.setStatus(HttpStatus.getByCode(i));
    }

    @Override
    public void sendError(int i, String s) throws IOException, IllegalStateException {
        if (isCommitted())  throw new IllegalStateException();
        baseResponse.error(HttpStatus.getByCode(i), s);
        commit();
    }

    @Override
    public void sendError(int i) throws IOException, IllegalStateException {
        if (isCommitted())  throw new IllegalStateException();
        baseResponse.error(HttpStatus.getByCode(i));
        commit();
    }

    @Override
    public void sendRedirect(String s) throws IOException, IllegalStateException {
        if (isCommitted())  throw new IllegalStateException();
        setHeader("Location", (new URL(baseResponse.getRequest().getUrl(), s)).toString());
        commit();
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

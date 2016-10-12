package edu.upenn.cis.cis455.webserver.servlet;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

public class HttpServletResponse implements javax.servlet.http.HttpServletResponse {
    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public boolean containsHeader(String s) {
        return false;
    }

    @Override
    public String encodeURL(String s) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String s) {
        return null;
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
    public void setDateHeader(String s, long l) {

    }

    @Override
    public void addDateHeader(String s, long l) {

    }

    @Override
    public void setHeader(String s, String s1) {

    }

    @Override
    public void addHeader(String s, String s1) {

    }

    @Override
    public void setIntHeader(String s, int i) {

    }

    @Override
    public void addIntHeader(String s, int i) {

    }

    @Override
    public void setStatus(int i) {

    }

    /**
     * @deprecated
     */
    @Override
    public void setStatus(int i, String s) {

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

    /**
     * @NotRequired
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
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
}

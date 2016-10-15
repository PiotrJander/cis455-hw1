package edu.upenn.cis.cis455.webserver.servlet;

import edu.upenn.cis.cis455.webserver.HttpResponse;
import junit.framework.TestCase;

import java.io.PrintWriter;

public class HttpServletResponseWritingTest extends TestCase {

    public void testWriteToResponse() throws Exception {
        HttpServletResponse response = new HttpServletResponse(new HttpResponse());
        PrintWriter writer = response.getWriter();
        writer.println("foo");
        writer.println("bar");
        response.flushBuffer();
        assertEquals("foo\nbar\n", response.getStringWriter().toString());
    }

    public void testWriteToResponseFlushWriter() throws Exception {
        HttpServletResponse response = new HttpServletResponse(new HttpResponse());
        PrintWriter writer = response.getWriter();
        writer.println("foo");
        writer.println("bar");
        writer.flush();
        assertEquals("foo\nbar\n", response.getStringWriter().toString());
    }

    public void testResetBuffer() throws Exception {
        HttpServletResponse response = new HttpServletResponse(new HttpResponse());
        PrintWriter writer = response.getWriter();
        writer.println("foo");
        writer.println("bar");
        response.resetBuffer();
        assertEquals("", response.getStringWriter().toString());
    }
}
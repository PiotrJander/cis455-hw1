package edu.upenn.cis.cis455.webserver;

import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.StringReader;

public class HttpRequestTest extends TestCase {
    public void testParseHeaders() throws Exception {
        String headers =
                "Host: www.example.com\n"
                + "User-Agent: Firefox/1.1.1\n"
                + "If-Modified-Since: 28 Sept 2016\n";
        BufferedReader in = new BufferedReader(new StringReader(headers));
        HttpRequest req = new HttpRequest(in);
        req.parseHeaders();
        assertEquals("www.example.com", req.getHeaderValue("Host"));
        assertEquals("Firefox/1.1.1", req.getHeaderValue("User-Agent"));
        assertEquals("28 Sept 2016", req.getHeaderValue("If-Modified-Since"));
        assertEquals(req.getHeaderValue("foo"), null);
    }

    public void testNoHost() throws Exception {
        String raw =
                "GET /foo HTTP/1.1\n"
                + "\n";
        BufferedReader in = new BufferedReader(new StringReader(raw));
        HttpRequest req = new HttpRequest(in);
        req.parse();
        assertFalse(req.isOk());
        assertEquals("'Host' header not specified", req.getBadRequestErrorMessage());
    }

    public void testHostMismatch() throws Exception {
        String raw =
                "GET http://www.bar.com/foo HTTP/1.1\n"
                        + "Host: www.example.com\n"
                        + "\n";
        BufferedReader in = new BufferedReader(new StringReader(raw));
        HttpRequest req = new HttpRequest(in);
        req.parse();
        assertFalse(req.isOk());
        assertEquals("'Host' header and the host in the URL don't agree", req.getBadRequestErrorMessage());
    }
}
package edu.upenn.cis.cis455.webserver;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.Assert.*;

public class HttpRequestTest {
    @Test
    public void parseHeaders() throws Exception {
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

}
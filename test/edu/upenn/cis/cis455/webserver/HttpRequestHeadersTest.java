package edu.upenn.cis.cis455.webserver;

import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;

public class HttpRequestHeadersTest extends TestCase {

    public void testMultivalueHeaders() throws Exception {
        StringReader stringReader = new StringReader("Header: 1\nHeader: 2,3\n");
        HttpRequest request = new HttpRequest(new BufferedReader(stringReader));
        request.parseHeaders();
        assertEquals(Arrays.asList("1", "2", "3"), request.getHeaders().get("Header"));
    }
}
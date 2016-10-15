package edu.upenn.cis.cis455.webserver.servlet;

import edu.upenn.cis.cis455.webserver.HttpMethod;
import edu.upenn.cis.cis455.webserver.HttpRequest;
import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.URL;

public class HttpServletRequestPostParametersTest extends TestCase {

    public void testParsePostParameters() throws Exception {
        HttpRequest baseRequest = new HttpRequest();
        baseRequest.setMethod(HttpMethod.POST);
        baseRequest.setUrl(new URL("http://foo.com/bar/baz?a=b"));
        HttpServletRequest request = new HttpServletRequest(baseRequest);
        StringReader stringReader = new StringReader("foo=bar&baz=xyz\n");
        request.makePostParametersHelper(new BufferedReader(stringReader));
        assertEquals("bar", request.getParameter("foo"));
        assertEquals("xyz", request.getParameter("baz"));
        assertEquals("b", request.getParameter("a"));
    }
}
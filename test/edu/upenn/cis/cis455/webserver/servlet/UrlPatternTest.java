package edu.upenn.cis.cis455.webserver.servlet;

import junit.framework.TestCase;

public class UrlPatternTest extends TestCase {

    public void testUrlPatternLiteral() throws Exception {
        UrlPattern pattern = UrlPattern.make("/foo");
        Match m1 = pattern.match("/foo");
        assertEquals(new Match("/foo"), m1);
        Match m2 = pattern.match("/foo/bar");
        assertNull(m2);
    }

    public void testUrlPatternWildcard() throws Exception {
        UrlPattern pattern = UrlPattern.make("/foo/*");
        Match m1 = pattern.match("/foo/bar");
        assertEquals(new Match("/foo", "/bar"), m1);
        Match m2 = pattern.match("/foo/");
        assertEquals(new Match("/foo", "/"), m2);
        Match m3 = pattern.match("/foo");
        assertEquals(new Match("/foo"), m3);
        Match m4 = pattern.match("/baz/xyz");
        assertNull(m4);
    }
}

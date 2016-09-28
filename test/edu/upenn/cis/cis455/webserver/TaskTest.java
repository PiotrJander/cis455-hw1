package edu.upenn.cis.cis455.webserver;

import junit.framework.TestCase;

import static org.junit.Assert.*;

public class TaskTest extends TestCase {

    public void testMime() throws Exception {
        assertEquals("text/html", Task.getMimeType("foo.html"));
    }

}
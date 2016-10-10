package edu.upenn.cis.cis455.webserver;

import junit.framework.TestCase;

import java.nio.file.Path;

public class TaskTest extends TestCase {

    public void testMime() throws Exception {
        assertEquals("text/html", Task.getMimeType("foo.html"));
    }

}
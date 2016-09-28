package edu.upenn.cis.cis455.webserver;

import org.junit.Test;

import static org.junit.Assert.*;

public class TaskTest {

    @Test
    public void mime() throws Exception {
        assertEquals("text/html", Task.getMimeType("foo.html"));
    }

}
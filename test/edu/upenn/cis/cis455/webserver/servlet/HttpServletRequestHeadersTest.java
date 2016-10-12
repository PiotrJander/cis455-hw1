package edu.upenn.cis.cis455.webserver.servlet;

import edu.upenn.cis.cis455.webserver.HttpRequest;
import junit.framework.TestCase;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HttpServletRequestHeadersTest extends TestCase {

    private HttpServletRequest request;
    private ZonedDateTime date = ZonedDateTime.now(ZoneOffset.UTC);

    public void setUp() throws Exception {
        super.setUp();

        HashMap<String, List<String>> headers = new HashMap<>();
        headers.put("foo", new LinkedList<>(Collections.singletonList("bar")));
        headers.put("baz", new LinkedList<>(Arrays.asList("a", "b")));
        headers.put("date", new LinkedList<>(Collections.singleton(date.format(DateTimeFormatter.RFC_1123_DATE_TIME))));
        headers.put("number", new LinkedList<>(Collections.singleton("1")));

        HttpRequest baseRequest = new HttpRequest();
        baseRequest.setHttpRequestHeaders(headers);

        request = new HttpServletRequest(baseRequest);
    }

    public void testGetDateHeader() throws Exception {
        assertEquals((double) date.toInstant().toEpochMilli(), (double) request.getDateHeader("date"), 1000);
    }

    public void testGetHeader() throws Exception {
        assertEquals("bar", request.getHeader("foo"));
        assertEquals("a", request.getHeader("baz"));
    }

    public void testGetHeaders() throws Exception {
        String value = (String) request.getHeaders("baz").nextElement();
        assertTrue(Objects.equals(value, "a") || Objects.equals(value, "b"));
    }

    public void testGetHeaderNames() throws Exception {
        String key = (String) request.getHeaderNames().nextElement();
        assertTrue(Objects.equals(key, "foo") || Objects.equals(key, "baz")
                || Objects.equals(key, "date") || Objects.equals(key, "number"));
    }

    public void testGetIntHeader() throws Exception {
        assertEquals(1, request.getIntHeader("number"));
    }

}

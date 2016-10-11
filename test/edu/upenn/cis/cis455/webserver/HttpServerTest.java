package edu.upenn.cis.cis455.webserver;

import edu.upenn.cis.cis455.webserver.servlet.ServletContext;
import junit.framework.TestCase;

import java.io.File;
import java.util.Enumeration;

public class HttpServerTest extends TestCase {

    ServletContext servletContext;

    public void setUp() throws Exception {
        super.setUp();
        HttpServer.setWebDotXmlSource(new File("conf/web.xml"));
        HttpServer.parseWebDotXml();
        HttpServer.makeServletContext();
        servletContext = HttpServer.getServletContext();
        HttpServer.loadServlets();
    }

    public void testContextGetAttribute() throws Exception {
        assertEquals(servletContext.getInitParameter("piotr"), "jander");
    }

    public void testContextGetInitParameterNames() throws Exception {
        Enumeration initParameterNames = servletContext.getInitParameterNames();
        String param = (String) initParameterNames.nextElement();
        assertTrue(param.equals("piotr") || param.equals("magda"));
        initParameterNames.nextElement();
        assertFalse(initParameterNames.hasMoreElements());
    }

    public void testAttributes() throws Exception {
        servletContext.setAttribute("foo", 1);
        assertEquals(servletContext.getAttribute("foo"), 1);
        servletContext.removeAttribute("foo");
        assertNull(servletContext.getAttribute("foo"));
    }
}
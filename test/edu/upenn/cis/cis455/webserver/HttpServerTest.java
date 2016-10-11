package edu.upenn.cis.cis455.webserver;

import edu.upenn.cis.cis455.webserver.servlet.ServletContext;
import junit.framework.TestCase;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.util.Enumeration;

public class HttpServerTest extends TestCase {

    private ServletContext servletContext;
    private HttpServlet calculatorServlet;

    public void setUp() throws Exception {
        super.setUp();
        HttpServer.setWebDotXmlSource(new File("conf/web.xml"));
        HttpServer.parseWebDotXml();
        HttpServer.makeServletContext();
        servletContext = HttpServer.getServletContext();
        HttpServer.loadServlets();
        calculatorServlet = HttpServer.getServletByName("CalculatorServlet");
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

    public void testServletGenericMethods() throws Exception {
        assertEquals(calculatorServlet.getInitParameter("baz"), "xyz");
        assertEquals(calculatorServlet.getServletContext().getMajorVersion(), 2);
        assertEquals(calculatorServlet.getServletName(), "CalculatorServlet");
    }

    public void testServletConfig() throws Exception {
        ServletConfig servletConfig = calculatorServlet.getServletConfig();
        assertEquals(servletConfig.getServletName(), "CalculatorServlet");
        assertEquals(servletConfig.getServletContext().getMajorVersion(), 2);
        assertEquals(servletConfig.getInitParameter("baz"), "xyz");
    }
}
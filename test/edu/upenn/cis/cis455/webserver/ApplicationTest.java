package edu.upenn.cis.cis455.webserver;

import edu.upenn.cis.cis455.webserver.servlet.ServletContext;
import junit.framework.TestCase;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.util.Enumeration;

public class ApplicationTest extends TestCase {

    private Application application;

    public void setUp() throws Exception {
        super.setUp();
        application = new Application(new File("conf/web.xml"));
        application.create();
    }

    public void testContextGetAttribute() throws Exception {
        assertEquals(application.getServletContext().getInitParameter("piotr"), "jander");
    }

    public void testContextGetInitParameterNames() throws Exception {
        Enumeration initParameterNames = application.getServletContext().getInitParameterNames();
        String param = (String) initParameterNames.nextElement();
        assertTrue(param.equals("piotr") || param.equals("magda"));
        initParameterNames.nextElement();
        assertFalse(initParameterNames.hasMoreElements());
    }

    public void testAttributes() throws Exception {
        ServletContext servletContext = application.getServletContext();
        servletContext.setAttribute("foo", 1);
        assertEquals(servletContext.getAttribute("foo"), 1);
        servletContext.removeAttribute("foo");
        assertNull(servletContext.getAttribute("foo"));
    }

    public void testServletGenericMethods() throws Exception {
        HttpServlet calculatorServlet = application.getServletByName("CalculatorServlet");
        assertEquals(calculatorServlet.getInitParameter("baz"), "xyz");
        assertEquals(calculatorServlet.getServletContext().getMajorVersion(), 2);
        assertEquals(calculatorServlet.getServletName(), "CalculatorServlet");
    }

    public void testServletConfig() throws Exception {
        ServletConfig servletConfig = application.getServletByName("CalculatorServlet").getServletConfig();
        assertEquals(servletConfig.getServletName(), "CalculatorServlet");
        assertEquals(servletConfig.getServletContext().getMajorVersion(), 2);
        assertEquals(servletConfig.getInitParameter("baz"), "xyz");
    }

    public void testUrlMapping() throws Exception {
        assertEquals(application.getServletByUrl("calculator/").getServletName(), "CalculatorServlet");
    }
}
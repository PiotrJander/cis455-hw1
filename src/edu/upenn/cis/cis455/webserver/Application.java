package edu.upenn.cis.cis455.webserver;

import edu.upenn.cis.cis455.webserver.servlet.ServletConfig;
import edu.upenn.cis.cis455.webserver.servlet.ServletContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Application {

    private File webDotXmlSource;
    private Document webDotXml;
    private ServletContext servletContext;
    private HashMap<String, HttpServlet> servletsNameToClassMapping = new HashMap<>();

    Application(File webDotXmlSource) {
        this.webDotXmlSource = webDotXmlSource;
    }

    void create() {
        parseWebDotXml();
        makeServletContext();
        loadServlets();
    }

    /**
     * Parses web.xml, and saves the DOM as `webDotXml`.
     */
    private void parseWebDotXml() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            webDotXml = db.parse(webDotXmlSource);
        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            System.out.println("Invalid XML in web.xml.");
            HttpServer.handleInvalidArguments();
        }
    }

    /**
     * Creates a ServletContext from data in web.xml.
     */
    private void makeServletContext() {
        String displayName = webDotXml.getElementsByTagName("display-name").item(0).getTextContent();

        NodeList contextParamsXml = webDotXml.getElementsByTagName("context-param");
        HashMap<String, String> contextInitParams = new HashMap<>();
        for (int i = 0; i < contextParamsXml.getLength(); i++) {
            Element param = (Element) contextParamsXml.item(i);
            String name = param.getElementsByTagName("param-name").item(0).getTextContent();
            String value = param.getElementsByTagName("param-value").item(0).getTextContent();
            contextInitParams.put(name, value);
        }
        servletContext = new ServletContext(displayName, contextInitParams);
    }

    /**
     * For each <servlet> element:
     * 1. Gets (servlet name, class name) pair from web.xml
     * 2. Loads the class and creates an instance of it; adds entry to `servletsNameToClassMapping`.
     * 4. Creates a ServletConfig from <init-param> elements.
     * 5. Calls the servlet's `init` method.
     */
    private void loadServlets() {
        NodeList servlets = webDotXml.getElementsByTagName("servlet");
        for (int i = 0; i < servlets.getLength(); i++) {
            Element servletElement = (Element) servlets.item(i);
            String servletName = servletElement.getElementsByTagName("servlet-name").item(0).getTextContent();
            String className = servletElement.getElementsByTagName("servlet-class").item(0).getTextContent();

            HttpServlet servlet;
            try {
                servlet = (HttpServlet) Class.forName(className).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
                continue;
            }

            servletsNameToClassMapping.put(servletName, servlet);

            NodeList initParamElementSet = servletElement.getElementsByTagName("init-param");
            HashMap<String, String> initParameters = new HashMap<>();
            for (int j = 0; j < initParamElementSet.getLength(); j++) {
                Element initParamElement = (Element) initParamElementSet.item(j);
                String paramName = initParamElement.getElementsByTagName("param-name").item(0).getTextContent();
                String paramValue = initParamElement.getElementsByTagName("param-value").item(0).getTextContent();
                initParameters.put(paramName, paramValue);
            }

            try {
                servlet.init(new ServletConfig(servletName, initParameters, servletContext));
            } catch (ServletException e) {
                e.printStackTrace();
            }
        }
    }

    ServletContext getServletContext() {
        return servletContext;
    }

    HttpServlet getServletByName(String s) {
        return servletsNameToClassMapping.get(s);
    }
}

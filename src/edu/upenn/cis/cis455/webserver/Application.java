package edu.upenn.cis.cis455.webserver;

import edu.upenn.cis.cis455.webserver.servlet.ServletConfig;
import edu.upenn.cis.cis455.webserver.servlet.ServletContext;
import org.w3c.dom.DOMException;
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

class Application {

    private File webDotXmlSource;
    private Document webDotXml;
    private ServletContext servletContext;
    private HashMap<String, HttpServlet> servletsNameToClassMapping = new HashMap<>();
    private HashMap<String, String> urlMapping = new HashMap<>();

    Application(File webDotXmlSource) {
        this.webDotXmlSource = webDotXmlSource;
    }

    void create() {
        try {
            parseWebDotXml();
            makeServletContext();
            loadServlets();
            loadUrlMapping();
        } catch (WebDotXmlException e) {
            System.out.println("Invalid web.xml");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Parses web.xml, and saves the DOM as `webDotXml`.
     */
    private void parseWebDotXml() throws WebDotXmlException {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            webDotXml = db.parse(webDotXmlSource);
        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            throw new WebDotXmlException(e);
        }
    }

    /**
     * Creates a ServletContext from data in web.xml.
     */
    private void makeServletContext() throws WebDotXmlException {
        try {
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
        } catch (ClassCastException | NullPointerException e) {
            throw new WebDotXmlException(e);
        }
    }

    /**
     * For each <servlet> element:
     * 1. Gets (servlet name, class name) pair from web.xml
     * 2. Loads the class and creates an instance of it; adds entry to `servletsNameToClassMapping`.
     * 4. Creates a ServletConfig from <init-param> elements.
     * 5. Calls the servlet's `init` method.
     */
    private void loadServlets() throws WebDotXmlException {
        NodeList servlets = webDotXml.getElementsByTagName("servlet");
        for (int i = 0; i < servlets.getLength(); i++) {
            Element servletElement;
            String servletName;
            String className;
            try {
                servletElement = (Element) servlets.item(i);
                servletName = servletElement.getElementsByTagName("servlet-name").item(0).getTextContent();
                className = servletElement.getElementsByTagName("servlet-class").item(0).getTextContent();
            } catch (ClassCastException | NullPointerException e) {
                throw new WebDotXmlException(e);
            }

            HttpServlet servlet;
            try {
                servlet = (HttpServlet) Class.forName(className).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new WebDotXmlException("Invalid class for the servlet.", e);
            }

            servletsNameToClassMapping.put(servletName, servlet);

            HashMap<String, String> initParameters;
            try {
                NodeList initParamElementSet = servletElement.getElementsByTagName("init-param");
                initParameters = new HashMap<>();
                for (int j = 0; j < initParamElementSet.getLength(); j++) {
                    Element initParamElement = (Element) initParamElementSet.item(j);
                    String paramName = initParamElement.getElementsByTagName("param-name").item(0).getTextContent();
                    String paramValue = initParamElement.getElementsByTagName("param-value").item(0).getTextContent();
                    initParameters.put(paramName, paramValue);
                }
            } catch (ClassCastException | NullPointerException e) {
                throw new WebDotXmlException(e);
            }

            try {
                servlet.init(new ServletConfig(servletName, initParameters, servletContext));
            } catch (ServletException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * TODO url as regex rather than as string?
     */
    private void loadUrlMapping() throws WebDotXmlException {
        NodeList servletMappingSet = webDotXml.getElementsByTagName("servlet-mapping");
        for (int i = 0; i < servletMappingSet.getLength(); i++) {
            String servletName;
            String urlPattern;
            try {
                Element servletMapping = (Element) servletMappingSet.item(i);
                servletName = servletMapping.getElementsByTagName("servlet-name").item(0).getTextContent();
                urlPattern = servletMapping.getElementsByTagName("url-pattern").item(0).getTextContent();
            } catch (ClassCastException | NullPointerException e) {
                throw new WebDotXmlException(e);
            }

            if (servletsNameToClassMapping.containsKey(servletName)) {
                urlMapping.put(servletName, urlPattern);
            } else {
                throw new WebDotXmlException("No servlet for the servlet name in mapping.");
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

class WebDotXmlException extends Exception {
    WebDotXmlException() { super(); }
    WebDotXmlException(String message) { super(message); }
    WebDotXmlException(String message, Throwable cause) { super(message, cause); }
    WebDotXmlException(Throwable cause) { super(cause); }
}

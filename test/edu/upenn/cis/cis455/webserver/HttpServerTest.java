package edu.upenn.cis.cis455.webserver;

import junit.framework.TestCase;

import java.io.File;

public class HttpServerTest extends TestCase {

    /**
     * 1. set the file
     * 2. parse the file
     * 3. assert things on context, config, servlet mapping, etc
     *
     * but need to have servlets first
     */
    public void testWebXml() {
        HttpServer.setWebDotXmlSource(new File("conf/web.xml"));
        HttpServer.parseWebDotXml();
        HttpServer.makeServletContext();
//        HttpServer.loadServlets();
    }

}
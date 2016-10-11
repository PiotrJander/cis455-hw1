package edu.upenn.cis.cis455.webserver;

import edu.upenn.cis.cis455.webserver.servlet.ServletConfig;
import edu.upenn.cis.cis455.webserver.servlet.ServletContext;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
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
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class HttpServer {
    private static Logger log = Logger.getLogger(HttpServer.class);

    // arguments
    private static int portNumber;
    private static Path rootDirectory;

    private static File webDotXmlSource;

    // queue
    private static final int QUEUE_SIZE = 1000;
    private static BlockingQueue<TcpRequest> queue = new BlockingQueue<>(QUEUE_SIZE);

    // thread pool
    private final static int WORKERS_POOL_SIZE = 50;
    private static Worker[] workersPool = new Worker[WORKERS_POOL_SIZE];

    private static ServerSocket serverSocket;
    private static Document webDotXml;
    private static ServletContext servletContext;
    private static HashMap<String, HttpServlet> servletsNameToClassMapping = new HashMap<>();

	public static void main(String args[]) throws InterruptedException {
        BasicConfigurator.configure();
		log.info("Start of Http Server");

        parseArguments(args);

        parseWebDotXml();

        makeServletContext();
        loadServlets();

        // TODO make mapping

        startWorkersPool();
        runDaemon();

        log.info("Http Server terminating");
	}

    /**
     * Creates a ServletContext from data in web.xml.
     */
    static void makeServletContext() {
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
    public static void loadServlets() {
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

    /**
     * Parses web.xml, and saves the DOM as `webDotXml`.
     */
    public static void parseWebDotXml() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            webDotXml = db.parse(webDotXmlSource);
        } catch (ParserConfigurationException | IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            System.out.println("Invalid XML in web.xml.");
            handleInvalidArguments();
        }
    }

    private static void parseArguments(String[] args) {
        try {
            portNumber = Integer.parseInt(args[0]);
            rootDirectory = Paths.get(args[1]);
            webDotXmlSource = new File(args[2]);

            if (!webDotXmlSource.exists()) {
                System.out.println("The path to web.xml is invalid.");
                handleInvalidArguments();
            }
        } catch (NumberFormatException | InvalidPathException | ArrayIndexOutOfBoundsException | NullPointerException e) {
            handleInvalidArguments();
        }
    }

    private static void handleInvalidArguments() {
        System.out.println("Piotr Jander");
        System.out.println("piotr@sas.upenn.edu");
        System.exit(1);
    }

    private static void startWorkersPool() {
        log.info("Starting workers pool");
        for (int i = 0; i < WORKERS_POOL_SIZE; i++) {
            workersPool[i] = new Worker(queue, i);
            workersPool[i].start();
        }
    }

    private static void runDaemon() {
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            HttpServer.serverSocket = serverSocket;
            log.info("Listening on port " + portNumber);
            TcpRequest tcpRequest;
            //noinspection InfiniteLoopStatement
            while (true) {
                tcpRequest = new TcpRequest(serverSocket.accept());
                queue.put(tcpRequest);
                log.info("New request added to the queue");
            }
        } catch (SocketException e) {
            stopWorkers();
            // TODO destroy servletsNameToClassMapping
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // port number out of range
            handleInvalidArguments();
        }
    }

    static void stop() {
        log.info("Stopping the server");

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void stopWorkers() {
        for (Worker worker : workersPool) {
            worker.interrupt();
        }
        for (Worker worker : workersPool) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                log.warn("Thread " + worker.getWorkerId() + " threw InterruptedException");
            }
        }
        log.info("All workers stopped");
    }

    static Path getRootDirectory() {
        return rootDirectory;
    }

    static Worker[] getWorkersPool() {
        return workersPool;
    }

    static void setWebDotXmlSource(File webDotXmlSource) {
        HttpServer.webDotXmlSource = webDotXmlSource;
    }

    static ServletContext getServletContext() {
        return servletContext;
    }

    static HttpServlet getServletByName(String s) {
        return servletsNameToClassMapping.get(s);
    }

}

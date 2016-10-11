package edu.upenn.cis.cis455.webserver;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    private static Application application;

	public static void main(String args[]) throws InterruptedException {
        BasicConfigurator.configure();
		log.info("Start of Http Server");

        parseArguments(args);

        application = new Application(webDotXmlSource);
        application.create();

        startWorkersPool();
        runDaemon();

        log.info("Http Server terminating");
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

    static void handleInvalidArguments() {
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

}

package edu.upenn.cis.cis455.webserver;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpServer {
    private static Logger log = Logger.getLogger(HttpServer.class);

    private static int portNumber;
    private static Path rootDirectory;

    private static final int QUEUE_SIZE = 10;
    private static BlockingQueue<TcpRequest> queue = new BlockingQueue<>(QUEUE_SIZE);

    private final static int WORKERS_POOL_SIZE = 10;
    private static Thread[] workersPool = new Thread[WORKERS_POOL_SIZE];

    private static volatile boolean running = true;

	public static void main(String args[]) throws InterruptedException
	{
        BasicConfigurator.configure();
		log.info("Start of Http Server");

        parseArguments(args);
        startWorkersPool();
        startDaemon();

        log.info("Http Server terminating");
	}

    private static void parseArguments(String[] args) {
        try {
            portNumber = Integer.parseInt(args[0]);
            rootDirectory = Paths.get(args[1]);
        } catch (NumberFormatException | InvalidPathException | ArrayIndexOutOfBoundsException e) {
            handleInvalidArguments();
        }
    }

    private static void handleInvalidArguments() {
        System.out.println("Piotr Jander");
        System.out.println("piotr@sas.upenn.edu");
        System.exit(0);
    }

    private static void startWorkersPool() {
        log.info("Starting workers pool");
        for (int i = 0; i < WORKERS_POOL_SIZE; i++) {
            workersPool[i] = new Thread(new Worker(queue, i));
            workersPool[i].start();
        }
    }

    private static void startDaemon() throws InterruptedException {
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            log.info("Listening on port " + portNumber);
            while (running) {
                TcpRequest tcpRequest = new TcpRequest(serverSocket.accept());
                queue.put(tcpRequest);
                log.info("New request added to the queue");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // port number out of range
            handleInvalidArguments();
        }
    }

    static void stop() {
        log.info("Stopping the server");
        running = false;
        stopWorkers();
    }

    private static void stopWorkers() {
        for (Thread worker : workersPool) {
            worker.interrupt();
        }
        for (Thread worker : workersPool) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("All workers stopped");
    }

    static Path getRootDirectory() {
        return rootDirectory;
    }

}

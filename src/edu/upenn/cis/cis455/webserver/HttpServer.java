package edu.upenn.cis.cis455.webserver;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpServer {
    private static Logger log = Logger.getLogger(HttpServer.class);

    private static int portNumber;
    private static Path rootDirectory;

    private static final int QUEUE_SIZE = 10;
    private static BlockingQueue<TcpRequest> queue = new BlockingQueue<>(QUEUE_SIZE);

    private static Thread daemon;

    private final static int WORKERS_POOL_SIZE = 10;
    private static Worker[] workersPool = new Worker[WORKERS_POOL_SIZE];

	public static void main(String args[]) throws InterruptedException
	{
        BasicConfigurator.configure();
		log.info("Start of Http Server");

        parseArguments(args);

        startDaemon();
        startWorkersPool();
	}

    private static void parseArguments(String[] args) {
        try {
            portNumber = Integer.parseInt(args[0]);
            rootDirectory = Paths.get(args[1]);
        } catch (NumberFormatException | InvalidPathException | ArrayIndexOutOfBoundsException e) {
            handleInvalidArguments();
        }
    }

    static void handleInvalidArguments() {
        System.out.println("Piotr Jander");
        System.out.println("piotr@sas.upenn.edu");
        System.exit(0);
    }

    private static void startDaemon() {
        daemon = new Daemon(portNumber, queue);
        daemon.start();
    }

    private static void startWorkersPool() {
        log.info("Starting workers pool");
        for (int i = 0; i < WORKERS_POOL_SIZE; i++) {
            workersPool[i] = new Worker(queue, i);
            workersPool[i].start();
        }
    }

    static void stop() {
        log.info("Stopping the server");

        daemon.interrupt();
        interruptWorkers();

        try {
            daemon.join();
            workersJoin();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            log.info("Http Server terminating");
        }
    }

    private static void interruptWorkers() {
        for (Thread worker : workersPool) {
            worker.interrupt();
        }
    }

    private static void workersJoin() throws InterruptedException {
        for (Thread worker : workersPool) {
            worker.join();
        }
        log.info("All workers stopped");
    }

    static Path getRootDirectory() {
        return rootDirectory;
    }

    static Worker[] getWorkersPool() {
        return workersPool;
    }

}

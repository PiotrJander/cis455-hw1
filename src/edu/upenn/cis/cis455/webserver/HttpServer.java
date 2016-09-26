package edu.upenn.cis.cis455.webserver;

import org.apache.log4j.Logger;

public class HttpServer {
    private static Logger log = Logger.getLogger(HttpServer.class);

    private static final int QUEUE_SIZE = 10;
    private static BlockingQueue<Request> queue = new BlockingQueue<>(QUEUE_SIZE);

    private static Thread daemon;

    private final static int WORKERS_POOL_SIZE = 10;
    private static Thread[] workersPool = new Thread[WORKERS_POOL_SIZE];

	public static void main(String args[]) throws InterruptedException
	{
		log.info("Start of Http Server");
		
		// TODO handle args here

        startDaemon();
        startWorkersPool();

        // call these when shutdown
//        stopDaemon();
//        stopWorkers();

        log.info("Http Server terminating");
	}

//    private static void stopWorkers() throws InterruptedException {
//        for (int i = 0; i < WORKERS_POOL_SIZE; i++) {
//            workersPool[i].interrupt();
//        }
//        for (int i = 0; i < WORKERS_POOL_SIZE; i++) {
//            workersPool[i].join();
//        }
//    }
//
//    private static void stopDaemon() throws InterruptedException {
//        daemon.interrupt();
//        daemon.join();
//    }

    private static void startWorkersPool() {
        for (int i = 0; i < WORKERS_POOL_SIZE; i++) {
            workersPool[i] = new Thread(new Worker(queue));
            workersPool[i].start();
        }
    }

    private static void startDaemon() {
        Daemon.INSTANCE.setQueue(queue);
        daemon = new Thread(Daemon.INSTANCE);
        daemon.start();
    }

}

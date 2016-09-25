package edu.upenn.cis.cis455.webserver;

import org.apache.log4j.Logger;

public class HttpServer {

	/**
     * Logger for this particular class TODO private?
	 */
    private static Logger log = Logger.getLogger(HttpServer.class);

	public static void main(String args[]) throws InterruptedException
	{
		log.info("Start of Http Server");
		
		// TODO handle args here

        BlockingQueue<Request> queue = new BlockingQueue<>(10);

        Daemon.INSTANCE.setQueue(queue);
        (new Thread(Daemon.INSTANCE)).start();
        (new Thread(new Worker(queue))).start();
		
		log.info("Http Server terminating");
	}

}

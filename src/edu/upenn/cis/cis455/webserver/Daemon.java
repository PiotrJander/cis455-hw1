package edu.upenn.cis.cis455.webserver;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

public class Daemon extends Thread {
    private static Logger log = Logger.getLogger(Daemon.class);

    private int portNumber;
    private BlockingQueue<TcpRequest> queue;

    public Daemon(int portNumber, BlockingQueue<TcpRequest> queue) {
        this.portNumber = portNumber;
        this.queue = queue;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            log.info("Listening on port " + portNumber);
            //noinspection InfiniteLoopStatement
            while (true) {
                TcpRequest tcpRequest = new TcpRequest(serverSocket.accept());
                queue.put(tcpRequest);
                log.info("New request added to the queue");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // port number out of range
            HttpServer.handleInvalidArguments();
        } catch (InterruptedException e) {
            log.warn("Daemon interrupted");
        }
    }
}

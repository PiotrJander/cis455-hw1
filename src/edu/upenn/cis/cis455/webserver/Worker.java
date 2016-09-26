package edu.upenn.cis.cis455.webserver;

import org.apache.log4j.Logger;

import java.io.IOException;

class Worker extends Thread {
    private static Logger log = Logger.getLogger(Worker.class);

    private BlockingQueue<TcpRequest> queue;
    private int id;

    private String currentRequestPath;

    Worker(BlockingQueue<TcpRequest> queue, int id) {
        this.queue = queue;
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Task task = new Task(queue.take(), this);
                log.info("Task was taken from the queue");
                task.run();
                currentRequestPath = null;
            } catch (InterruptedException e) {
                // the worker was interrupted; stop
                log.info("Stopping worker " + id);
                return;
            } catch (IOException e) {
                log.error("A task threw IOException");
            }
        }
    }

    int getWorkerId() {
        return id;
    }

    void setCurrentRequestPath(String currentRequestPath) {
        this.currentRequestPath = currentRequestPath;
    }

    public String getCurrentRequestPath() {
        return currentRequestPath;
    }
}

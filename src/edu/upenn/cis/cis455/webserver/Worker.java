package edu.upenn.cis.cis455.webserver;

import org.apache.log4j.Logger;

import java.io.IOException;

class Worker extends Thread {
    private static Logger log = Logger.getLogger(Worker.class);

    private BlockingQueue<TcpRequest> queue;
    private int workerId;

    private String currentTask;

    Worker(BlockingQueue<TcpRequest> queue, int workerId) {
        this.queue = queue;
        this.workerId = workerId;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Task task = new Task(queue.take(), this);
                log.info("Task was taken from the queue");
                task.run();
                currentTask = null;
            } catch (InterruptedException e) {
                // the worker was interrupted; stop
                log.info("Stopping worker " + workerId);
                return;
            } catch (IOException e) {
                log.error("A task threw IOException");
            }
        }
    }

    int getWorkerId() {
        return workerId;
    }

    /**
     * This is included so that the worker's job can be displayed.
     */
    void setCurrentTask(String currentTask) {
        this.currentTask = currentTask;
    }

    String getCurrentTask() {
        return currentTask;
    }
}

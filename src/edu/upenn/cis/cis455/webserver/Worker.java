package edu.upenn.cis.cis455.webserver;

class Worker implements Runnable {
    private BlockingQueue<TcpRequest> queue;
    private int id;

    Worker(BlockingQueue<TcpRequest> queue, int id) {
        this.queue = queue;
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            try {
                (new Task(queue.take())).run();
            } catch (InterruptedException e) {
                // the worker was interrupted; stop
                return;
            }
        }
    }
}

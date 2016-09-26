package edu.upenn.cis.cis455.webserver;

class Worker implements Runnable {
    private BlockingQueue<TcpRequest> queue;

    Worker(BlockingQueue<TcpRequest> queue) {
        this.queue = queue;
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

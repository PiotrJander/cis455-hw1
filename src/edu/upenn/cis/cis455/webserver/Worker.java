package edu.upenn.cis.cis455.webserver;

class Worker implements Runnable {
    private BlockingQueue<Request> queue;

    Worker(BlockingQueue<Request> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            try {
                System.out.println(queue.take().i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

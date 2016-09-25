package edu.upenn.cis.cis455.webserver;

enum Daemon implements Runnable {
    INSTANCE;

    private BlockingQueue<Request> queue;

    public void setQueue(BlockingQueue<Request> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            try {
                queue.put(new Request(i));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
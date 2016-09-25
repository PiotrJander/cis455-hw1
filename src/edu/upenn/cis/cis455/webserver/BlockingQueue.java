package edu.upenn.cis.cis455.webserver;

import java.util.LinkedList;

class BlockingQueue<E> {
    private LinkedList<E> queue = new LinkedList<>();
    private final int MAX_SIZE;

    BlockingQueue(int max_size) {
        this.MAX_SIZE = max_size;
    }

    synchronized void put(E elem) throws InterruptedException {
        while (queue.size() == MAX_SIZE) {
            wait();
        }
        if (queue.size() == 0) {
            notifyAll();
        }

        queue.add(elem);
    }

    synchronized E take() throws InterruptedException {
        while (queue.size() == 0) {
            wait();
        }
        if (queue.size() == MAX_SIZE) {
            notifyAll();
        }

        return queue.remove();
    }
}

package edu.upenn.cis.cis455.webserver;

import java.util.LinkedList;

class BlockingQueue<E> {
    private LinkedList<E> queue = new LinkedList<>();
    private int maxSize;

    BlockingQueue(int max_size) {
        this.maxSize = max_size;
    }

    synchronized void put(E elem) throws InterruptedException {
        while (queue.size() == maxSize) {
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
        if (queue.size() == maxSize) {
            notifyAll();
        }

        return queue.remove();
    }
}

package edu.upenn.cis.cis455.webserver;

import java.util.LinkedList;

class BlockingQueue<E> {
    private LinkedList<E> queue = new LinkedList<>();
    private int maxSize;

    BlockingQueue(int max_size) {
        this.maxSize = max_size;
    }

    synchronized void put(E elem) {
        if (queue.size() == maxSize) {
            // discard overflow request
            return;
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

        return queue.remove();
    }
}

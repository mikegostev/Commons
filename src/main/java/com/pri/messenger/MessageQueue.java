/*
 * Created on 25.04.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.messenger;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class MessageQueue {

    public enum State {READY, SESS_EXPIRED, CLOSE_REQ}

    private static final int MAX_QUEUE_SIZE = 10;

    private LinkedList<Message> queue;

    State state;
    Lock lock;
    Condition cond;
// private boolean mustDie = false;

    public MessageQueue() {
        queue = new LinkedList<Message>();
        lock = new ReentrantLock(true);
        cond = lock.newCondition();
        state = State.READY;
    }

    public void setState(State st) {
        lock.lock();

        state = st;
        if (st != State.READY) {
            cond.signalAll();
        }
        lock.unlock();
    }

    public State getState() {
        lock.lock();
        try {
            return state;
        } finally {
            lock.unlock();
        }
    }

    public void destroy() {
        setState(State.SESS_EXPIRED);
    }

    public void finalize() throws Throwable {
        destroy();
        super.finalize();
    }

    public boolean enqueueMessage(Message m) throws NetworkException {
        lock.lock();

        try {
            if (state != State.READY) {
                throw new NetworkException("Client disconnected");
            }

            if (queue.size() >= MAX_QUEUE_SIZE) {
                throw new NetworkException("Output queue full");
            }

            queue.addLast(m);
            cond.signal();
            return true;
        } finally {
            lock.unlock();
        }

    }

    public QueueElement getMessage(long wt) {
        lock.lock();

        try {

            if (queue.size() == 0 && state == State.READY) {
                long startWaitTime = System.currentTimeMillis();
                do {
                    try {
                        cond.await(wt, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                    }
                } while (queue.size() == 0 && System.currentTimeMillis() - startWaitTime < wt && state == State.READY);
            }

            QueueElement ql = new QueueElement();

            ql.setState(state);

            if (!queue.isEmpty()) {
                ql.setMessage(queue.removeFirst());
            } else {
                ql.setMessage(null);
            }

            ql.setQueueLength(queue.size());

            return ql;
        } finally {
            lock.unlock();
        }

    }


    public QueueElement getMessage() {
        lock.lock();

        try {

            while (queue.size() == 0 && state == State.READY) {
                try {
                    cond.await();
                } catch (InterruptedException e) {
                }
            }

            QueueElement ql = new QueueElement();

            ql.setState(state);
            ql.setMessage(queue.removeFirst());
            ql.setQueueLength(queue.size());

            return ql;
        } finally {
            lock.unlock();
        }
    }

    public boolean hasMessages() {
        lock.lock();
        try {
            return !queue.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    public void messagePushback(Message msg) {
        lock.lock();

        try {
            queue.addFirst(msg);
            cond.signal();
        } finally {
            lock.unlock();
        }

    }

    public int getLength() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    public static class QueueElement {

        private Message message;
        private State state;
        int queueLenth;

        public Message getMessage() {
            return message;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public int getQueueLenth() {
            return queueLenth;
        }

        public void setQueueLength(int queueLenth) {
            this.queueLenth = queueLenth;
        }

        public State getState() {
            return state;
        }

        public void setState(State state) {
            this.state = state;
        }
    }

}


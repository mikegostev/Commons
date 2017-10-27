package com.pri.messenger.server;

public class DataWaiter {

    private static final long MAX_WAIT_TIME_MSEC = 60 * 1000;

    private boolean busy;
    private Object data;

    private String dest, meth;

    public DataWaiter() {
    }

    public synchronized void waitData() {
        busy = true;
        data = null;
        try {
            wait(MAX_WAIT_TIME_MSEC);
        } catch (InterruptedException e) {
        }

        busy = false;
    }

    public synchronized boolean dataReady(String d, String m, Object o) {
        data = o;
        dest = d;
        meth = m;

        if (busy) {
            notify();
        } else {
            return false;
        }

        return true;
    }

    public synchronized void releaseWaiter() {
        if (busy) {
            notify();
        }
    }

    public synchronized boolean isBusy() {
        return busy;
    }

    public synchronized Object getData() {
        busy = true;
        data = null;
        try {
            wait(MAX_WAIT_TIME_MSEC);
        } catch (InterruptedException e) {
        }

        busy = false;

        Object o = data;
        data = null;
        return o;
    }

    public String getDestination() {
        return dest;
    }

    public String getMethod() {
        return meth;
    }
}

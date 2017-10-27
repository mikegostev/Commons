package com.pri.util;

/*
 * Created on 07.03.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Semaphore {

    private volatile int readLocks;
    private volatile boolean writeLocked;

    private Object rLock;
    private Object wLock;
    private volatile Thread wlOwner;

    public Semaphore() {
        rLock = new Object();
        wLock = new Object();
        readLocks = 0;
        writeLocked = false;

    }

    public void getReadLock() {
        synchronized (wLock) {
            while (writeLocked) {
//System.out.println("Threads. Current: "+Thread.currentThread()+" WL owher: "+wlOwner);
                if (wlOwner != Thread.currentThread()) {
                    try {
                        wLock.wait();
                    } catch (InterruptedException ex) {
                    }
                } else {
                    break;
                }

            }
        }

        synchronized (rLock) {
            readLocks++;
        }
    }

    public void getWriteLock() {
        synchronized (wLock) {
            while (writeLocked) {
                if (wlOwner == Thread.currentThread()) {
                    return;
                }

                try {
                    wLock.wait();
                } catch (InterruptedException ex) {
                }
            }

            writeLocked = true;

            synchronized (rLock) {
                while (readLocks > 0) {
                    try {
                        rLock.wait();
                    } catch (InterruptedException ex) {
                    }
                }
            }

            wlOwner = Thread.currentThread();
        }
    }


    public void releaseReadLock() {
        synchronized (rLock) {
            readLocks--;
            rLock.notifyAll();
        }
    }

    public void releaseWriteLock() {
        synchronized (wLock) {
            wlOwner = null;
            writeLocked = false;
            wLock.notifyAll();
        }
    }

}

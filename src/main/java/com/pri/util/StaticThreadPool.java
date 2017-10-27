package com.pri.util;

public class StaticThreadPool implements ThreadPool {

    ThreadRef tpool[];
    int nTh;
    volatile int freeCounter;
    int nreqs = 0;
    int nlocks = 0;
    long totalWaitTime = 0;

    public StaticThreadPool(int n) {
        tpool = new ThreadRef[n];
        nTh = n;
        freeCounter = 0;

        for (int i = 0; i < n; i++) {
            tpool[i] = new ThreadRef(this);
        }
    }

    public void destroy() {
        for (int i = 0; i < nTh; i++) {
            tpool[i].die();
        }
    }

    public void execute(Runnable r) {
        nreqs++;
        ThreadRef t = getFreeThread();

        t.execute(r);
    }

    synchronized ThreadRef getFreeThread() {
        while (true) {
            if (freeCounter > 0) {
                for (int i = 0; i < nTh; i++) {
                    if (tpool[i].allocate()) {
                        return tpool[i];
                    }
                }
            }
            try {
                long wt = System.currentTimeMillis();

                //	System.out.println("Lock Tread: "+Thread.currentThread().getName());

                nlocks++;

                wait();

                totalWaitTime += System.currentTimeMillis() - wt;
            } catch (InterruptedException e) {
            }
        }

    }

    synchronized void threadIdle() {
        freeCounter++;
        notify();
    }

    void threadBusy() {
        freeCounter--;
    }

    public int getNReqs() {
        return nreqs;
    }

    public int getNLocks() {
        return nlocks;
    }

    public long getTotalWaitTime() {
        return totalWaitTime;
    }

    protected void finalize() throws Throwable {
        destroy();
        super.finalize();
    }

    class ThreadRef extends Thread {

        Runnable toRun;

        boolean idle;

        StaticThreadPool tPool;

        boolean mustDie = false;

        public ThreadRef(StaticThreadPool pool) {
            tPool = pool;
            setIdle(true);
            start();
        }

        synchronized public void die() {
            mustDie = true;
            notify();
        }

        public void run() {
            while (true) {
                if (mustDie) {
                    return;
                }

                waitExec();

                if (mustDie || toRun == null) {
                    return;
                }

                toRun.run();
                toRun = null;
            }
        }

        public void setRunnable(Runnable r) {
            toRun = r;
        }

        void setIdle(boolean state) {
            idle = state;

            if (idle) {
                tPool.threadIdle();
            } else {
                tPool.threadBusy();
            }

        }

        public boolean allocate() {
            if (idle) {
                setIdle(false);
                return true;
            }

            return false;
        }

        public synchronized void execute(Runnable r) {
            setRunnable(r);
            notify();
        }

        synchronized void waitExec() {

            try {
                setIdle(true);
                wait();
            } catch (InterruptedException e) {
            }
        }
    }
}
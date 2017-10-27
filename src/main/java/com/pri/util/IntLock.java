package com.pri.util;

import com.pri.util.collection.IntMap;
import com.pri.util.collection.IntTreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class IntLock {

    Lock lock = new ReentrantLock();

    IntMap<CntLock> lockMap = new IntTreeMap<CntLock>();
    ObjectRecycler<CntLock> cache = new ObjectRecycler<CntLock>(4);

    public IntLock() {
    }

    public void lock(int id) {
        CntLock clk = null;
        try {
            lock.lock();

            clk = lockMap.get(id);

            if (clk == null) {
                clk = cache.getObject();

                if (clk == null) {
                    clk = new CntLock();
                }

                lockMap.put(id, clk);
            }

            clk.count++;
        } finally {
            lock.unlock();
        }

        clk.lock.lock();
    }

    public void unlock(int id) {
        CntLock clk = null;
        try {
            lock.lock();

            clk = lockMap.get(id);

            if (clk == null) {
                return;
            }

            clk.lock.unlock();
            clk.count--;

            if (clk.count == 0) {
                lockMap.remove(id);
                cache.recycleObject(clk);
            }
        } finally {
            lock.unlock();
        }
    }

    static class CntLock {

        int count = 0;
        Lock lock = new ReentrantLock();
    }
}

package com.pri.util;

public class ObjectRecycler<T> {

    // int maxSize,size;
    ObjectHolder head;
    ObjectHolder tail;

    public ObjectRecycler(int sz) {
//  maxSize=sz;
//  size=0;

        head = new ObjectHolder();
        ObjectHolder cHldr = head;
        while (--sz > 0) {
            cHldr.next = new ObjectHolder();
            cHldr = cHldr.next;
        }

        cHldr.next = head;
        tail = head;
    }

    public synchronized T getObject() {
        if (tail == head) {
            return null;
        }

        T obj = head.obj;
        head.obj = null;
        head = head.next;
//  size--;

        return obj;
    }

    public synchronized void recycleObject(T obj) {
        if (tail.next == head) {
            return;
        }

        tail.obj = obj;
        tail = tail.next;
//  size++;
    }

    private class ObjectHolder {

        ObjectHolder next;
        T obj;
    }

}

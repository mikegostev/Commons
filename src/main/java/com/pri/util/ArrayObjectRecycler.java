package com.pri.util;


public class ArrayObjectRecycler<T> {

    private Object[] buf;
    int ptr = 0;

    public ArrayObjectRecycler(int sz) {
        buf = new Object[sz];
    }

    @SuppressWarnings("unchecked")
    public synchronized T getObject() {
        if (ptr == 0) {
            return null;
        }

        return (T) buf[--ptr];
    }

    public synchronized void recycleObject(T obj) {
        if (ptr == buf.length) {
            return;
        }

        buf[ptr++] = obj;
    }

}

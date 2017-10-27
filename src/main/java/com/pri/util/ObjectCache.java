package com.pri.util;

import com.pri.util.collection.IntMap;
import com.pri.util.collection.IntTreeMap;

public class ObjectCache<T extends IntID> {

    private int cacheSize;
    private int cacheMaxSize;
    private IntMap<ListEl<T>> cacheMap = new IntTreeMap<ListEl<T>>();
    private ListEl<T> token = new ListEl<T>();

    public ObjectCache(int sz) {
        cacheMaxSize = sz;
        cacheSize = 0;
        token.prev = token.next = token;
    }

    public synchronized boolean cache(T obj) {
        ListEl<T> strd = cacheMap.get(((IntID) obj).getID());

        if (strd == null) {
            if (cacheSize < cacheMaxSize) {
                strd = new ListEl<T>();
                strd.id = ((IntID) obj).getID();
                strd.obj = obj;

                strd.next = token.next;
                token.next = strd;
                strd.next.prev = strd;
                strd.prev = token;
                cacheMap.put(((IntID) obj).getID(), strd);
                cacheSize++;
                return true;
            }

            strd = token.prev;
            cacheMap.remove(strd.id);
            strd.id = ((IntID) obj).getID();

            strd.obj = obj;

            strd.prev.next = token;
            token.prev = strd.prev;
            strd.next = token.next;
            token.next = strd;
            strd.prev = token;
            strd.next.prev = strd;

            return true;
        }

        strd.obj = obj;

        if (token.next == strd) {
            return false;
        }

        strd.prev.next = strd.next;
        strd.next.prev = strd.prev;

        token.next.prev = strd;
        strd.next = token.next;
        token.next = strd;
        strd.prev = token;

        return false;
    }

    public synchronized T fetch(int id) {
        ListEl<T> strd = cacheMap.get(id);

        if (strd == null) {
            return null;
        }

        if (token.next != strd) {
            strd.prev.next = strd.next;
            strd.next.prev = strd.prev;

            token.next.prev = strd;
            strd.next = token.next;
            token.next = strd;
            strd.prev = token;
        }

        return strd.obj;
    }

    private static class ListEl<T> {

        int id;
        ListEl<T> next;
        ListEl<T> prev;
        T obj;
    }

}

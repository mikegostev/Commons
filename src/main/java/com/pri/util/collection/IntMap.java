package com.pri.util.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


public interface IntMap<V> {

    int size();

    boolean isEmpty();

    boolean containsKey(int key);

    boolean containsValue(Object value);

    V get(int key);

    V put(int key, V value);

    V remove(int key);

    void putAll(Map<Integer, ? extends V> t);

    void putAll(IntMap<? extends V> t);

    void clear();

    Set<Integer> keySet();

    IntIterator keyIterator();

    Collection<V> values();

    Set<IntMap.Entry<V>> entrySet();

    boolean equals(Object o);

    int hashCode();

    interface Entry<VL> {

        int getKey();

        VL getValue();

        VL setValue(VL value);

        boolean equals(Object o);

        int hashCode();
    }

}

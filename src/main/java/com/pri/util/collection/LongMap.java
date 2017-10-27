package com.pri.util.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


public interface LongMap<V> {

    int size();

    boolean isEmpty();

    boolean containsKey(long key);

    boolean containsValue(Object value);

    V get(long key);

    V put(long key, V value);

    V remove(long key);

    void putAll(Map<Long, ? extends V> t);

    void putAll(LongMap<? extends V> t);

    void clear();

    Set<Long> keySet();

    Collection<V> values();

    Set<LongMap.Entry<V>> entrySet();

    boolean equals(Object o);

    int hashCode();

    interface Entry<VL> {

        long getKey();

        VL getValue();

        VL setValue(VL value);

        boolean equals(Object o);

        int hashCode();
    }

}

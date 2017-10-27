package com.pri.util.collection;

public interface MapIterator<K, V> {

    boolean next();

    V get(K key);
}

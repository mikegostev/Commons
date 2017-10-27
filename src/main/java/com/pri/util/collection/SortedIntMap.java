package com.pri.util.collection;

import java.util.Comparator;

public interface SortedIntMap<T> extends IntMap<T> {

    Comparator<Integer> comparator();

    SortedIntMap<T> subMap(int fromKey, int toKey);

    SortedIntMap<T> headMap(int toKey);

    SortedIntMap<T> tailMap(int fromKey);

    int firstKey();

    int lastKey();
}

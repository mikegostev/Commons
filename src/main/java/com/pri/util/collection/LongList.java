package com.pri.util.collection;

import java.util.Collection;
import java.util.Iterator;

public interface LongList {

    int size();

    boolean isEmpty();

    boolean contains(long o);

    Iterator<Long> iterator();

    long[] toArray();

    boolean add(long o);

    boolean remove(long o);

    void clear();

    boolean equals(Object o);

    int hashCode();

    long get(int index);

    long set(int index, long element);

    void add(int index, long element);

    public boolean addAll(int index, Collection<Long> c);

    long removeAt(int index);

    int indexOf(long o);

    int lastIndexOf(long o);

    LongListIterator listIterator();

    LongListIterator listIterator(int index);

    LongList subList(int fromIndex, int toIndex);
}

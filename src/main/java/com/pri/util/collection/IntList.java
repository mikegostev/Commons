package com.pri.util.collection;

import java.util.Collection;
import java.util.Iterator;

public interface IntList extends Iterable<Integer> {

    int size();

    boolean isEmpty();

    boolean contains(int o);

    @Override
    Iterator<Integer> iterator();

    int[] toArray();

    boolean add(int o);

    boolean remove(int o);

    void clear();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();

    int get(int index);

    int set(int index, int element);

    void add(int index, int element);

    public boolean addAll(int index, Collection<Integer> c);

    int removeAt(int index);

    int indexOf(int o);

    int lastIndexOf(int o);

    IntListIterator listIterator();

    IntListIterator listIterator(int index);

    IntList subList(int fromIndex, int toIndex);

}

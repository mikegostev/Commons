package com.pri.util.collection;

public interface IntListIterator extends IntIterator {

    boolean hasNext();

    int next();

    boolean hasPrevious();

    int previous();

    int nextIndex();

    int previousIndex();

    void remove();

    void set(int o);

    void add(int o);
}

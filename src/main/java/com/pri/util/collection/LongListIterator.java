package com.pri.util.collection;

public interface LongListIterator extends LongIterator {

    boolean hasNext();

    long next();

    boolean hasPrevious();

    long previous();

    int nextIndex();

    int previousIndex();

    void remove();

    void set(long o);

    void add(long o);
}

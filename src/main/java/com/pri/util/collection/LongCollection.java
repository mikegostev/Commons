package com.pri.util.collection;

public interface LongCollection extends LongIterable, Iterable<Long> {
    // Query Operations

    int size();

    boolean isEmpty();

    boolean contains(long o);

    @Override
    LongIterator longIterator();

    long[] toArray();

    long[] toArray(long[] a);

    // Modification Operations

    boolean add(long e);

    boolean remove(long o);

    // Bulk Operations

    boolean containsAll(LongCollection c);

    boolean addAll(LongCollection c);

    boolean removeAll(LongCollection c);

    boolean retainAll(LongCollection c);

    void clear();

    // Comparison and hashing

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}

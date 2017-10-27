package com.pri.util.collection;

public interface LongSet extends LongCollection {
    // Query Operations

    @Override
    int size();

    @Override
    boolean isEmpty();

    @Override
    boolean contains(long o);

    @Override
    LongIterator longIterator();

    @Override
    long[] toArray();

    @Override
    long[] toArray(long[] a);

    // Modification Operations

    @Override
    boolean add(long e);

    @Override
    boolean remove(long o);

    // Bulk Operations

    @Override
    boolean containsAll(LongCollection c);

    @Override
    boolean addAll(LongCollection c);

    @Override
    boolean retainAll(LongCollection c);

    @Override
    boolean removeAll(LongCollection c);

    @Override
    void clear();

    // Comparison and hashing

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}

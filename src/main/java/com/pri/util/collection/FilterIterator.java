package com.pri.util.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class FilterIterator<T> implements Iterator<T> {

    private final Iterator<? extends T> iter;
    private final Predicate<T> predicate;
    private boolean ready = false;
    private T nextEl;

    public FilterIterator(Iterator<? extends T> it, Predicate<T> pr) {
        iter = it;
        predicate = pr;
    }

    @Override
    public boolean hasNext() {
        if (ready) {
            return true;
        }

        while (iter.hasNext()) {
            nextEl = iter.next();
            ready = true;

            if (predicate.evaluate(nextEl)) {
                return true;
            }
        }

        nextEl = null;
        return false;
    }

    @Override
    public T next() {
        if (ready) {
            ready = false;
            return nextEl;
        }

        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        ready = false;
        return nextEl;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}

/*
 * Created on 29.06.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.util.collection;

import java.io.Serializable;
import java.util.ListIterator;
import java.util.NoSuchElementException;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class EmptyIterator<T> implements ListIterator<T>, Serializable {

    private static final long serialVersionUID = 020110716L;

    private static EmptyIterator<Object> instance = new EmptyIterator<Object>();

    private EmptyIterator() {
    }

    @SuppressWarnings("unchecked")
    public static <T> EmptyIterator<T> getInstance() {
        return (EmptyIterator<T>) instance;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return false;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public T next() {
        throw new NoSuchElementException();
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }

    @Override
    public T previous() {
        throw new NoSuchElementException();
    }

    @Override
    public int nextIndex() {
        return 0;
    }

    @Override
    public int previousIndex() {
        return -1;
    }

    @Override
    public void set(T e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(T e) {
        throw new UnsupportedOperationException();
    }

}

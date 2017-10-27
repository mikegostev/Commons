package com.pri.util.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class LinkedListVer<E> extends LinkedList<E> implements ListVer<E> {

    private int version;

    public LinkedListVer() {
        super();
    }

    public int version() {
        return version;
    }


    public boolean add(E arg0) {
        boolean chg = super.add(arg0);

        if (chg) {
            version++;
        }

        return chg;
    }

    public boolean remove(Object arg0) {
        boolean chg = super.remove(arg0);

        if (chg) {
            version++;
        }

        return chg;
    }


    public boolean addAll(Collection<? extends E> arg0) {
        boolean chg = super.addAll(arg0);

        if (chg) {
            version++;
        }

        return chg;
    }

    public boolean addAll(int arg0, Collection<? extends E> arg1) {
        boolean chg = super.addAll(arg0, arg1);

        if (chg) {
            version++;
        }

        return chg;
    }


    public void clear() {
        if (size() > 0) {
            version++;
        }

        super.clear();
    }

    public E set(int arg0, E arg1) {
        E el = super.set(arg0, arg1);
        version++;
        return el;
    }

    public void add(int arg0, E arg1) {
        super.add(arg0, arg1);
        version++;
    }

    public E remove(int arg0) {
        E el = super.remove(arg0);
        version++;
        return el;
    }

    public ListIterator<E> listIterator() {
        return new ListVerListIterator();
    }

    public ListIterator<E> listIterator(int arg0) {
        return new ListVerListIterator(arg0);
    }

    public Iterator<E> iterator() {
        return new ListVerIterator();
    }

    private Iterator<E> superIter() {
        return super.iterator();
    }

    private class ListVerIterator implements Iterator<E> {

        private Iterator<E> iter;

        public ListVerIterator() {
            iter = superIter();
        }

        public boolean hasNext() {
            return iter.hasNext();
        }

        public E next() {
            return iter.next();
        }

        public void remove() {
            iter.remove();
            version++;
        }
    }

    private class ListVerListIterator implements ListIterator<E> {

        private ListIterator<E> iter;

        public ListVerListIterator() {
            iter = LinkedListVer.super.listIterator();
        }

        public ListVerListIterator(int ind) {
            iter = LinkedListVer.super.listIterator(ind);
        }

        public boolean hasNext() {
            return iter.hasNext();
        }

        public E next() {
            return iter.next();
        }

        public boolean hasPrevious() {
            return iter.hasPrevious();
        }

        public E previous() {
            return iter.previous();
        }

        public int nextIndex() {
            return iter.nextIndex();
        }

        public int previousIndex() {
            return iter.previousIndex();
        }

        public void remove() {
            iter.remove();
            version++;
        }

        public void set(E arg0) {
            iter.set(arg0);
            version++;
        }

        public void add(E arg0) {
            iter.add(arg0);
            version++;
        }

    }
}

package com.pri.util.collection;

import java.util.Iterator;

/**
 * <p>A usefull implementation of an event-like queue. Supports adding and removing elements while iterating over the
 * queue. The implementation garantees that everytime you ask for an element from iterator it will use the most recent
 * information about elements. As a sideeffect the queue doesn't allow null elements since iteration cycle looks
 * something like this: </p> <code> ... Object next = null ; do { next = it.next () ; ... do work here ... } while (next
 * != null) ; </code>
 *
 * @author mike
 */
public class ConcurrentLinkedQueue<E> implements Iterable<E> {

    private final ListEl<E> marker = new ListEl<E>();
    private int size = 0;

    public ConcurrentLinkedQueue() {
        marker.prev = marker.next = marker;
    }

    public synchronized void add(E obj) {
        ListEl<E> el = new ListEl<E>();

        el.obj = obj;

        el.next = marker;
        el.prev = marker.prev;
        marker.prev.next = el;
        marker.prev = el;

        ListEl<E> sd = el.prev.side;
        while (sd != null) {
            sd.next = el;
            sd = sd.side;
        }

        size++;
    }

    private void removeElement(ListEl<E> el) {
        el.prev.next = el.next;
        el.next.prev = el.prev;

        ListEl<E> sd = el.prev.side;
        while (sd != null) {
            sd.next = el.next;
            sd = sd.side;
        }

        if (el.count != 0) {
            if (el.prev.side != null) {
                el.side = el.prev.side;
                el.side.prev = el;
            }

            el.prev.side = el;
        }

        size--;

    }

    public synchronized void remove(E obj) {
        ListEl<E> el = marker.next;

        while (el != marker) {
            if (el.obj.equals(obj)) {
                removeElement(el);
                return;
            }

            el = el.next;
        }
    }

    public synchronized boolean contains(E obj) {
        ListEl<E> el = marker.next;

        while (el != marker) {
            if (el.obj.equals(obj)) {
                return true;
            }

            el = el.next;
        }

        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new CLQIterator();
    }

    public synchronized int size() {
        return size;
    }

    @Override
    public synchronized String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("Size=").append(size).append(" [");

        ListEl<E> ptr = marker.next;

        if (marker.side != null) {
            sb.append("{");
            ListEl<E> ptr2 = marker.side;

            while (ptr2 != null) {
                sb.append(ptr2.obj.toString()).append(',');
                ptr2 = ptr2.side;
            }

            sb.deleteCharAt(sb.length() - 1);
            sb.append('}');
        }

        while (ptr != marker) {
            sb.append(ptr.obj.toString()).append('(').append(ptr.count).append(')');
            if (ptr.side != null) {
                sb.append("->{");
                ListEl<E> ptr2 = ptr.side;

                while (ptr2 != null) {
                    sb.append(ptr2.obj.toString()).append(',');
                    ptr2 = ptr2.side;
                }

                sb.deleteCharAt(sb.length() - 1);
                sb.append('}');
            }

            sb.append(',');
            ptr = ptr.next;
        }

        if (size > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        sb.append(']');

        return sb.toString();
    }

    private static class ListEl<T> {

        public ListEl<T> prev;
        public ListEl<T> next;
        public ListEl<T> side;
        public T obj;
        public int count = 0;
    }

    private class CLQIterator implements Iterator<E> {

        private ListEl<E> ptr;

        CLQIterator() {
            ptr = marker;
        }

        @Override
        public boolean hasNext() {
            return true;
        }

        @Override
        public E next() {
            synchronized (ConcurrentLinkedQueue.this) {
                if (ptr == null) {
                    return null;
                }

                ptr.count--;

                if (ptr.count == 0 && ptr.prev.side == ptr) {
                    ptr.prev.side = ptr.side;
                    if (ptr.side != null) {
                        ptr.side.prev = ptr.prev;
                    }
                }

                ptr = ptr.next;

                if (ptr == marker) {
                    ptr = null;
                    return null;
                }

                ptr.count++;

                return ptr.obj;
            }
        }

        @Override
        public void remove() {
            synchronized (ConcurrentLinkedQueue.this) {
                if (ptr.prev.side == ptr || ptr == marker) {
                    return;
                }

                removeElement(ptr);
            }
        }

        @Override
        public void finalize() {
            synchronized (ConcurrentLinkedQueue.this) {

                if (ptr == null) {
                    return;
                }

                ptr.count--;

                if (ptr.count == 0 && ptr.prev.side == ptr) {
                    ptr.prev.side = ptr.side;
                    if (ptr.side != null) {
                        ptr.side.prev = ptr.prev;
                    }
                }
            }

        }

    }

}

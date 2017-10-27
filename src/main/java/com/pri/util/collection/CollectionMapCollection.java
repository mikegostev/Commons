package com.pri.util.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class CollectionMapCollection<E> implements Collection<E> {

    private Map<?, ? extends Collection<E>> map;

    public CollectionMapCollection(Map<?, ? extends Collection<E>> m) {
        map = m;
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        for (Collection<E> c : map.values()) {
            if (c.contains(o)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public Iterator<E> iterator() {
        if (size() == 0) {
            return EmptyIterator.getInstance();
        }

        return new Iterator<E>() {

            Iterator<? extends Collection<E>> citer = map.values().iterator();

            Iterator<E> eiter = null;

            {
                while (citer.hasNext()) {
                    Collection<E> cl = citer.next();

                    if (cl != null) {
                        eiter = cl.iterator();
                        break;
                    }
                }
            }


            @Override
            public boolean hasNext() {
                if (eiter == null) {
                    return false;
                }

                while (true) {
                    if (eiter.hasNext()) {
                        return true;
                    }

                    if (!citer.hasNext()) {
                        return false;
                    }

                    eiter = null;

                    while (citer.hasNext()) {
                        Collection<E> cl = citer.next();

                        if (cl != null) {
                            eiter = cl.iterator();
                            break;
                        }
                    }

                }

            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                return eiter.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        int size = 0;

        for (Collection<E> c : map.values()) {
            if (c != null) {
                size += c.size();
            }
        }

        return size;
    }

    @Override
    public Object[] toArray() {
        int i = 0;

        Object[] res = new Object[size()];

        for (Collection<E> c : map.values()) {
            for (E el : c) {
                res[i++] = el;
            }
        }

        return res;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

}

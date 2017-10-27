package com.pri.util.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class MapValueCollectionUnion<K, E> implements Collection<E> {

    private final Collection<Map<K, E>> map;

// public MapValueCollectionUnion( Collection<Map<?,E>> m )
// {
//  map=m;
// }

    public MapValueCollectionUnion(Collection<Map<K, E>> v) {
        map = v;
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
        for (Map<?, ? extends E> c : map) {
            if (c.containsValue(o)) {
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

            Iterator<Map<K, E>> citer = map.iterator();

            Iterator<? extends E> eiter = null;

            {
                while (citer.hasNext()) {
                    Map<?, ? extends E> cl = citer.next();

                    if (cl != null) {
                        eiter = cl.values().iterator();
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
                        Map<?, ? extends E> cl = citer.next();

                        if (cl != null) {
                            eiter = cl.values().iterator();
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

        for (Map<?, ? extends E> c : map) {
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

        for (Map<?, ? extends E> c : map) {
            for (E el : c.values()) {
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

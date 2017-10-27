package com.pri.util.collection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class CollectionsUnion<E> implements Collection<E>, Serializable {

    private static final long serialVersionUID = 1750397661035015383L;

    private Collection<? extends Collection<? extends E>> collecs;
    private int size = 0;

// public CollectionsUnion( Collection<E> ... cs )
// {
//  Collection<Collection<E>> col = new ArrayList<Collection<E>>( cs.length );
//  
//  for( Collection<E> c : cs )
//  {
//   if( c == null )
//    continue;
//   
//   col.add(c);
//   size+=c.size();
//  }
//  
//  collecs=col;
// }

    public CollectionsUnion(Collection<? extends E>... cs) {
        Collection<Collection<? extends E>> col = new ArrayList<Collection<? extends E>>(cs.length);

        for (Collection<? extends E> c : cs) {
            if (c == null) {
                continue;
            }

            col.add(c);
            size += c.size();
        }

        collecs = col;
    }

    public CollectionsUnion(Collection<? extends Collection<? extends E>> cs) {
        collecs = cs;

        for (Collection<? extends E> c : collecs) {
            if (c != null) {
                size += c.size();
            }
        }
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
        for (Collection<? extends E> c : collecs) {
            if (c != null && c.contains(o)) {
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
        if (size == 0) {
            return EmptyIterator.getInstance();
        }

        return new Iterator<E>() {

            Iterator<? extends Collection<? extends E>> citer = collecs.iterator();
            Iterator<? extends E> eiter = null;

            {
                while (citer.hasNext()) {
                    Collection<? extends E> c = citer.next();

                    if (c != null) {
                        eiter = c.iterator();
                        break;
                    }
                }
            }

            @Override
            public boolean hasNext() {
                while (true) {
                    if (eiter != null && eiter.hasNext()) {
                        return true;
                    }

                    if (!citer.hasNext()) {
                        return false;
                    }

                    eiter = null;

                    while (citer.hasNext()) {
                        Collection<? extends E> c = citer.next();

                        if (c != null) {
                            eiter = c.iterator();
                            break;
                        }
                    }
                }

            }

            @Override
            public E next() {
                hasNext();
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
        return size;
    }

    @Override
    public Object[] toArray() {
        int i = 0;

        Object[] res = new Object[size];

        for (Collection<? extends E> c : collecs) {
            if (c == null) {
                continue;
            }

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

package com.pri.util.collection;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

public class ListUnionRO<E> extends AbstractList<E> implements List<E>, Serializable {

    private static final long serialVersionUID = 1750397661035015383L;

    private List<List<? extends E>> lists;
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

    @SafeVarargs
    public ListUnionRO(List<? extends E>... cs) {
        lists = Arrays.asList(cs);

        for (List<? extends E> c : cs) {
            if (c == null) {
                continue;
            }

            size += c.size();
        }

    }

    public ListUnionRO(Collection<List<? extends E>> cs) {
        if (cs instanceof List && cs instanceof RandomAccess) {
            lists = (List<List<? extends E>>) cs;
        } else {
            lists = new ArrayList<List<? extends E>>(cs);
        }

        for (Collection<? extends E> c : lists) {
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
        for (Collection<? extends E> c : lists) {
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

            Iterator<? extends Collection<? extends E>> citer = lists.iterator();
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

        for (Collection<? extends E> c : lists) {
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

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E get(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException();
        }

        for (List<? extends E> l : lists) {
            if (l == null) {
                continue;
            }

            if (index < l.size()) {
                return l.get(index);
            }

            index -= l.size();
        }

        return null;
    }

    @Override
    public int indexOf(Object o) {
        int i = 0;

        for (List<? extends E> l : lists) {
            if (l == null) {
                continue;
            }

            int li = l.indexOf(o);

            if (li >= 0) {
                return i + li;
            }

            i += l.size();
        }

        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        List<? extends List<? extends E>> listlist = (lists instanceof List) ? (List<? extends List<? extends E>>) lists
                : new ArrayList<List<? extends E>>(lists);

        int i = -1;

        for (int j = listlist.size() - 1; j >= 0; j--) {
            List<? extends E> l = listlist.get(j);

            if (l == null) {
                continue;
            }

            if (i == -1) {
                i = l.indexOf(o);
            } else {
                i += l.size();
            }
        }

        return i;
    }

    @Override
    public ListIterator<E> listIterator() {
        return new Itr(0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return new Itr(index);
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    protected class Itr implements ListIterator<E> {

        ListIterator<List<? extends E>> citer = null;
        ListIterator<? extends E> eiter = null;
        List<? extends E> cList = null;

        int listOffs = 0;
        int elOffs;

        Itr(int idx) {
            if (!(lists instanceof List)) {
                lists = new ArrayList<List<? extends E>>(lists);
            }

            citer = lists.listIterator();

            while (citer.hasNext()) {
                List<? extends E> c = citer.next();

                if (c == null) {
                    continue;
                }

                if (idx < c.size()) {
                    eiter = c.listIterator(idx);
                    elOffs = idx;
                    cList = c;
                    break;
                }

                idx -= c.size();
                listOffs += c.size();
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

                while (citer.hasNext()) {
                    List<? extends E> c = citer.next();

                    if (cList == c) {
                        continue;
                    }

                    if (c != null) {
                        cList = c;
                        eiter = c.listIterator();

                        listOffs += elOffs;
                        elOffs = 0;

                        break;
                    }
                }
            }

        }

        @Override
        public E next() {
            hasNext();
            elOffs++;
            return eiter.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasPrevious() {
            while (true) {
                if (eiter != null && eiter.hasPrevious()) {
                    return true;
                }

                if (!citer.hasPrevious()) {
                    return false;
                }

                while (citer.hasPrevious()) {
                    List<? extends E> c = citer.previous();

                    if (cList == c) {
                        continue;
                    }

                    if (c != null) {
                        listOffs -= c.size();
                        elOffs = c.size();

                        cList = c;
                        eiter = c.listIterator(c.size());
                        break;
                    }
                }
            }
        }

        @Override
        public E previous() {
            hasPrevious();
            elOffs--;
            return eiter.previous();
        }

        @Override
        public int nextIndex() {
            return listOffs + elOffs;
        }

        @Override
        public int previousIndex() {
            return listOffs + elOffs - 1;
        }

        @Override
        public void set(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(E e) {
            throw new UnsupportedOperationException();
        }
    }

}

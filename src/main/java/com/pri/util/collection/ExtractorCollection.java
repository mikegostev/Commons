package com.pri.util.collection;

import com.pri.util.Extractor;
import java.util.Collection;
import java.util.Iterator;

public class ExtractorCollection<SrcT, DstT> implements Collection<DstT> {

    private Extractor<SrcT, DstT> extr;
    private Collection<SrcT> srcColl;

    public ExtractorCollection(Collection<SrcT> sCl, Extractor<SrcT, DstT> ext) {
        srcColl = sCl;
        extr = ext;
    }

    @Override
    public int size() {
        return srcColl.size();
    }

    @Override
    public boolean isEmpty() {
        return srcColl.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        for (SrcT el : srcColl) {
            if (extr.extract(el).equals(o)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Iterator<DstT> iterator() {
        return new Iterator<DstT>() {
            Iterator<SrcT> srcIter = srcColl.iterator();

            @Override
            public boolean hasNext() {
                return srcIter.hasNext();
            }

            @Override
            public DstT next() {
                return extr.extract(srcIter.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };

    }

    @Override
    public Object[] toArray() {
        Object[] result = new Object[size()];

        int i = 0;
        for (Object el : this) {
            result[i++] = el;
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        if (a.length < size()) {
            a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size());
        }

        int i = 0;
        Object[] result = a;
        for (Object el : this) {
            result[i++] = el;
        }

        if (a.length > size()) {
            a[size()] = null;
        }

        return a;
    }

    @Override
    public boolean add(DstT e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends DstT> c) {
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
    public void clear() {
        throw new UnsupportedOperationException();
    }

}

package uk.ac.ebi.mg.collection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;

public class IndexList<NType, T extends Named<NType>> extends ArrayList<T> {

    private class ElCmp implements Serializable, Comparator<T> {

        private static final long serialVersionUID = 1L;

        @Override
        public int compare(T o1, T o2) {
            return comparator.compare(o1.getId(), o2.getId());
        }
    }

    private static final long serialVersionUID = 1L;

    private Comparator<NType> comparator;

    private Comparator<T> elComparator;

    public IndexList(Comparator<NType> cmp) {
        comparator = cmp;

        elComparator = new ElCmp();

    }


    public int indexOfKey(NType key) {
        int low = 0;
        int high = size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            T midVal = get(mid);
            int cmp = comparator.compare(midVal.getId(), key);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid; // key found
            }
        }
        return -(low + 1); // key not found
    }

    public T getByKey(NType key) {
        int ind = indexOfKey(key);

        if (ind >= 0) {
            return get(ind);
        }

        return null; // key not found
    }

    @Override
    public boolean add(T el) {
        boolean res = super.add(el);

        Collections.sort(this, elComparator);

        return res;
    }

    @Override
    public boolean addAll(Collection<? extends T> els) {
        boolean res = super.addAll(els);

        Collections.sort(this, elComparator);

        return res;
    }

    @Override
    public void add(int ind, T el) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int ind, Collection<? extends T> els) {
        throw new UnsupportedOperationException();
    }

    public T removeKey(NType key) {
        int ind = indexOfKey(key);

        if (ind >= 0) {
            return remove(ind);
        }

        return null; // key not found
    }

    @Override
    public boolean remove(Object o) {
        @SuppressWarnings("unchecked") boolean res = removeKey(((T) o).getId()) != null;

        Collections.sort(this, elComparator);

        return res;
    }

    @Override
    public T remove(int ind) {
        T res = super.remove(ind);

        Collections.sort(this, elComparator);

        return res;
    }


    @Override
    public boolean removeAll(Collection<?> coll) {
        boolean res = super.removeAll(coll);

        Collections.sort(this, elComparator);

        return res;
    }

    @Override
    public boolean retainAll(Collection<?> coll) {
        boolean res = super.retainAll(coll);

        Collections.sort(this, elComparator);

        return res;
    }

    @Override
    public ListIterator<T> listIterator() {
        return new ListIter();
    }

    @Override
    public ListIterator<T> listIterator(int i) {
        return new ListIter(i);
    }

    @Override
    public Iterator<T> iterator() {
        return new ListIter();
    }


    private class ListIter implements ListIterator<T> {

        private ListIterator<T> iter;

        ListIter() {
            iter = IndexList.super.listIterator();
        }

        ListIter(int i) {
            iter = IndexList.super.listIterator(i);
        }


        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public T next() {
            return iter.next();
        }

        @Override
        public boolean hasPrevious() {
            return iter.hasPrevious();
        }

        @Override
        public T previous() {
            return iter.previous();
        }

        @Override
        public int nextIndex() {
            return iter.nextIndex();
        }

        @Override
        public int previousIndex() {
            return iter.previousIndex();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(T e) {
            iter.set(e);
        }

        @Override
        public void add(T e) {
            throw new UnsupportedOperationException();
        }
    }
}

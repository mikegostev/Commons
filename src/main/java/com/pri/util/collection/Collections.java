package com.pri.util.collection;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;

public class Collections {

    private static List<Object> EMPTY_COLLECTION = new EmptyCollection();
    private static Map<Object, Object> EMPTY_MAP = new EmptyMap();

    @SuppressWarnings("unchecked")
    public static final <T> List<T> emptyList() {
        return (List<T>) EMPTY_COLLECTION;
    }

    @SuppressWarnings("unchecked")
    public static final <T> Set<T> emptySet() {
        return (Set<T>) EMPTY_COLLECTION;
    }

    @SuppressWarnings("unchecked")
    public static final <K, V> Map<K, V> emptyMap() {
        return (Map<K, V>) EMPTY_MAP;
    }

    /**
     * @serial include
     */
    private static class EmptyCollection implements List<Object>, Set<Object>, RandomAccess, Serializable {

        private static final long serialVersionUID = 020110716L;

        private Object readResolve() throws ObjectStreamException {
            return Collections.EMPTY_COLLECTION;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean contains(Object obj) {
            return false;
        }

        @Override
        public Object get(int index) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Iterator<Object> iterator() {
            return EmptyIterator.getInstance();
        }

        @Override
        public Spliterator<Object> spliterator() {
            return Spliterators.emptySpliterator();
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T[] toArray(T[] a) {
            return (T[]) new Object[0];
        }

        @Override
        public boolean add(Object e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends Object> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, Collection<? extends Object> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return false;
        }

        @Override
        public void clear() {
        }

        @Override
        public Object set(int index, Object element) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        @Override
        public void add(int index, Object element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object remove(int index) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        @Override
        public int indexOf(Object o) {
            return -1;
        }

        @Override
        public int lastIndexOf(Object o) {
            return -1;
        }

        @Override
        public ListIterator<Object> listIterator() {
            return EmptyIterator.getInstance();
        }

        @Override
        public ListIterator<Object> listIterator(int index) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        @Override
        public List<Object> subList(int fromIndex, int toIndex) {
            if (fromIndex != 0 || toIndex != 0) {
                throw new IndexOutOfBoundsException();
            }

            return this;
        }

    }

    private static class EmptyMap implements Map<Object, Object>, Serializable {

        private static final long serialVersionUID = 20111109L;

        private Object readResolve() throws ObjectStreamException {
            return Collections.EMPTY_MAP;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public boolean containsKey(Object key) {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public Object get(Object key) {
            return null;
        }

        @Override
        public Object put(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object remove(Object key) {
            return null;
        }

        @Override
        public void putAll(Map<? extends Object, ? extends Object> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
        }

        @Override
        public Set<Object> keySet() {
            return emptySet();
        }

        @Override
        public Collection<Object> values() {
            return emptySet();
        }

        @Override
        public Set<java.util.Map.Entry<Object, Object>> entrySet() {
            return emptySet();
        }

    }

    public static <T> Collection<T> compactCollection(Collection<T> lst) {
        if (lst == null || lst.size() == 0) {
            return emptyList();
        }

        if (lst.size() == 1) {
            return java.util.Collections.singletonList(lst.iterator().next());
        }

        return new ArrayList<T>(lst);
    }

    public static <T> List<T> compactList(List<T> lst) {
        if (lst == null || lst.size() == 0) {
            return emptyList();
        }

        if (lst.size() == 1) {
            return java.util.Collections.singletonList(lst.get(0));
        }

        return new ArrayList<T>(lst);
    }

    public static <T> List<T> addToCompactList(List<T> lst, T el) {
        if (lst == null) {
            lst = new ArrayList<T>();

            lst.add(el);

            return lst;
        }

        if (lst.size() <= 1 && !(lst instanceof ArrayList)) {
            List<T> newLst = new ArrayList<T>();

            newLst.addAll(lst);
            newLst.add(el);

            return newLst;
        }

        lst.add(el);
        return lst;
    }

    public interface Mapper<K, V> {

        V map(K key);
    }

    public static <T, K> int indexedBinarySearch(List<? extends T> l, K key, Comparator<? super K> c,
            Mapper<T, K> mapper) {
        int low = 0;
        int high = l.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            T midVal = l.get(mid);
            int cmp = c.compare(mapper.map(midVal), key);

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

    public static <T, K extends Comparable<K>> int indexedBinarySearch(List<T> list, K key, Mapper<T, K> mapper) {
        int low = 0;
        int high = list.size() - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            T midVal = list.get(mid);
            int cmp = mapper.map(midVal).compareTo(key);

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

}

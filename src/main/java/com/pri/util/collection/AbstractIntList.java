package com.pri.util.collection;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;


public abstract class AbstractIntList implements IntList

{

    protected AbstractIntList() {
    }

    public boolean add(int o) {
        add(size(), o);
        return true;
    }

    abstract public int get(int index);

    @SuppressWarnings("unused")
    public int set(int index, int element) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unused")
    public void add(int index, int element) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unused")
    public int removeAt(int index) {
        throw new UnsupportedOperationException();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int indexOf(int o) {
        IntListIterator e = listIterator();

        while (e.hasNext()) {
            if (o == e.next()) {
                return e.previousIndex();
            }
        }

        return -1;
    }

    public int lastIndexOf(int o) {
        IntListIterator e = listIterator(size());

        while (e.hasPrevious()) {
            if (o == e.previous()) {
                return e.nextIndex();
            }
        }

        return -1;
    }

    public void clear() {
        removeRange(0, size());
    }

    public int[] toArray() {
        int[] result = new int[size()];
        IntListIterator e = listIterator();
        for (int i = 0; e.hasNext(); i++) {
            result[i] = e.next();
        }
        return result;
    }

    public boolean contains(int o) {
        IntListIterator e = listIterator();

        while (e.hasNext()) {
            if (o == e.next()) {
                return true;
            }
        }
        return false;
    }

    public boolean addAll(int index, Collection<Integer> c) {
        boolean modified = false;
        Iterator<Integer> e = c.iterator();
        while (e.hasNext()) {
            add(index++, e.next().intValue());
            modified = true;
        }
        return modified;
    }

    public Iterator<Integer> iterator() {
        return new Itr();
    }

    public IntListIterator listIterator() {
        return listIterator(0);
    }

    public IntListIterator listIterator(final int index) {
        if (index < 0 || index > size()) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        return new ListItr(index);
    }

    private class Itr implements Iterator<Integer> {

        int cursor = 0;
        int lastRet = -1;
        int expectedModCount = modCount;

        public boolean hasNext() {
            return cursor != size();
        }

        public Integer next() {
            checkForComodification();
            try {
                Integer next = get(cursor);
                lastRet = cursor++;
                return next;
            } catch (IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (lastRet == -1) {
                throw new IllegalStateException();
            }
            checkForComodification();

            try {
                AbstractIntList.this.remove(lastRet);
                if (lastRet < cursor) {
                    cursor--;
                }
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    private class ListItr implements IntListIterator {

        int cursor = 0;
        int lastRet = -1;
        int expectedModCount = modCount;

        ListItr(int index) {
            cursor = index;
        }

        public boolean hasPrevious() {
            return cursor != 0;
        }

        public int previous() {
            checkForComodification();
            try {
                int i = cursor - 1;
                int previous = get(i);
                lastRet = cursor = i;
                return previous;
            } catch (IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException();
            }
        }

        public int nextIndex() {
            return cursor;
        }

        public int previousIndex() {
            return cursor - 1;
        }

        public void set(int o) {
            if (lastRet == -1) {
                throw new IllegalStateException();
            }
            checkForComodification();

            try {
                AbstractIntList.this.set(lastRet, o);
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        public void add(int o) {
            checkForComodification();

            try {
                AbstractIntList.this.add(cursor++, o);
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        public boolean hasNext() {
            return cursor != size();
        }

        public int next() {
            checkForComodification();
            try {
                Integer next = get(cursor);
                lastRet = cursor++;
                return next;
            } catch (IndexOutOfBoundsException e) {
                checkForComodification();
                throw new NoSuchElementException();
            }
        }

        public void remove() {
            if (lastRet == -1) {
                throw new IllegalStateException();
            }
            checkForComodification();

            try {
                AbstractIntList.this.remove(lastRet);
                if (lastRet < cursor) {
                    cursor--;
                }
                lastRet = -1;
                expectedModCount = modCount;
            } catch (IndexOutOfBoundsException e) {
                throw new ConcurrentModificationException();
            }
        }

        final void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }

    public IntList subList(int fromIndex, int toIndex) {
        return (this instanceof RandomAccess ? new RandomAccessSubList(this, fromIndex, toIndex)
                : new SubList(this, fromIndex, toIndex));
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof IntList)) {
            return false;
        }

        IntListIterator e1 = listIterator();
        IntListIterator e2 = ((IntList) o).listIterator();
        while (e1.hasNext() && e2.hasNext()) {
            if (e1.next() != e2.next()) {
                return false;
            }
        }
        return !(e1.hasNext() || e2.hasNext());
    }

    public int hashCode() {
        int hashCode = 1;
        IntListIterator i = listIterator();

        while (i.hasNext()) {
            int obj = i.next();
            hashCode = 31 * hashCode + obj;
        }
        return hashCode;
    }

    public boolean remove(int o) {
        IntListIterator e = listIterator();

        while (e.hasNext()) {
            if (o == e.next()) {
                e.remove();
                return true;
            }
        }

        return false;
    }

    protected void removeRange(int fromIndex, int toIndex) {
        IntListIterator it = listIterator(fromIndex);
        for (int i = 0, n = toIndex - fromIndex; i < n; i++) {
            it.next();
            it.remove();
        }
    }

    protected transient int modCount = 0;
}

class SubList extends AbstractIntList {

    private AbstractIntList l;
    private int offset;
    private int size;
    private int expectedModCount;

    SubList(AbstractIntList list, int fromIndex, int toIndex) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        }
        if (toIndex > list.size()) {
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        }
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        }
        l = list;
        offset = fromIndex;
        size = toIndex - fromIndex;
        expectedModCount = l.modCount;
    }

    public int set(int index, int element) {
        rangeCheck(index);
        checkForComodification();
        return l.set(index + offset, element);
    }

    public int get(int index) {
        rangeCheck(index);
        checkForComodification();
        return l.get(index + offset);
    }

    public int size() {
        checkForComodification();
        return size;
    }

    public void add(int index, int element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        checkForComodification();
        l.add(index + offset, element);
        expectedModCount = l.modCount;
        size++;
        modCount++;
    }

    public int removeAt(int index) {
        rangeCheck(index);
        checkForComodification();
        int result = l.removeAt(index + offset);
        expectedModCount = l.modCount;
        size--;
        modCount++;
        return result;
    }

    protected void removeRange(int fromIndex, int toIndex) {
        checkForComodification();
        l.removeRange(fromIndex + offset, toIndex + offset);
        expectedModCount = l.modCount;
        size -= (toIndex - fromIndex);
        modCount++;
    }

    public boolean addAll(Collection<Integer> c) {
        return addAll(size, c);
    }

    public boolean addAll(int index, Collection<Integer> c) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        int cSize = c.size();
        if (cSize == 0) {
            return false;
        }

        checkForComodification();
        l.addAll(offset + index, c);
        expectedModCount = l.modCount;
        size += cSize;
        modCount++;
        return true;
    }

    public Iterator<Integer> iterator() {
        checkForComodification();

        return new Iterator<Integer>() {
            private IntListIterator i = l.listIterator(offset);

            public boolean hasNext() {
                return nextIndex() < size;
            }

            public Integer next() {
                if (hasNext()) {
                    return i.next();
                } else {
                    throw new NoSuchElementException();
                }
            }


            public int nextIndex() {
                return i.nextIndex() - offset;
            }

            public void remove() {
                i.remove();
                expectedModCount = l.modCount;
                size--;
                modCount++;
            }

        };
    }

    public IntListIterator listIterator(final int index) {
        checkForComodification();
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        return new IntListIterator() {
            private IntListIterator i = l.listIterator(index + offset);

            public boolean hasNext() {
                return nextIndex() < size;
            }

            public int next() {
                if (hasNext()) {
                    return i.next();
                } else {
                    throw new NoSuchElementException();
                }
            }

            public boolean hasPrevious() {
                return previousIndex() >= 0;
            }

            public int previous() {
                if (hasPrevious()) {
                    return i.previous();
                } else {
                    throw new NoSuchElementException();
                }
            }

            public int nextIndex() {
                return i.nextIndex() - offset;
            }

            public int previousIndex() {
                return i.previousIndex() - offset;
            }

            public void remove() {
                i.remove();
                expectedModCount = l.modCount;
                size--;
                modCount++;
            }

            public void set(int o) {
                i.set(o);
            }

            public void add(int o) {
                i.add(o);
                expectedModCount = l.modCount;
                size++;
                modCount++;
            }
        };
    }

    public IntList subList(int fromIndex, int toIndex) {
        return new SubList(this, fromIndex, toIndex);
    }

    private void rangeCheck(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ",Size: " + size);
        }
    }

    private void checkForComodification() {
        if (l.modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }
}

class RandomAccessSubList extends SubList implements RandomAccess {

    RandomAccessSubList(AbstractIntList list, int fromIndex, int toIndex) {
        super(list, fromIndex, toIndex);
    }

    public IntList subList(int fromIndex, int toIndex) {
        return new RandomAccessSubList(this, fromIndex, toIndex);
    }
}

package com.pri.util.collection;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.RandomAccess;

public class ArrayIntList extends AbstractIntList implements IntList, RandomAccess, Cloneable, java.io.Serializable {

    private static final long serialVersionUID = 334L;

    private transient int[] elementData;
    private int size;

    public ArrayIntList(int initialCapacity) {
        super();
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        this.elementData = new int[initialCapacity];
    }

    public ArrayIntList() {
        this(10);
    }

    public ArrayIntList(Collection<Integer> c) {
        size = c.size();

        int capacity = (int) Math.min((size * 110L) / 100, Integer.MAX_VALUE);
        elementData = new int[capacity];

        int ind = 0;
        for (Integer el : c) {
            elementData[ind++] = el;
        }
    }

    public ArrayIntList(IntList c) {
        size = c.size();

        int capacity = (int) Math.min((size * 110L) / 100, Integer.MAX_VALUE);
        elementData = new int[capacity];

        int ind = 0;
        IntListIterator ili = c.listIterator();
        while (ili.hasNext()) {
            elementData[ind++] = ili.next();
        }
    }

    public void trimToSize() {
        modCount++;
        int oldCapacity = elementData.length;
        if (size < oldCapacity) {
            int oldData[] = elementData;
            elementData = new int[size];
            System.arraycopy(oldData, 0, elementData, 0, size);
        }
    }

    public void ensureCapacity(int minCapacity) {
        modCount++;
        int oldCapacity = elementData.length;
        if (minCapacity > oldCapacity) {
            int oldData[] = elementData;
            int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            elementData = new int[newCapacity];
            System.arraycopy(oldData, 0, elementData, 0, size);
        }
    }

    public int size() {
        return size;
    }

    public boolean contains(int elem) {
        return indexOf(elem) >= 0;
    }

    public int indexOf(int elem) {

        for (int i = 0; i < size; i++) {
            if (elem == elementData[i]) {
                return i;
            }
        }

        return -1;
    }

    public int lastIndexOf(int elem) {

        for (int i = size - 1; i >= 0; i--) {
            if (elem == elementData[i]) {
                return i;
            }
        }

        return -1;
    }

    public Object clone() {
        try {
            ArrayIntList v = (ArrayIntList) super.clone();
            v.elementData = new int[size];
            System.arraycopy(elementData, 0, v.elementData, 0, size);
            v.modCount = 0;
            return v;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError();
        }
    }

    public int[] toArray() {
        int[] result = new int[size];
        System.arraycopy(elementData, 0, result, 0, size);
        return result;
    }

    public int get(int index) {
        RangeCheck(index);

        return elementData[index];
    }

    public int set(int index, int element) {
        RangeCheck(index);

        int oldValue = elementData[index];
        elementData[index] = element;
        return oldValue;
    }

    public boolean add(int o) {
        ensureCapacity(size + 1); // Increments modCount!!
        elementData[size++] = o;
        return true;
    }

    public void add(int index, int element) {
        if (index > size || index < 0) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        ensureCapacity(size + 1); // Increments modCount!!
        System.arraycopy(elementData, index, elementData, index + 1, size - index);
        elementData[index] = element;
        size++;
    }

    public void addAll(int[] iarr) {
        ensureCapacity(size + iarr.length);
        System.arraycopy(iarr, 0, elementData, size, iarr.length);
        size += iarr.length;
    }

    public int removeAt(int index) {
        RangeCheck(index);

        modCount++;
        int oldValue = elementData[index];

        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elementData, index + 1, elementData, index, numMoved);
        }

        return oldValue;
    }

    public boolean remove(int o) {

        for (int index = 0; index < size; index++) {
            if (o == elementData[index]) {
                fastRemove(index);
                return true;
            }
        }

        return false;
    }

    private void fastRemove(int index) {
        modCount++;
        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elementData, index + 1, elementData, index, numMoved);
        }

        size--;
    }

    public void clear() {
        modCount++;

        size = 0;
    }

    protected void removeRange(int fromIndex, int toIndex) {
        modCount++;
        int numMoved = size - toIndex;
        System.arraycopy(elementData, toIndex, elementData, fromIndex, numMoved);

    }

    private void RangeCheck(int index) {
        if (index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
        int expectedModCount = modCount;
        // Write out element count, and any hidden stuff
        s.defaultWriteObject();

        // Write out array length
        s.writeInt(elementData.length);

        // Write out all elements in the proper order.
        for (int i = 0; i < size; i++) {
            s.writeInt(elementData[i]);
        }

        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append('[');

        for (int i = 0; i < size; i++) {
            sb.append(elementData[i]).append(',');
        }

        if (size > 0) {
            sb.setCharAt(sb.length() - 1, ']');
        } else {
            sb.append(']');
        }

        return sb.toString();
    }


    private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
        // Read in size, and any hidden stuff
        s.defaultReadObject();

        // Read in array length and allocate array
        int arrayLength = s.readInt();
        int[] a = elementData = new int[arrayLength];

        // Read in all elements in the proper order.
        for (int i = 0; i < size; i++) {
            a[i] = s.readInt();
        }
    }
}

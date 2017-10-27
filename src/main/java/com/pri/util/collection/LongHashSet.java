package com.pri.util.collection;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LongHashSet implements LongSet, Cloneable, Serializable {

    /**
     * The default initial capacity - MUST be a power of two.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The maximum capacity, used if a higher value is implicitly specified by either of the constructors with
     * arguments. MUST be a power of two <= 1<<30.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     **/
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The table, resized as necessary. Length MUST Always be a power of two.
     */
    transient Entry[] table;

    /**
     * The number of key-value mappings contained in this identity hash map.
     */
    transient int size;

    /**
     * The next size value at which to resize (capacity * load factor).
     *
     * @serial
     */
    int threshold;

    /**
     * The load factor for the hash table.
     *
     * @serial
     */
    final float loadFactor;

    /**
     * The number of times this HashMap has been structurally modified Structural modifications are those that change
     * the number of mappings in the HashMap or otherwise modify its internal structure (e.g., rehash).  This field is
     * used to make iterators on Collection-views of the HashMap fail-fast.  (See ConcurrentModificationException).
     */
    transient volatile int modCount;

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial capacity and load factor.
     *
     * @param initialCapacity The initial capacity.
     * @param loadFactor The load factor.
     * @throws IllegalArgumentException if the initial capacity is negative or the load factor is nonpositive.
     */
    public LongHashSet(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        if (initialCapacity > MAXIMUM_CAPACITY) {
            initialCapacity = MAXIMUM_CAPACITY;
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }

        // Find a power of 2 >= initialCapacity
        int capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }

        this.loadFactor = loadFactor;
        threshold = (int) (capacity * loadFactor);
        table = new Entry[capacity];
        init();
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial capacity and the default load factor (0.75).
     *
     * @param initialCapacity the initial capacity.
     * @throws IllegalArgumentException if the initial capacity is negative.
     */
    public LongHashSet(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the default initial capacity (16) and the default load factor (0.75).
     */
    public LongHashSet() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
        table = new Entry[DEFAULT_INITIAL_CAPACITY];
        init();
    }

    /**
     * Constructs a new <tt>HashMap</tt> with the same mappings as the specified <tt>Map</tt>.  The <tt>HashMap</tt> is
     * created with default load factor (0.75) and an initial capacity sufficient to hold the mappings in the specified
     * <tt>Map</tt>.
     *
     * @param m the map whose mappings are to be placed in this map.
     * @throws NullPointerException if the specified map is null.
     */
    public LongHashSet(LongCollection m) {
        this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1, DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
        putAllForCreate(m);
    }

    // internal utilities

    /**
     * Initialization hook for subclasses. This method is called in all constructors and pseudo-constructors (clone,
     * readObject) after HashMap has been initialized but before any entries have been inserted.  (In the absence of
     * this method, readObject would require explicit knowledge of subclasses.)
     */
    void init() {
    }

    /**
     * Returns a hash value for the specified object.  In addition to the object's own hashCode, this method applies a
     * "supplemental hash function," which defends against poor quality hash functions. This is critical because HashMap
     * uses power-of two length hash tables.<p>
     *
     * The shift distances in this function were chosen as the result of an automated search over the entire
     * four-dimensional search space.
     */
    static int hash(Object x) {
        int h = x.hashCode();

        h += ~(h << 9);
        h ^= (h >>> 14);
        h += (h << 4);
        h ^= (h >>> 10);
        return h;
    }

    /**
     * Check for equality of non-null reference x and possibly-null y.
     */
    static boolean eq(Object x, Object y) {
        return x == y || x.equals(y);
    }

    /**
     * Returns index for hash code h.
     */
    static int indexFor(int h, int length) {
        return h & (length - 1);
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }


    /**
     * Returns the entry associated with the specified key in the HashMap.  Returns null if the HashMap contains no
     * mapping for this key.
     */
    Entry getEntry(long key) {
        int i = indexFor(longHash(key), table.length);
        Entry e = table[i];
        while (e != null && !(e.key == key)) {
            e = e.next;
        }
        return e;
    }


    /**
     * This method is used instead of put by constructors and pseudoconstructors (clone, readObject).  It does not
     * resize the table, check for comodification, etc.  It calls createEntry rather than addEntry.
     */
    private void putForCreate(long key) {

        int i = indexFor(longHash(key), table.length);

        /**
         * Look for preexisting entry for key.  This will never happen for
         * clone or deserialize.  It will only happen for construction if the
         * input Map is a sorted map whose ordering is inconsistent w/ equals.
         */
        for (Entry e = table[i]; e != null; e = e.next) {
            if (e.key == key) {
                return;
            }
        }

        createEntry(key, i);
    }

    void putAllForCreate(LongCollection m) {
        for (LongIterator i = m.longIterator(); i.hasNext(); ) {
            long e = i.next();
            putForCreate(e);
        }
    }

    /**
     * Rehashes the contents of this map into a new array with a larger capacity.  This method is called automatically
     * when the number of keys in this map reaches its threshold.
     *
     * If current capacity is MAXIMUM_CAPACITY, this method does not resize the map, but sets threshold to
     * Integer.MAX_VALUE. This has the effect of preventing future calls.
     *
     * @param newCapacity the new capacity, MUST be a power of two; must be greater than current capacity unless current
     * capacity is MAXIMUM_CAPACITY (in which case value is irrelevant).
     */
    void resize(int newCapacity) {
        Entry[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        Entry[] newTable = new Entry[newCapacity];
        transfer(newTable);
        table = newTable;
        threshold = (int) (newCapacity * loadFactor);
    }

    /**
     * Transfer all entries from current table to newTable.
     */
    void transfer(Entry[] newTable) {
        Entry[] src = table;
        int newCapacity = newTable.length;
        for (int j = 0; j < src.length; j++) {
            Entry e = src[j];
            if (e != null) {
                src[j] = null;
                do {
                    Entry next = e.next;
                    int i = indexFor(longHash(e.key), newCapacity);
                    e.next = newTable[i];
                    newTable[i] = e;
                    e = next;
                } while (e != null);
            }
        }
    }


    /**
     * Removes and returns the entry associated with the specified key in the HashMap.  Returns null if the HashMap
     * contains no mapping for this key.
     */
    Entry removeEntryForKey(long key) {

        int i = indexFor(longHash(key), table.length);
        Entry prev = table[i];
        Entry e = prev;

        while (e != null) {
            Entry next = e.next;
            if (e.key == key) {
                modCount++;
                size--;
                if (prev == e) {
                    table[i] = next;
                } else {
                    prev.next = next;
                }
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }

        return e;
    }


    /**
     * Removes all mappings from this map.
     */
    @Override
    public void clear() {
        modCount++;
        Entry[] tab = table;
        for (int i = 0; i < tab.length; i++) {
            tab[i] = null;
        }
        size = 0;
    }


    /**
     * Returns a shallow copy of this <tt>HashMap</tt> instance: the keys and values themselves are not cloned.
     *
     * @return a shallow copy of this map.
     */
    @Override
    public Object clone() {
        LongHashSet result = null;
        try {
            result = (LongHashSet) super.clone();
        } catch (CloneNotSupportedException e) {
            // assert false;
        }

        result.table = new Entry[table.length];
        result.modCount = 0;
        result.size = 0;
        result.init();
        result.putAllForCreate(this);

        return result;
    }


    static class Entry {

        final long key;
        Entry next;

        /**
         * Create new entry.
         */
        Entry(long k, Entry n) {
            next = n;
            key = k;
        }

        public long getKey() {
            return key;
        }


        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }

            Entry e = (Entry) o;

            return getKey() == e.getKey();
        }

        @Override
        public int hashCode() {
            return longHash(key);
        }

        @Override
        public String toString() {
            return String.valueOf(getKey());
        }

        /**
         * This method is invoked whenever the value in an entry is overwritten by an invocation of put(k,v) for a key k
         * that's already in the HashMap.
         */
        void recordAccess(LongHashSet m) {
        }

        /**
         * This method is invoked whenever the entry is removed from the table.
         */
        void recordRemoval(LongHashSet m) {
        }
    }

    /**
     * Add a new entry with the specified key, value and hash code to the specified bucket.  It is the responsibility of
     * this method to resize the table if appropriate.
     *
     * Subclass overrides this to alter the behavior of put method.
     */
    void addEntry(long key, int bucketIndex) {
        Entry e = table[bucketIndex];
        table[bucketIndex] = new Entry(key, e);
        if (size++ >= threshold) {
            resize(2 * table.length);
        }
    }

    /**
     * Like addEntry except that this version is used when creating entries as part of Map construction or
     * "pseudo-construction" (cloning, deserialization).  This version needn't worry about resizing the table.
     *
     * Subclass overrides this to alter the behavior of HashMap(Map), clone, and readObject.
     */
    void createEntry(long key, int bucketIndex) {
        Entry e = table[bucketIndex];
        table[bucketIndex] = new Entry(key, e);
        size++;
    }

    private abstract class HashIterator<E> implements Iterator<E> {

        Entry next;            // next entry to return
        int expectedModCount; // For fast-fail
        int index;           // current slot
        Entry current;         // current entry

        HashIterator() {
            expectedModCount = modCount;
            Entry[] t = table;
            int i = t.length;
            Entry n = null;
            if (size != 0) { // advance to first entry
                while (i > 0 && (n = t[--i]) == null) {
                    ;
                }
            }
            next = n;
            index = i;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        Entry nextEntry() {
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            Entry e = next;
            if (e == null) {
                throw new NoSuchElementException();
            }

            Entry n = e.next;
            Entry[] t = table;
            int i = index;
            while (n == null && i > 0) {
                n = t[--i];
            }
            index = i;
            next = n;
            return current = e;
        }

        @Override
        public void remove() {
            if (current == null) {
                throw new IllegalStateException();
            }
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }

            long k = current.key;

            current = null;
            LongHashSet.this.removeEntryForKey(k);
            expectedModCount = modCount;
        }

    }


    private class KeyIterator extends HashIterator<Long> {

        @Override
        public Long next() {
            return nextEntry().getKey();
        }
    }

    private class EntryIterator extends HashIterator<Entry> {

        @Override
        public Entry next() {
            return nextEntry();
        }
    }

    // Subclass overrides these to alter behavior of views' iterator() method
    Iterator<Long> newKeyIterator() {
        return new KeyIterator();
    }


    Iterator<Entry> newEntryIterator() {
        return new EntryIterator();
    }


    /**
     * Save the state of the <tt>HashMap</tt> instance to a stream (i.e., serialize it).
     *
     * @serialData The <i>capacity</i> of the HashMap (the length of the bucket array) is emitted (int), followed  by
     * the <i>size</i> of the HashMap (the number of key-value mappings), followed by the key (Object) and value
     * (Object) for each key-value mapping represented by the HashMap The key-value mappings are emitted in the order
     * that they are returned by <tt>entrySet().iterator()</tt>.
     */
    private void writeObject(java.io.ObjectOutputStream s) throws IOException {
        // Write out the threshold, loadfactor, and any hidden stuff
        s.defaultWriteObject();

        // Write out number of buckets
        s.writeInt(table.length);

        // Write out size (number of Mappings)
        s.writeInt(size);

        // Write out keys and values (alternating)
        for (LongIterator i = longIterator(); i.hasNext(); ) {
            s.writeLong(i.next());
        }
    }

    private static final long serialVersionUID = 362498820763181265L;

    /**
     * Reconstitute the <tt>HashMap</tt> instance from a stream (i.e., deserialize it).
     */
    private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
        // Read in the threshold, loadfactor, and any hidden stuff
        s.defaultReadObject();

        // Read in number of buckets and allocate the bucket array;
        int numBuckets = s.readInt();
        table = new Entry[numBuckets];

        init(); // Give subclass a chance to do its thing.

        // Read in size (number of Mappings)
        int size = s.readInt();

        // Read the keys and values, and put the mappings in the HashMap
        for (int i = 0; i < size; i++) {
            long key = s.readLong();
            putForCreate(key);
        }
    }

    // These methods are used when serializing HashSets
    int capacity() {
        return table.length;
    }

    float loadFactor() {
        return loadFactor;
    }

    static int longHash(long value) {
        int h = (int) (value ^ (value >>> 32));

        h += ~(h << 9);
        h ^= (h >>> 14);
        h += (h << 4);
        h ^= (h >>> 10);

        return h;
    }

    @Override
    public Iterator<Long> iterator() {
        return newKeyIterator();
    }

    @Override
    public boolean contains(long key) {
        int i = indexFor(longHash(key), table.length);

        Entry e = table[i];

        while (e != null) {
            if (e.key == key) {
                return true;
            }
            e = e.next;
        }

        return false;
    }

    @Override
    public LongIterator longIterator() {
        return new LongIterator() {
            Iterator<Entry> itr = newEntryIterator();

            @Override
            public void remove() {
                itr.remove();
            }

            @Override
            public long next() {
                Entry e = itr.next();

                return e.key;
            }

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }
        };
    }

    @Override
    public long[] toArray() {
        // Estimate size of array; be prepared to see more or fewer elements
        long[] r = new long[size()];
        LongIterator it = longIterator();
        for (int i = 0; i < r.length; i++) {
            if (!it.hasNext()) // fewer elements than expected
            {
                return Arrays.copyOf(r, i);
            }
            r[i] = it.next();
        }
        return it.hasNext() ? finishToArray(r, it) : r;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns an array containing all the elements returned by this collection's iterator in the
     * same order, stored in consecutive elements of the array, starting with index {@code 0}. If the number of elements
     * returned by the iterator is too large to fit into the specified array, then the elements are returned in a newly
     * allocated array with length equal to the number of elements returned by the iterator, even if the size of this
     * collection changes during iteration, as might happen if the collection permits concurrent modification during
     * iteration.  The {@code size} method is called only as an optimization hint; the correct result is returned even
     * if the iterator returns a different number of elements.
     *
     * <p>This method is equivalent to:
     *
     * <pre> {@code
     * List<E> list = new ArrayList<E>(size());
     * for (E e : this)
     *     list.add(e);
     * return list.toArray(a);
     * }</pre>
     *
     * @throws ArrayStoreException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public long[] toArray(long[] a) {
        // Estimate size of array; be prepared to see more or fewer elements
        int size = size();
        long[] r = a.length >= size ? a : new long[size];

        LongIterator it = longIterator();

        for (int i = 0; i < r.length; i++) {
            if (!it.hasNext()) { // fewer elements than expected
                if (a == r) {
                    r[i] = 0; // null-terminate
                } else if (a.length < i) {
                    return Arrays.copyOf(r, i);
                } else {
                    System.arraycopy(r, 0, a, 0, i);
                    if (a.length > i) {
                        a[i] = 0;
                    }
                }
                return a;
            }
            r[i] = it.next();
        }
        // more elements than expected
        return it.hasNext() ? finishToArray(r, it) : r;
    }

    /**
     * The maximum size of array to allocate. Some VMs reserve some header words in an array. Attempts to allocate
     * larger arrays may result in OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Reallocates the array being used within toArray when the iterator returned more elements than expected, and
     * finishes filling it from the iterator.
     *
     * @param r the array, replete with previously stored elements
     * @param it the in-progress iterator over this collection
     * @return array containing the elements in the given array, plus any further elements returned by the iterator,
     * trimmed to size
     */
    private static long[] finishToArray(long[] r, LongIterator it) {
        int i = r.length;
        while (it.hasNext()) {
            int cap = r.length;
            if (i == cap) {
                int newCap = cap + (cap >> 1) + 1;
                // overflow-conscious code
                if (newCap - MAX_ARRAY_SIZE > 0) {
                    newCap = hugeCapacity(cap + 1);
                }
                r = Arrays.copyOf(r, newCap);
            }
            r[i++] = it.next();
        }
        // trim if overallocated
        return (i == r.length) ? r : Arrays.copyOf(r, i);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
        {
            throw new OutOfMemoryError("Required array size too large");
        }
        return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }


    @Override
    public boolean add(long key) {

        int i = indexFor(longHash(key), table.length);

        for (Entry e = table[i]; e != null; e = e.next) {
            if (e.key == key) {
                e.recordAccess(this);
                return false;
            }
        }

        modCount++;
        addEntry(key, i);
        return true;
    }

    @Override
    public boolean remove(long o) {
        Entry e = removeEntryForKey(o);
        return e == null;
    }

    @Override
    public boolean containsAll(LongCollection c) {
        LongIterator itr = c.longIterator();

        while (itr.hasNext()) {
            if (!contains(itr.next())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(LongCollection m) {
        int numKeysToBeAdded = m.size();
        if (numKeysToBeAdded == 0) {
            return false;
        }

  /*
   * Expand the map if the map if the number of mappings to be added
   * is greater than or equal to threshold.  This is conservative; the
   * obvious condition is (m.size() + size) >= threshold, but this
   * condition could result in a map with twice the appropriate capacity,
   * if the keys to be added overlap with the keys already in this map.
   * By using the conservative calculation, we subject ourself
   * to at most one extra resize.
   */
        if (numKeysToBeAdded > threshold) {
            int targetCapacity = (int) (numKeysToBeAdded / loadFactor + 1);
            if (targetCapacity > MAXIMUM_CAPACITY) {
                targetCapacity = MAXIMUM_CAPACITY;
            }
            int newCapacity = table.length;
            while (newCapacity < targetCapacity) {
                newCapacity <<= 1;
            }
            if (newCapacity > table.length) {
                resize(newCapacity);
            }
        }

        boolean modified = false;

        for (LongIterator i = m.longIterator(); i.hasNext(); ) {
            long e = i.next();

            if (add(e)) {
                modified = true;
            }
        }

        return modified;
    }

    @Override
    public boolean retainAll(LongCollection c) {
        boolean modified = false;
        LongIterator it = longIterator();

        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }

        return modified;
    }

    @Override
    public boolean removeAll(LongCollection c) {
        boolean modified = false;
        LongIterator it = longIterator();

        while (it.hasNext()) {
            if (c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }

        return modified;
    }
}

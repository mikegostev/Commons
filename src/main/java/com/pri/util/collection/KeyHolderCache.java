package com.pri.util.collection;

/*
 * Copyright (c) 1998, 2010, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;


/**
 * Hash table based implementation of the <tt>Map</tt> interface, with
 * <em>weak keys</em>.
 * An entry in a <tt>WeakHashMap</tt> will automatically be removed when
 * its key is no longer in ordinary use.  More precisely, the presence of a
 * mapping for a given key will not prevent the key from being discarded by the
 * garbage collector, that is, made finalizable, finalized, and then reclaimed.
 * When a key has been discarded its entry is effectively removed from the map,
 * so this class behaves somewhat differently from other <tt>Map</tt>
 * implementations.
 *
 * <p> Both null values and the null key are supported. This class has
 * performance characteristics similar to those of the <tt>HashMap</tt>
 * class, and has the same efficiency parameters of <em>initial capacity</em>
 * and <em>load factor</em>.
 *
 * <p> Like most collection classes, this class is not synchronized.
 * A synchronized <tt>WeakHashMap</tt> may be constructed using the
 * {@link Collections#synchronizedMap Collections.synchronizedMap}
 * method.
 *
 * <p> This class is intended primarily for use with key objects whose
 * <tt>equals</tt> methods test for object identity using the
 * <tt>==</tt> operator.  Once such a key is discarded it can never be
 * recreated, so it is impossible to do a lookup of that key in a
 * <tt>WeakHashMap</tt> at some later time and be surprised that its entry
 * has been removed.  This class will work perfectly well with key objects
 * whose <tt>equals</tt> methods are not based upon object identity, such
 * as <tt>String</tt> instances.  With such recreatable key objects,
 * however, the automatic removal of <tt>WeakHashMap</tt> entries whose
 * keys have been discarded may prove to be confusing.
 *
 * <p> The behavior of the <tt>WeakHashMap</tt> class depends in part upon
 * the actions of the garbage collector, so several familiar (though not
 * required) <tt>Map</tt> invariants do not hold for this class.  Because
 * the garbage collector may discard keys at any time, a
 * <tt>WeakHashMap</tt> may behave as though an unknown thread is silently
 * removing entries.  In particular, even if you synchronize on a
 * <tt>WeakHashMap</tt> instance and invoke none of its mutator methods, it
 * is possible for the <tt>size</tt> method to return smaller values over
 * time, for the <tt>isEmpty</tt> method to return <tt>false</tt> and
 * then <tt>true</tt>, for the <tt>containsKey</tt> method to return
 * <tt>true</tt> and later <tt>false</tt> for a given key, for the
 * <tt>get</tt> method to return a value for a given key but later return
 * <tt>null</tt>, for the <tt>put</tt> method to return
 * <tt>null</tt> and the <tt>remove</tt> method to return
 * <tt>false</tt> for a key that previously appeared to be in the map, and
 * for successive examinations of the key set, the value collection, and
 * the entry set to yield successively smaller numbers of elements.
 *
 * <p> Each key object in a <tt>WeakHashMap</tt> is stored indirectly as
 * the referent of a weak reference.  Therefore a key will automatically be
 * removed only after the weak references to it, both inside and outside of the
 * map, have been cleared by the garbage collector.
 *
 * <p> <strong>Implementation note:</strong> The value objects in a
 * <tt>WeakHashMap</tt> are held by ordinary strong references.  Thus care
 * should be taken to ensure that value objects do not strongly refer to their
 * own keys, either directly or indirectly, since that will prevent the keys
 * from being discarded.  Note that a value object may refer indirectly to its
 * key via the <tt>WeakHashMap</tt> itself; that is, a value object may
 * strongly refer to some other key object whose associated value object, in
 * turn, strongly refers to the key of the first value object.  One way
 * to deal with this is to wrap values themselves within
 * <tt>WeakReferences</tt> before
 * inserting, as in: <tt>m.put(key, new WeakReference(value))</tt>,
 * and then unwrapping upon each <tt>get</tt>.
 *
 * <p>The iterators returned by the <tt>iterator</tt> method of the collections
 * returned by all of this class's "collection view methods" are
 * <i>fail-fast</i>: if the map is structurally modified at any time after the
 * iterator is created, in any way except through the iterator's own
 * <tt>remove</tt> method, the iterator will throw a {@link
 * ConcurrentModificationException}.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the future.
 *
 * <p>Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw <tt>ConcurrentModificationException</tt> on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:  <i>the fail-fast behavior of iterators
 * should be used only to detect bugs.</i>
 *
 * <p>This class is a member of the
 * <a href="{@docRoot}/../technotes/guides/collections/index.html">
 * Java Collections Framework</a>.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 *
 * @author      Doug Lea
 * @author      Josh Bloch
 * @author      Mark Reinhold
 * @since       1.2
 * @see         java.util.HashMap
 * @see         java.lang.ref.WeakReference
 */
public class KeyHolderCache<K,V>
    extends AbstractMap<K,V>
    implements Map<K,V> {

    /**
     * The default initial capacity -- MUST be a power of two.
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The table, resized as necessary. Length MUST Always be a power of two.
     */
    Entry<K,V>[] table;

    /**
     * The number of key-value mappings contained in this weak hash map.
     */
    private int size;

    /**
     * The next size value at which to resize (capacity * load factor).
     */
    private int threshold;

    /**
     * The load factor for the hash table.
     */
    private final float loadFactor;

    /**
     * Reference queue for cleared WeakEntries
     */
    private final ReferenceQueue<Object> queue = new ReferenceQueue<Object>();

    /**
     * The number of times this WeakHashMap has been structurally modified.
     * Structural modifications are those that change the number of
     * mappings in the map or otherwise modify its internal structure
     * (e.g., rehash).  This field is used to make iterators on
     * Collection-views of the map fail-fast.
     *
     * @see ConcurrentModificationException
     */
    int modCount;

    /**
     * The default threshold of map capacity above which alternative hashing is
     * used for String keys. Alternative hashing reduces the incidence of
     * collisions due to weak hash code calculation for String keys.
     * <p/>
     * This value may be overridden by defining the system property
     * {@code jdk.map.althashing.threshold}. A property value of {@code 1}
     * forces alternative hashing to be used at all times whereas
     * {@code -1} value ensures that alternative hashing is never used.
     */

  


    @SuppressWarnings("unchecked")
    private Entry<K,V>[] newTable(int n) {
        return new Entry[n];
    }

    /**
     * Constructs a new, empty <tt>WeakHashMap</tt> with the given initial
     * capacity and the given load factor.
     *
     * @param  initialCapacity The initial capacity of the <tt>WeakHashMap</tt>
     * @param  loadFactor      The load factor of the <tt>WeakHashMap</tt>
     * @throws IllegalArgumentException if the initial capacity is negative,
     *         or if the load factor is nonpositive.
     */
    public KeyHolderCache(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Initial Capacity: "+
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;

        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal Load factor: "+
                                               loadFactor);
        int capacity = 1;
        while (capacity < initialCapacity)
            capacity <<= 1;
        table = newTable(capacity);
        this.loadFactor = loadFactor;
        threshold = (int)(capacity * loadFactor);
    }

    /**
     * Constructs a new, empty <tt>WeakHashMap</tt> with the given initial
     * capacity and the default load factor (0.75).
     *
     * @param  initialCapacity The initial capacity of the <tt>WeakHashMap</tt>
     * @throws IllegalArgumentException if the initial capacity is negative
     */
    public KeyHolderCache(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs a new, empty <tt>WeakHashMap</tt> with the default initial
     * capacity (16) and load factor (0.75).
     */
    public KeyHolderCache() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    /**
     * Constructs a new <tt>WeakHashMap</tt> with the same mappings as the
     * specified map.  The <tt>WeakHashMap</tt> is created with the default
     * load factor (0.75) and an initial capacity sufficient to hold the
     * mappings in the specified map.
     *
     * @param   m the map whose mappings are to be placed in this map
     * @throws  NullPointerException if the specified map is null
     * @since   1.3
     */
    public KeyHolderCache(Map<? extends K, ? extends V> m) {
        this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1,
                DEFAULT_INITIAL_CAPACITY),
             DEFAULT_LOAD_FACTOR);
        putAll(m);
    }

    // internal utilities

    /**
     * Value representing null keys inside tables.
     */
    private static final Object NULL_KEY = new Object();

    /**
     * Use NULL_KEY for key if it is null.
     */
    private static Object maskNull(Object key) {
        return (key == null) ? NULL_KEY : key;
    }

    /**
     * Returns internal representation of null key back to caller as null.
     */
    static Object unmaskNull(Object key) {
        return (key == NULL_KEY) ? null : key;
    }

    /**
     * Checks for equality of non-null reference x and possibly-null y.  By
     * default uses Object.equals.
     */
    private static boolean eq(Object x, Object y) {
        return x == y || x.equals(y);
    }

    /**
     * Retrieve object hash code and applies a supplemental hash function to the
     * result hash, which defends against poor quality hash functions.  This is
     * critical because HashMap uses power-of-two length hash tables, that
     * otherwise encounter collisions for hashCodes that do not differ
     * in lower bits.
     */
    int hash(Object k) {

        int h;

            h = k.hashCode();

        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    /**
     * Returns index for hash code h.
     */
    private static int indexFor(int h, int length) {
        return h & (length-1);
    }

    /**
     * Expunges stale entries from the table.
     */
    private void expungeStaleEntries() {
        for (Object x; (x = queue.poll()) != null; ) {
            synchronized (queue) {
                @SuppressWarnings("unchecked")
                    Entry<K,V> e = (Entry<K,V>) x;
                int i = indexFor(e.hash, table.length);

                Entry<K,V> prev = table[i];
                Entry<K,V> p = prev;
                while (p != null) {
                    Entry<K,V> next = p.next;
                    if (p == e) {
                        if (prev == e)
                            table[i] = next;
                        else
                            prev.next = next;
                        // Must not null out e.next;
                        // stale entries may be in use by a HashIterator
                        e.value = null; // Help GC
                        size--;
                        break;
                    }
                    prev = p;
                    p = next;
                }
            }
        }
    }

    /**
     * Returns the table after first expunging stale entries.
     */
    private Entry<K,V>[] getTable() {
        expungeStaleEntries();
        return table;
    }

    /**
     * Returns the number of key-value mappings in this map.
     * This result is a snapshot, and may not reflect unprocessed
     * entries that will be removed before next attempted access
     * because they are no longer referenced.
     */
    @Override
    public int size() {
        if (size == 0)
            return 0;
        expungeStaleEntries();
        return size;
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     * This result is a snapshot, and may not reflect unprocessed
     * entries that will be removed before next attempted access
     * because they are no longer referenced.
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
     * key.equals(k))}, then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     *
     * <p>A return value of {@code null} does not <i>necessarily</i>
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@code null}.
     * The {@link #containsKey containsKey} operation may be used to
     * distinguish these two cases.
     *
     * @see #put(Object, Object)
     */
    @Override
    public V get(Object key) {
        Object k = maskNull(key);
        int h = hash(k);
        Entry<K,V>[] tab = getTable();
        int index = indexFor(h, tab.length);
        Entry<K,V> e = tab[index];
        while (e != null) {
            if (e.hash == h && eq(k, e.get()))
                return e.value.get();
            e = e.next;
        }
        return null;
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the
     * specified key.
     *
     * @param  key   The key whose presence in this map is to be tested
     * @return <tt>true</tt> if there is a mapping for <tt>key</tt>;
     *         <tt>false</tt> otherwise
     */
    @Override
    public boolean containsKey(Object key) {
        return getEntry(key) != null;
    }

    /**
     * Returns the entry associated with the specified key in this map.
     * Returns null if the map contains no mapping for this key.
     */
    Entry<K,V> getEntry(Object key) {
        Object k = maskNull(key);
        int h = hash(k);
        Entry<K,V>[] tab = getTable();
        int index = indexFor(h, tab.length);
        Entry<K,V> e = tab[index];
        while (e != null && !(e.hash == h && eq(k, e.get())))
            e = e.next;
        return e;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for this key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>.
     *         (A <tt>null</tt> return can also indicate that the map
     *         previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    @Override
    public V put(K key, V value) {
        Object k = maskNull(key);
        int h = hash(k);
        Entry<K,V>[] tab = getTable();
        int i = indexFor(h, tab.length);

        for (Entry<K,V> e = tab[i]; e != null; e = e.next) {
            if (h == e.hash && eq(k, e.get())) {
                V oldValue = e.value.get();
                if (value != oldValue)
                    e.value = new SoftReference<V>(value);
                return oldValue;
            }
        }

        modCount++;
        Entry<K,V> e = tab[i];
        tab[i] = new Entry<K,V>(k, value, queue, h, e);
        if (++size >= threshold)
            resize(tab.length * 2);
        return null;
    }

    /**
     * Rehashes the contents of this map into a new array with a
     * larger capacity.  This method is called automatically when the
     * number of keys in this map reaches its threshold.
     *
     * If current capacity is MAXIMUM_CAPACITY, this method does not
     * resize the map, but sets threshold to Integer.MAX_VALUE.
     * This has the effect of preventing future calls.
     *
     * @param newCapacity the new capacity, MUST be a power of two;
     *        must be greater than current capacity unless current
     *        capacity is MAXIMUM_CAPACITY (in which case value
     *        is irrelevant).
     */
    void resize(int newCapacity) {
        Entry<K,V>[] oldTable = getTable();
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        Entry<K,V>[] newTable = newTable(newCapacity);

        transfer(oldTable, newTable, false);
        table = newTable;

        /*
         * If ignoring null elements and processing ref queue caused massive
         * shrinkage, then restore old table.  This should be rare, but avoids
         * unbounded expansion of garbage-filled tables.
         */
        if (size >= threshold / 2) {
            threshold = (int)(newCapacity * loadFactor);
        } else {
            expungeStaleEntries();
            transfer(newTable, oldTable, false);
            table = oldTable;
        }
    }

    /** Transfers all entries from src to dest tables */
    private void transfer(Entry<K,V>[] src, Entry<K,V>[] dest, boolean rehash) {
        for (int j = 0; j < src.length; ++j) {
            Entry<K,V> e = src[j];
            src[j] = null;
            while (e != null) {
                Entry<K,V> next = e.next;
                Object key = e.get();
                if (key == null) {
                    e.next = null;  // Help GC
                    e.value = null; //  "   "
                    size--;
                } else {
                    if (rehash) {
                        e.hash = hash(key);
                    }
                    int i = indexFor(e.hash, dest.length);
                    e.next = dest[i];
                    dest[i] = e;
                }
                e = next;
            }
        }
    }

    /**
     * Copies all of the mappings from the specified map to this map.
     * These mappings will replace any mappings that this map had for any
     * of the keys currently in the specified map.
     *
     * @param m mappings to be stored in this map.
     * @throws  NullPointerException if the specified map is null.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        int numKeysToBeAdded = m.size();
        if (numKeysToBeAdded == 0)
            return;

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
            int targetCapacity = (int)(numKeysToBeAdded / loadFactor + 1);
            if (targetCapacity > MAXIMUM_CAPACITY)
                targetCapacity = MAXIMUM_CAPACITY;
            int newCapacity = table.length;
            while (newCapacity < targetCapacity)
                newCapacity <<= 1;
            if (newCapacity > table.length)
                resize(newCapacity);
        }

        for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
            put(e.getKey(), e.getValue());
    }

    /**
     * Removes the mapping for a key from this weak hash map if it is present.
     * More formally, if this map contains a mapping from key <tt>k</tt> to
     * value <tt>v</tt> such that <code>(key==null ?  k==null :
     * key.equals(k))</code>, that mapping is removed.  (The map can contain
     * at most one such mapping.)
     *
     * <p>Returns the value to which this map previously associated the key,
     * or <tt>null</tt> if the map contained no mapping for the key.  A
     * return value of <tt>null</tt> does not <i>necessarily</i> indicate
     * that the map contained no mapping for the key; it's also possible
     * that the map explicitly mapped the key to <tt>null</tt>.
     *
     * <p>The map will not contain a mapping for the specified key once the
     * call returns.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with <tt>key</tt>, or
     *         <tt>null</tt> if there was no mapping for <tt>key</tt>
     */
    @Override
    public V remove(Object key) {
        Object k = maskNull(key);
        int h = hash(k);
        Entry<K,V>[] tab = getTable();
        int i = indexFor(h, tab.length);
        Entry<K,V> prev = tab[i];
        Entry<K,V> e = prev;

        while (e != null) {
            Entry<K,V> next = e.next;
            if (h == e.hash && eq(k, e.get())) {
                modCount++;
                size--;
                if (prev == e)
                    tab[i] = next;
                else
                    prev.next = next;
                return e.value.get();
            }
            prev = e;
            e = next;
        }

        return null;
    }

    /** Special version of remove needed by Entry set */
    boolean removeMapping(Object o) {
        if (!(o instanceof Map.Entry))
            return false;
        Entry<K,V>[] tab = getTable();
        Map.Entry<?,?> entry = (Map.Entry<?,?>)o;
        Object k = maskNull(entry.getKey());
        int h = hash(k);
        int i = indexFor(h, tab.length);
        Entry<K,V> prev = tab[i];
        Entry<K,V> e = prev;

        while (e != null) {
            Entry<K,V> next = e.next;
            if (h == e.hash && e.equals(entry)) {
                modCount++;
                size--;
                if (prev == e)
                    tab[i] = next;
                else
                    prev.next = next;
                return true;
            }
            prev = e;
            e = next;
        }

        return false;
    }

    /**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     */
    @Override
    public void clear() {
        // clear out ref queue. We don't need to expunge entries
        // since table is getting cleared.
        while (queue.poll() != null)
            ;

        modCount++;
        Arrays.fill(table, null);
        size = 0;

        // Allocation of array may have caused GC, which may have caused
        // additional entries to go stale.  Removing these entries from the
        // reference queue will make them eligible for reclamation.
        while (queue.poll() != null)
            ;
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.
     *
     * @param value value whose presence in this map is to be tested
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value
     */
    @Override
    public boolean containsValue(Object value) {
        if (value==null)
            return containsNullValue();

        Entry<K,V>[] tab = getTable();
        for (int i = tab.length; i-- > 0;)
            for (Entry<K,V> e = tab[i]; e != null; e = e.next)
                if (value.equals(e.value))
                    return true;
        return false;
    }

    /**
     * Special-case code for containsValue with null argument
     */
    private boolean containsNullValue() {
        Entry<K,V>[] tab = getTable();
        for (int i = tab.length; i-- > 0;)
            for (Entry<K,V> e = tab[i]; e != null; e = e.next)
                if (e.value==null)
                    return true;
        return false;
    }

    /**
     * The entries in this hash table extend WeakReference, using its main ref
     * field as the key.
     */
    private static class Entry<K,V> extends SoftReference<Object> implements Map.Entry<K,V> {
        SoftReference<V> value;
        int hash;
        Entry<K,V> next;

        /**
         * Creates new entry.
         */
        Entry(Object key, V value,
              ReferenceQueue<Object> queue,
              int hash, Entry<K,V> next) {
            super(key, queue);
            this.value = new SoftReference<V>(value);
            this.hash  = hash;
            this.next  = next;
        }

        @Override
        @SuppressWarnings("unchecked")
        public K getKey() {
            return (K) KeyHolderCache.unmaskNull(get());
        }

        @Override
        public V getValue() {
            return value.get();
        }

        @Override
        public V setValue(V newValue) {
            V oldValue = value.get();
            value = new SoftReference<V>(newValue);
            return oldValue;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> e = (Map.Entry<?,?>)o;
            K k1 = getKey();
            Object k2 = e.getKey();
            if (k1 == k2 || (k1 != null && k1.equals(k2))) {
                V v1 = getValue();
                Object v2 = e.getValue();
                if (v1 == v2 || (v1 != null && v1.equals(v2)))
                    return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            K k = getKey();
            V v = getValue();
            return ((k==null ? 0 : k.hashCode()) ^
                    (v==null ? 0 : v.hashCode()));
        }

        @Override
        public String toString() {
            return getKey() + "=" + getValue();
        }
    }

    private abstract class HashIterator<T> implements Iterator<T> {
        private int index;
        private Entry<K,V> entry = null;
        private Entry<K,V> lastReturned = null;
        private int expectedModCount = modCount;

        /**
         * Strong reference needed to avoid disappearance of key
         * between hasNext and next
         */
        private Object nextKey = null;

        /**
         * Strong reference needed to avoid disappearance of key
         * between nextEntry() and any use of the entry
         */
        private Object currentKey = null;

        HashIterator() {
            index = isEmpty() ? 0 : table.length;
        }

        @Override
        public boolean hasNext() {
            Entry<K,V>[] t = table;

            while (nextKey == null) {
                Entry<K,V> e = entry;
                int i = index;
                while (e == null && i > 0)
                    e = t[--i];
                entry = e;
                index = i;
                if (e == null) {
                    currentKey = null;
                    return false;
                }
                nextKey = e.get(); // hold on to key in strong ref
                if (nextKey == null)
                    entry = entry.next;
            }
            return true;
        }

        /** The common parts of next() across different types of iterators */
        protected Entry<K,V> nextEntry() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            if (nextKey == null && !hasNext())
                throw new NoSuchElementException();

            lastReturned = entry;
            entry = entry.next;
            currentKey = nextKey;
            nextKey = null;
            return lastReturned;
        }

        @Override
        public void remove() {
            if (lastReturned == null)
                throw new IllegalStateException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();

            KeyHolderCache.this.remove(currentKey);
            expectedModCount = modCount;
            lastReturned = null;
            currentKey = null;
        }

    }

    private class ValueIterator extends HashIterator<V> {
        @Override
        public V next() {
            return nextEntry().value.get();
        }
    }

    private class KeyIterator extends HashIterator<K> {
        @Override
        public K next() {
            return nextEntry().getKey();
        }
    }

    private class EntryIterator extends HashIterator<Map.Entry<K,V>> {
        @Override
        public Map.Entry<K,V> next() {
            return nextEntry();
        }
    }

    // Views

    private transient Set<Map.Entry<K,V>> entrySet = null;

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation), the results of
     * the iteration are undefined.  The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * <tt>Iterator.remove</tt>, <tt>Set.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt>, and <tt>clear</tt>
     * operations.  It does not support the <tt>add</tt> or <tt>addAll</tt>
     * operations.
     */
    @Override
    public Set<K> keySet() {
        Set<K> ks = keySet;
        return (ks != null ? ks : (keySet = new KeySet()));
    }

    private class KeySet extends AbstractSet<K> {
        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public int size() {
            return KeyHolderCache.this.size();
        }

        @Override
        public boolean contains(Object o) {
            return containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            if (containsKey(o)) {
             KeyHolderCache.this.remove(o);
                return true;
            }
            else
                return false;
        }

        @Override
        public void clear() {
         KeyHolderCache.this.clear();
        }
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterator's own <tt>remove</tt> operation),
     * the results of the iteration are undefined.  The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Collection.remove</tt>, <tt>removeAll</tt>,
     * <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not
     * support the <tt>add</tt> or <tt>addAll</tt> operations.
     */
    @Override
    public Collection<V> values() {
        Collection<V> vs = values;
        return (vs != null) ? vs : (values = new Values());
    }

    private class Values extends AbstractCollection<V> {
        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override
        public int size() {
            return KeyHolderCache.this.size();
        }

        @Override
        public boolean contains(Object o) {
            return containsValue(o);
        }

        @Override
        public void clear() {
         KeyHolderCache.this.clear();
        }
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterator's own <tt>remove</tt> operation, or through the
     * <tt>setValue</tt> operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and
     * <tt>clear</tt> operations.  It does not support the
     * <tt>add</tt> or <tt>addAll</tt> operations.
     */
    @Override
    public Set<Map.Entry<K,V>> entrySet() {
        Set<Map.Entry<K,V>> es = entrySet;
        return es != null ? es : (entrySet = new EntrySet());
    }

    private class EntrySet extends AbstractSet<Map.Entry<K,V>> {
        @Override
        public Iterator<Map.Entry<K,V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry<?,?> e = (Map.Entry<?,?>)o;
            Entry<K,V> candidate = getEntry(e.getKey());
            return candidate != null && candidate.equals(e);
        }

        @Override
        public boolean remove(Object o) {
            return removeMapping(o);
        }

        @Override
        public int size() {
            return KeyHolderCache.this.size();
        }

        @Override
        public void clear() {
         KeyHolderCache.this.clear();
        }

        private List<Map.Entry<K,V>> deepCopy() {
            List<Map.Entry<K,V>> list = new ArrayList<Map.Entry<K,V>>(size());
            for (Map.Entry<K,V> e : this)
                list.add(new AbstractMap.SimpleEntry<K,V>(e));
            return list;
        }

        @Override
        public Object[] toArray() {
            return deepCopy().toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return deepCopy().toArray(a);
        }
    }
}


abstract class AbstractMap<K,V> implements Map<K,V> {
    /**
     * Sole constructor.  (For invocation by subclass constructors, typically
     * implicit.)
     */
    protected AbstractMap() {
    }

    // Query Operations

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns <tt>entrySet().size()</tt>.
     */
    @Override
    public int size() {
        return entrySet().size();
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns <tt>size() == 0</tt>.
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over <tt>entrySet()</tt> searching
     * for an entry with the specified value.  If such an entry is found,
     * <tt>true</tt> is returned.  If the iteration terminates without
     * finding such an entry, <tt>false</tt> is returned.  Note that this
     * implementation requires linear time in the size of the map.
     *
     * @throws ClassCastException   {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public boolean containsValue(Object value) {
        Iterator<Entry<K,V>> i = entrySet().iterator();
        if (value==null) {
            while (i.hasNext()) {
                Entry<K,V> e = i.next();
                if (e.getValue()==null)
                    return true;
            }
        } else {
            while (i.hasNext()) {
                Entry<K,V> e = i.next();
                if (value.equals(e.getValue()))
                    return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over <tt>entrySet()</tt> searching
     * for an entry with the specified key.  If such an entry is found,
     * <tt>true</tt> is returned.  If the iteration terminates without
     * finding such an entry, <tt>false</tt> is returned.  Note that this
     * implementation requires linear time in the size of the map; many
     * implementations will override this method.
     *
     * @throws ClassCastException   {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public boolean containsKey(Object key) {
        Iterator<Map.Entry<K,V>> i = entrySet().iterator();
        if (key==null) {
            while (i.hasNext()) {
                Entry<K,V> e = i.next();
                if (e.getKey()==null)
                    return true;
            }
        } else {
            while (i.hasNext()) {
                Entry<K,V> e = i.next();
                if (key.equals(e.getKey()))
                    return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over <tt>entrySet()</tt> searching
     * for an entry with the specified key.  If such an entry is found,
     * the entry's value is returned.  If the iteration terminates without
     * finding such an entry, <tt>null</tt> is returned.  Note that this
     * implementation requires linear time in the size of the map; many
     * implementations will override this method.
     *
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     */
    @Override
    public V get(Object key) {
        Iterator<Entry<K,V>> i = entrySet().iterator();
        if (key==null) {
            while (i.hasNext()) {
                Entry<K,V> e = i.next();
                if (e.getKey()==null)
                    return e.getValue();
            }
        } else {
            while (i.hasNext()) {
                Entry<K,V> e = i.next();
                if (key.equals(e.getKey()))
                    return e.getValue();
            }
        }
        return null;
    }


    // Modification Operations

    /**
     * {@inheritDoc}
     *
     * <p>This implementation always throws an
     * <tt>UnsupportedOperationException</tt>.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     */
    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over <tt>entrySet()</tt> searching for an
     * entry with the specified key.  If such an entry is found, its value is
     * obtained with its <tt>getValue</tt> operation, the entry is removed
     * from the collection (and the backing map) with the iterator's
     * <tt>remove</tt> operation, and the saved value is returned.  If the
     * iteration terminates without finding such an entry, <tt>null</tt> is
     * returned.  Note that this implementation requires linear time in the
     * size of the map; many implementations will override this method.
     *
     * <p>Note that this implementation throws an
     * <tt>UnsupportedOperationException</tt> if the <tt>entrySet</tt>
     * iterator does not support the <tt>remove</tt> method and this map
     * contains a mapping for the specified key.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     */
    @Override
    public V remove(Object key) {
        Iterator<Entry<K,V>> i = entrySet().iterator();
        Entry<K,V> correctEntry = null;
        if (key==null) {
            while (correctEntry==null && i.hasNext()) {
                Entry<K,V> e = i.next();
                if (e.getKey()==null)
                    correctEntry = e;
            }
        } else {
            while (correctEntry==null && i.hasNext()) {
                Entry<K,V> e = i.next();
                if (key.equals(e.getKey()))
                    correctEntry = e;
            }
        }

        V oldValue = null;
        if (correctEntry !=null) {
            oldValue = correctEntry.getValue();
            i.remove();
        }
        return oldValue;
    }


    // Bulk Operations

    /**
     * {@inheritDoc}
     *
     * <p>This implementation iterates over the specified map's
     * <tt>entrySet()</tt> collection, and calls this map's <tt>put</tt>
     * operation once for each entry returned by the iteration.
     *
     * <p>Note that this implementation throws an
     * <tt>UnsupportedOperationException</tt> if this map does not support
     * the <tt>put</tt> operation and the specified map is nonempty.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
            put(e.getKey(), e.getValue());
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation calls <tt>entrySet().clear()</tt>.
     *
     * <p>Note that this implementation throws an
     * <tt>UnsupportedOperationException</tt> if the <tt>entrySet</tt>
     * does not support the <tt>clear</tt> operation.
     *
     * @throws UnsupportedOperationException {@inheritDoc}
     */
    @Override
    public void clear() {
        entrySet().clear();
    }


    // Views

    /**
     * Each of these fields are initialized to contain an instance of the
     * appropriate view the first time this view is requested.  The views are
     * stateless, so there's no reason to create more than one of each.
     */
    transient volatile Set<K>        keySet = null;
    transient volatile Collection<V> values = null;

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns a set that subclasses {@link AbstractSet}.
     * The subclass's iterator method returns a "wrapper object" over this
     * map's <tt>entrySet()</tt> iterator.  The <tt>size</tt> method
     * delegates to this map's <tt>size</tt> method and the
     * <tt>contains</tt> method delegates to this map's
     * <tt>containsKey</tt> method.
     *
     * <p>The set is created the first time this method is called,
     * and returned in response to all subsequent calls.  No synchronization
     * is performed, so there is a slight chance that multiple calls to this
     * method will not all return the same set.
     */
    @Override
    public Set<K> keySet() {
        if (keySet == null) {
            keySet = new AbstractSet<K>() {
                @Override
                public Iterator<K> iterator() {
                    return new Iterator<K>() {
                        private final Iterator<Entry<K,V>> i = entrySet().iterator();

                        @Override
                        public boolean hasNext() {
                            return i.hasNext();
                        }

                        @Override
                        public K next() {
                            return i.next().getKey();
                        }

                        @Override
                        public void remove() {
                            i.remove();
                        }
                    };
                }

                @Override
                public int size() {
                    return AbstractMap.this.size();
                }

                @Override
                public boolean isEmpty() {
                    return AbstractMap.this.isEmpty();
                }

                @Override
                public void clear() {
                    AbstractMap.this.clear();
                }

                @Override
                public boolean contains(Object k) {
                    return AbstractMap.this.containsKey(k);
                }
            };
        }
        return keySet;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns a collection that subclasses {@link
     * AbstractCollection}.  The subclass's iterator method returns a
     * "wrapper object" over this map's <tt>entrySet()</tt> iterator.
     * The <tt>size</tt> method delegates to this map's <tt>size</tt>
     * method and the <tt>contains</tt> method delegates to this map's
     * <tt>containsValue</tt> method.
     *
     * <p>The collection is created the first time this method is called, and
     * returned in response to all subsequent calls.  No synchronization is
     * performed, so there is a slight chance that multiple calls to this
     * method will not all return the same collection.
     */
    @Override
    public Collection<V> values() {
        if (values == null) {
            values = new AbstractCollection<V>() {
                @Override
                public Iterator<V> iterator() {
                    return new Iterator<V>() {
                        private final Iterator<Entry<K,V>> i = entrySet().iterator();

                        @Override
                        public boolean hasNext() {
                            return i.hasNext();
                        }

                        @Override
                        public V next() {
                            return i.next().getValue();
                        }

                        @Override
                        public void remove() {
                            i.remove();
                        }
                    };
                }

                @Override
                public int size() {
                    return AbstractMap.this.size();
                }

                @Override
                public boolean isEmpty() {
                    return AbstractMap.this.isEmpty();
                }

                @Override
                public void clear() {
                    AbstractMap.this.clear();
                }

                @Override
                public boolean contains(Object v) {
                    return AbstractMap.this.containsValue(v);
                }
            };
        }
        return values;
    }

    @Override
    public abstract Set<Entry<K,V>> entrySet();


    // Comparison and hashing

    /**
     * Compares the specified object with this map for equality.  Returns
     * <tt>true</tt> if the given object is also a map and the two maps
     * represent the same mappings.  More formally, two maps <tt>m1</tt> and
     * <tt>m2</tt> represent the same mappings if
     * <tt>m1.entrySet().equals(m2.entrySet())</tt>.  This ensures that the
     * <tt>equals</tt> method works properly across different implementations
     * of the <tt>Map</tt> interface.
     *
     * <p>This implementation first checks if the specified object is this map;
     * if so it returns <tt>true</tt>.  Then, it checks if the specified
     * object is a map whose size is identical to the size of this map; if
     * not, it returns <tt>false</tt>.  If so, it iterates over this map's
     * <tt>entrySet</tt> collection, and checks that the specified map
     * contains each mapping that this map contains.  If the specified map
     * fails to contain such a mapping, <tt>false</tt> is returned.  If the
     * iteration completes, <tt>true</tt> is returned.
     *
     * @param o object to be compared for equality with this map
     * @return <tt>true</tt> if the specified object is equal to this map
     */
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof Map))
            return false;
        Map<K,V> m = (Map<K,V>) o;
        if (m.size() != size())
            return false;

        try {
            Iterator<Entry<K,V>> i = entrySet().iterator();
            while (i.hasNext()) {
                Entry<K,V> e = i.next();
                K key = e.getKey();
                V value = e.getValue();
                if (value == null) {
                    if (!(m.get(key)==null && m.containsKey(key)))
                        return false;
                } else {
                    if (!value.equals(m.get(key)))
                        return false;
                }
            }
        } catch (ClassCastException unused) {
            return false;
        } catch (NullPointerException unused) {
            return false;
        }

        return true;
    }

    /**
     * Returns the hash code value for this map.  The hash code of a map is
     * defined to be the sum of the hash codes of each entry in the map's
     * <tt>entrySet()</tt> view.  This ensures that <tt>m1.equals(m2)</tt>
     * implies that <tt>m1.hashCode()==m2.hashCode()</tt> for any two maps
     * <tt>m1</tt> and <tt>m2</tt>, as required by the general contract of
     * {@link Object#hashCode}.
     *
     * <p>This implementation iterates over <tt>entrySet()</tt>, calling
     * {@link Map.Entry#hashCode hashCode()} on each element (entry) in the
     * set, and adding up the results.
     *
     * @return the hash code value for this map
     * @see Map.Entry#hashCode()
     * @see Object#equals(Object)
     * @see Set#equals(Object)
     */
    @Override
    public int hashCode() {
        int h = 0;
        Iterator<Entry<K,V>> i = entrySet().iterator();
        while (i.hasNext())
            h += i.next().hashCode();
        return h;
    }

    /**
     * Returns a string representation of this map.  The string representation
     * consists of a list of key-value mappings in the order returned by the
     * map's <tt>entrySet</tt> view's iterator, enclosed in braces
     * (<tt>"{}"</tt>).  Adjacent mappings are separated by the characters
     * <tt>", "</tt> (comma and space).  Each key-value mapping is rendered as
     * the key followed by an equals sign (<tt>"="</tt>) followed by the
     * associated value.  Keys and values are converted to strings as by
     * {@link String#valueOf(Object)}.
     *
     * @return a string representation of this map
     */
    @Override
    public String toString() {
        Iterator<Entry<K,V>> i = entrySet().iterator();
        if (! i.hasNext())
            return "{}";

        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (;;) {
            Entry<K,V> e = i.next();
            K key = e.getKey();
            V value = e.getValue();
            sb.append(key   == this ? "(this Map)" : key);
            sb.append('=');
            sb.append(value == this ? "(this Map)" : value);
            if (! i.hasNext())
                return sb.append('}').toString();
            sb.append(',').append(' ');
        }
    }

    /**
     * Returns a shallow copy of this <tt>AbstractMap</tt> instance: the keys
     * and values themselves are not cloned.
     *
     * @return a shallow copy of this map
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        AbstractMap<K,V> result = (AbstractMap<K,V>)super.clone();
        result.keySet = null;
        result.values = null;
        return result;
    }

    /**
     * Utility method for SimpleEntry and SimpleImmutableEntry.
     * Test for equality, checking for nulls.
     */
    private static boolean eq(Object o1, Object o2) {
        return o1 == null ? o2 == null : o1.equals(o2);
    }

    // Implementation Note: SimpleEntry and SimpleImmutableEntry
    // are distinct unrelated classes, even though they share
    // some code. Since you can't add or subtract final-ness
    // of a field in a subclass, they can't share representations,
    // and the amount of duplicated code is too small to warrant
    // exposing a common abstract class.


    /**
     * An Entry maintaining a key and a value.  The value may be
     * changed using the <tt>setValue</tt> method.  This class
     * facilitates the process of building custom map
     * implementations. For example, it may be convenient to return
     * arrays of <tt>SimpleEntry</tt> instances in method
     * <tt>Map.entrySet().toArray</tt>.
     *
     * @since 1.6
     */
    public static class SimpleEntry<K,V>
        implements Entry<K,V>, java.io.Serializable
    {
        private static final long serialVersionUID = -8499721149061103585L;

        private final K key;
        private V value;

        /**
         * Creates an entry representing a mapping from the specified
         * key to the specified value.
         *
         * @param key the key represented by this entry
         * @param value the value represented by this entry
         */
        public SimpleEntry(K key, V value) {
            this.key   = key;
            this.value = value;
        }

        /**
         * Creates an entry representing the same mapping as the
         * specified entry.
         *
         * @param entry the entry to copy
         */
        public SimpleEntry(Entry<? extends K, ? extends V> entry) {
            this.key   = entry.getKey();
            this.value = entry.getValue();
        }

        /**
         * Returns the key corresponding to this entry.
         *
         * @return the key corresponding to this entry
         */
        @Override
        public K getKey() {
            return key;
        }

        /**
         * Returns the value corresponding to this entry.
         *
         * @return the value corresponding to this entry
         */
        @Override
        public V getValue() {
            return value;
        }

        /**
         * Replaces the value corresponding to this entry with the specified
         * value.
         *
         * @param value new value to be stored in this entry
         * @return the old value corresponding to the entry
         */
        @Override
        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        /**
         * Compares the specified object with this entry for equality.
         * Returns {@code true} if the given object is also a map entry and
         * the two entries represent the same mapping.  More formally, two
         * entries {@code e1} and {@code e2} represent the same mapping
         * if<pre>
         *   (e1.getKey()==null ?
         *    e2.getKey()==null :
         *    e1.getKey().equals(e2.getKey()))
         *   &amp;&amp;
         *   (e1.getValue()==null ?
         *    e2.getValue()==null :
         *    e1.getValue().equals(e2.getValue()))</pre>
         * This ensures that the {@code equals} method works properly across
         * different implementations of the {@code Map.Entry} interface.
         *
         * @param o object to be compared for equality with this map entry
         * @return {@code true} if the specified object is equal to this map
         *         entry
         * @see    #hashCode
         */
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry e = (Map.Entry)o;
            return eq(key, e.getKey()) && eq(value, e.getValue());
        }

        /**
         * Returns the hash code value for this map entry.  The hash code
         * of a map entry {@code e} is defined to be: <pre>
         *   (e.getKey()==null   ? 0 : e.getKey().hashCode()) ^
         *   (e.getValue()==null ? 0 : e.getValue().hashCode())</pre>
         * This ensures that {@code e1.equals(e2)} implies that
         * {@code e1.hashCode()==e2.hashCode()} for any two Entries
         * {@code e1} and {@code e2}, as required by the general
         * contract of {@link Object#hashCode}.
         *
         * @return the hash code value for this map entry
         * @see    #equals
         */
        @Override
        public int hashCode() {
            return (key   == null ? 0 :   key.hashCode()) ^
                   (value == null ? 0 : value.hashCode());
        }

        /**
         * Returns a String representation of this map entry.  This
         * implementation returns the string representation of this
         * entry's key followed by the equals character ("<tt>=</tt>")
         * followed by the string representation of this entry's value.
         *
         * @return a String representation of this map entry
         */
        @Override
        public String toString() {
            return key + "=" + value;
        }

    }

    /**
     * An Entry maintaining an immutable key and value.  This class
     * does not support method <tt>setValue</tt>.  This class may be
     * convenient in methods that return thread-safe snapshots of
     * key-value mappings.
     *
     * @since 1.6
     */
    public static class SimpleImmutableEntry<K,V>
        implements Entry<K,V>, java.io.Serializable
    {
        private static final long serialVersionUID = 7138329143949025153L;

        private final K key;
        private final V value;

        /**
         * Creates an entry representing a mapping from the specified
         * key to the specified value.
         *
         * @param key the key represented by this entry
         * @param value the value represented by this entry
         */
        public SimpleImmutableEntry(K key, V value) {
            this.key   = key;
            this.value = value;
        }

        /**
         * Creates an entry representing the same mapping as the
         * specified entry.
         *
         * @param entry the entry to copy
         */
        public SimpleImmutableEntry(Entry<? extends K, ? extends V> entry) {
            this.key   = entry.getKey();
            this.value = entry.getValue();
        }

        /**
         * Returns the key corresponding to this entry.
         *
         * @return the key corresponding to this entry
         */
        @Override
        public K getKey() {
            return key;
        }

        /**
         * Returns the value corresponding to this entry.
         *
         * @return the value corresponding to this entry
         */
        @Override
        public V getValue() {
            return value;
        }

        /**
         * Replaces the value corresponding to this entry with the specified
         * value (optional operation).  This implementation simply throws
         * <tt>UnsupportedOperationException</tt>, as this class implements
         * an <i>immutable</i> map entry.
         *
         * @param value new value to be stored in this entry
         * @return (Does not return)
         * @throws UnsupportedOperationException always
         */
        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        /**
         * Compares the specified object with this entry for equality.
         * Returns {@code true} if the given object is also a map entry and
         * the two entries represent the same mapping.  More formally, two
         * entries {@code e1} and {@code e2} represent the same mapping
         * if<pre>
         *   (e1.getKey()==null ?
         *    e2.getKey()==null :
         *    e1.getKey().equals(e2.getKey()))
         *   &amp;&amp;
         *   (e1.getValue()==null ?
         *    e2.getValue()==null :
         *    e1.getValue().equals(e2.getValue()))</pre>
         * This ensures that the {@code equals} method works properly across
         * different implementations of the {@code Map.Entry} interface.
         *
         * @param o object to be compared for equality with this map entry
         * @return {@code true} if the specified object is equal to this map
         *         entry
         * @see    #hashCode
         */
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry))
                return false;
            Map.Entry e = (Map.Entry)o;
            return eq(key, e.getKey()) && eq(value, e.getValue());
        }

        /**
         * Returns the hash code value for this map entry.  The hash code
         * of a map entry {@code e} is defined to be: <pre>
         *   (e.getKey()==null   ? 0 : e.getKey().hashCode()) ^
         *   (e.getValue()==null ? 0 : e.getValue().hashCode())</pre>
         * This ensures that {@code e1.equals(e2)} implies that
         * {@code e1.hashCode()==e2.hashCode()} for any two Entries
         * {@code e1} and {@code e2}, as required by the general
         * contract of {@link Object#hashCode}.
         *
         * @return the hash code value for this map entry
         * @see    #equals
         */
        @Override
        public int hashCode() {
            return (key   == null ? 0 :   key.hashCode()) ^
                   (value == null ? 0 : value.hashCode());
        }

        /**
         * Returns a String representation of this map entry.  This
         * implementation returns the string representation of this
         * entry's key followed by the equals character ("<tt>=</tt>")
         * followed by the string representation of this entry's value.
         *
         * @return a String representation of this map entry
         */
        @Override
        public String toString() {
            return key + "=" + value;
        }

    }

}

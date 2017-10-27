package com.pri.util.collection;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class LongAbstractMap<V> implements LongMap<V> {

    /**
     * Sole constructor.  (For invocation by subclass constructors, typically implicit.)
     */
    protected LongAbstractMap() {
    }

    // Query Operations

    /**
     * Returns the number of key-value mappings in this map.  If the map contains more than <tt>Integer.MAX_VALUE</tt>
     * elements, returns <tt>Integer.MAX_VALUE</tt>.<p>
     *
     * This implementation returns <tt>entrySet().size()</tt>.
     *
     * @return the number of key-value mappings in this map.
     */
    public int size() {
        return entrySet().size();
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings. <p>
     *
     * This implementation returns <tt>size() == 0</tt>.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings.
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns <tt>true</tt> if this map maps one or more keys to this value. More formally, returns <tt>true</tt> if
     * and only if this map contains at least one mapping to a value <tt>v</tt> such that <tt>(value==null ? v==null :
     * value.equals(v))</tt>.  This operation will probably require time linear in the map size for most implementations
     * of map.<p>
     *
     * This implementation iterates over entrySet() searching for an entry with the specified value.  If such an entry
     * is found, <tt>true</tt> is returned.  If the iteration terminates without finding such an entry, <tt>false</tt>
     * is returned.  Note that this implementation requires linear time in the size of the map.
     *
     * @param value value whose presence in this map is to be tested.
     * @return <tt>true</tt> if this map maps one or more keys to this value.
     */
    public boolean containsValue(Object value) {
        Iterator<Entry<V>> i = entrySet().iterator();
        if (value == null) {
            while (i.hasNext()) {
                Entry<V> e = i.next();
                if (e.getValue() == null) {
                    return true;
                }
            }
        } else {
            while (i.hasNext()) {
                Entry<V> e = i.next();
                if (value.equals(e.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified key. <p>
     *
     * This implementation iterates over <tt>entrySet()</tt> searching for an entry with the specified key.  If such an
     * entry is found, <tt>true</tt> is returned.  If the iteration terminates without finding such an entry,
     * <tt>false</tt> is returned.  Note that this implementation requires linear time in the size of the map; many
     * implementations will override this method.
     *
     * @param key key whose presence in this map is to be tested.
     * @return <tt>true</tt> if this map contains a mapping for the specified key.
     * @throws NullPointerException if the key is <tt>null</tt> and this map does not permit <tt>null</tt> keys.
     */
    public boolean containsKey(long key) {
        Iterator<Entry<V>> i = entrySet().iterator();

        while (i.hasNext()) {
            Entry<V> e = i.next();
            if (key == e.getKey()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the value to which this map maps the specified key.  Returns <tt>null</tt> if the map contains no mapping
     * for this key.  A return value of <tt>null</tt> does not <i>necessarily</i> indicate that the map contains no
     * mapping for the key; it's also possible that the map explicitly maps the key to <tt>null</tt>.  The containsKey
     * operation may be used to distinguish these two cases. <p>
     *
     * This implementation iterates over <tt>entrySet()</tt> searching for an entry with the specified key.  If such an
     * entry is found, the entry's value is returned.  If the iteration terminates without finding such an entry,
     * <tt>null</tt> is returned.  Note that this implementation requires linear time in the size of the map; many
     * implementations will override this method.
     *
     * @param key key whose associated value is to be returned.
     * @return the value to which this map maps the specified key.
     * @throws NullPointerException if the key is <tt>null</tt> and this map does not permit <tt>null</tt> keys.
     * @see #containsKey(Object)
     */
    public V get(long key) {
        Iterator<Entry<V>> i = entrySet().iterator();

        while (i.hasNext()) {
            Entry<V> e = i.next();
            if (key == e.getKey()) {
                return e.getValue();
            }
        }
        return null;
    }

    // Modification Operations

    /**
     * Associates the specified value with the specified key in this map (optional operation).  If the map previously
     * contained a mapping for this key, the old value is replaced.<p>
     *
     * This implementation always throws an <tt>UnsupportedOperationException</tt>.
     *
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return previous value associated with specified key, or <tt>null</tt> if there was no mapping for key.  (A
     * <tt>null</tt> return can also indicate that the map previously associated <tt>null</tt> with the specified key,
     * if the implementation supports <tt>null</tt> values.)
     * @throws UnsupportedOperationException if the <tt>put</tt> operation is not supported by this map.
     * @throws ClassCastException if the class of the specified key or value prevents it from being stored in this map.
     * @throws IllegalArgumentException if some aspect of this key or value * prevents it from being stored in this
     * map.
     * @throws NullPointerException if this map does not permit <tt>null</tt> keys or values, and the specified key or
     * value is <tt>null</tt>.
     */
    @SuppressWarnings("unused")
    public V put(long key, V value) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the mapping for this key from this map if present (optional operation). <p>
     *
     * This implementation iterates over <tt>entrySet()</tt> searching for an entry with the specified key.  If such an
     * entry is found, its value is obtained with its <tt>getValue</tt> operation, the entry is removed from the
     * Collection (and the backing map) with the iterator's <tt>remove</tt> operation, and the saved value is returned.
     * If the iteration terminates without finding such an entry, <tt>null</tt> is returned.  Note that this
     * implementation requires linear time in the size of the map; many implementations will override this method.<p>
     *
     * Note that this implementation throws an <tt>UnsupportedOperationException</tt> if the <tt>entrySet</tt> iterator
     * does not support the <tt>remove</tt> method and this map contains a mapping for the specified key.
     *
     * @param key key whose mapping is to be removed from the map.
     * @return previous value associated with specified key, or <tt>null</tt> if there was no entry for key.  (A
     * <tt>null</tt> return can also indicate that the map previously associated <tt>null</tt> with the specified key,
     * if the implementation supports <tt>null</tt> values.)
     * @throws UnsupportedOperationException if the <tt>remove</tt> operation is not supported by this map.
     */
    public V remove(long key) {
        Iterator<Entry<V>> i = entrySet().iterator();
        Entry<V> correctEntry = null;
        while (correctEntry == null && i.hasNext()) {
            Entry<V> e = i.next();
            if (key == e.getKey()) {
                correctEntry = e;
            }
        }

        V oldValue = null;
        if (correctEntry != null) {
            oldValue = correctEntry.getValue();
            i.remove();
        }
        return oldValue;
    }

    // Bulk Operations

    /**
     * Copies all of the mappings from the specified map to this map (optional operation).  These mappings will replace
     * any mappings that this map had for any of the keys currently in the specified map.<p>
     *
     * This implementation iterates over the specified map's <tt>entrySet()</tt> collection, and calls this map's
     * <tt>put</tt> operation once for each entry returned by the iteration.<p>
     *
     * Note that this implementation throws an <tt>UnsupportedOperationException</tt> if this map does not support the
     * <tt>put</tt> operation and the specified map is nonempty.
     *
     * @param t mappings to be stored in this map.
     * @throws UnsupportedOperationException if the <tt>putAll</tt> operation is not supported by this map.
     * @throws ClassCastException if the class of a key or value in the specified map prevents it from being stored in
     * this map.
     * @throws IllegalArgumentException if some aspect of a key or value in the specified map prevents it from being
     * stored in this map.
     * @throws NullPointerException if the specified map is <tt>null</tt>, or if this map does not permit <tt>null</tt>
     * keys or values, and the specified map contains <tt>null</tt> keys or values.
     */
    public void putAll(Map<Long, ? extends V> t) {
        Iterator<? extends Map.Entry<Long, ? extends V>> i = t.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<Long, ? extends V> e = i.next();
            put(e.getKey().intValue(), e.getValue());
        }
    }

    public void putAll(LongMap<? extends V> t) {
        Iterator<? extends Entry<? extends V>> i = t.entrySet().iterator();
        while (i.hasNext()) {
            Entry<? extends V> e = i.next();
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * Removes all mappings from this map (optional operation). <p>
     *
     * This implementation calls <tt>entrySet().clear()</tt>.
     *
     * Note that this implementation throws an <tt>UnsupportedOperationException</tt> if the <tt>entrySet</tt> does not
     * support the <tt>clear</tt> operation.
     *
     * @throws UnsupportedOperationException clear is not supported by this map.
     */
    public void clear() {
        entrySet().clear();
    }

    // Views

    /**
     * Each of these fields are initialized to contain an instance of the appropriate view the first time this view is
     * requested.  The views are stateless, so there's no reason to create more than one of each.
     */
    transient volatile Set<Long> keySet = null;
    transient volatile Collection<V> values = null;

    /**
     * Returns a Set view of the keys contained in this map.  The Set is backed by the map, so changes to the map are
     * reflected in the Set, and vice-versa.  (If the map is modified while an iteration over the Set is in progress,
     * the results of the iteration are undefined.) The Set supports element removal, which removes the corresponding
     * entry from the map, via the Iterator.remove, Set.remove,  removeAll retainAll, and clear operations.  It does not
     * support the add or addAll operations.<p>
     *
     * This implementation returns a Set that subclasses AbstractSet.  The subclass's iterator method returns a "wrapper
     * object" over this map's entrySet() iterator.  The size method delegates to this map's size method and the
     * contains method delegates to this map's containsKey method.<p>
     *
     * The Set is created the first time this method is called, and returned in response to all subsequent calls.  No
     * synchronization is performed, so there is a slight chance that multiple calls to this method will not all return
     * the same Set.
     *
     * @return a Set view of the keys contained in this map.
     */
    public Set<Long> keySet() {
        if (keySet == null) {
            keySet = new AbstractSet<Long>() {
                public Iterator<Long> iterator() {
                    return new Iterator<Long>() {
                        private Iterator<Entry<V>> i = entrySet().iterator();

                        public boolean hasNext() {
                            return i.hasNext();
                        }

                        public Long next() {
                            return i.next().getKey();
                        }

                        public void remove() {
                            i.remove();
                        }
                    };
                }

                public int size() {
                    return LongAbstractMap.this.size();
                }

                @SuppressWarnings("unused")
                public boolean contains(long k) {
                    return LongAbstractMap.this.containsKey(k);
                }
            };
        }
        return keySet;
    }

    /**
     * Returns a collection view of the values contained in this map.  The collection is backed by the map, so changes
     * to the map are reflected in the collection, and vice-versa.  (If the map is modified while an iteration over the
     * collection is in progress, the results of the iteration are undefined.)  The collection supports element removal,
     * which removes the corresponding entry from the map, via the <tt>Iterator.remove</tt>, <tt>Collection.remove</tt>,
     * <tt>removeAll</tt>, <tt>retainAll</tt> and <tt>clear</tt> operations. It does not support the <tt>add</tt> or
     * <tt>addAll</tt> operations.<p>
     *
     * This implementation returns a collection that subclasses abstract collection.  The subclass's iterator method
     * returns a "wrapper object" over this map's <tt>entrySet()</tt> iterator.  The size method delegates to this map's
     * size method and the contains method delegates to this map's containsValue method.<p>
     *
     * The collection is created the first time this method is called, and returned in response to all subsequent calls.
     *  No synchronization is performed, so there is a slight chance that multiple calls to this method will not all
     * return the same Collection.
     *
     * @return a collection view of the values contained in this map.
     */
    public Collection<V> values() {
        if (values == null) {
            values = new AbstractCollection<V>() {
                public Iterator<V> iterator() {
                    return new Iterator<V>() {
                        private Iterator<Entry<V>> i = entrySet().iterator();

                        public boolean hasNext() {
                            return i.hasNext();
                        }

                        public V next() {
                            return i.next().getValue();
                        }

                        public void remove() {
                            i.remove();
                        }
                    };
                }

                public int size() {
                    return LongAbstractMap.this.size();
                }

                public boolean contains(Object v) {
                    return LongAbstractMap.this.containsValue(v);
                }
            };
        }
        return values;
    }

    /**
     * Returns a set view of the mappings contained in this map.  Each element in this set is a Map.Entry.  The set is
     * backed by the map, so changes to the map are reflected in the set, and vice-versa.  (If the map is modified while
     * an iteration over the set is in progress, the results of the iteration are undefined.)  The set supports element
     * removal, which removes the corresponding entry from the map, via the <tt>Iterator.remove</tt>,
     * <tt>Set.remove</tt>, <tt>removeAll</tt>, <tt>retainAll</tt> and <tt>clear</tt> operations.  It does not support
     * the <tt>add</tt> or <tt>addAll</tt> operations.
     *
     * @return a set view of the mappings contained in this map.
     */
    public abstract Set<Entry<V>> entrySet();

    // Comparison and hashing

    /**
     * Compares the specified object with this map for equality.  Returns <tt>true</tt> if the given object is also a
     * map and the two maps represent the same mappings.  More formally, two maps <tt>t1</tt> and <tt>t2</tt> represent
     * the same mappings if <tt>t1.keySet().equals(t2.keySet())</tt> and for every key <tt>k</tt> in
     * <tt>t1.keySet()</tt>, <tt> (t1.get(k)==null ? t2.get(k)==null : t1.get(k).equals(t2.get(k))) </tt>.  This ensures
     * that the <tt>equals</tt> method works properly across different implementations of the map interface.<p>
     *
     * This implementation first checks if the specified object is this map; if so it returns <tt>true</tt>.  Then, it
     * checks if the specified object is a map whose size is identical to the size of this set; if not, it returns
     * <tt>false</tt>.  If so, it iterates over this map's <tt>entrySet</tt> collection, and checks that the specified
     * map contains each mapping that this map contains.  If the specified map fails to contain such a mapping,
     * <tt>false</tt> is returned.  If the iteration completes, <tt>true</tt> is returned.
     *
     * @param o object to be compared for equality with this map.
     * @return <tt>true</tt> if the specified object is equal to this map.
     */
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof LongAbstractMap)) {
            return false;
        }
        LongAbstractMap<V> t = (LongAbstractMap<V>) o;
        if (t.size() != size()) {
            return false;
        }

        try {
            Iterator<Entry<V>> i = entrySet().iterator();
            while (i.hasNext()) {
                Entry<V> e = i.next();
                long key = e.getKey();
                V value = e.getValue();
                if (value == null) {
                    if (!t.containsKey(key)) {
                        return false;
                    }
                } else {
                    if (!value.equals(t.get(key))) {
                        return false;
                    }
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
     * Returns the hash code value for this map.  The hash code of a map is defined to be the sum of the hash codes of
     * each entry in the map's <tt>entrySet()</tt> view.  This ensures that <tt>t1.equals(t2)</tt> implies that
     * <tt>t1.hashCode()==t2.hashCode()</tt> for any two maps <tt>t1</tt> and <tt>t2</tt>, as required by the general
     * contract of Object.hashCode.<p>
     *
     * This implementation iterates over <tt>entrySet()</tt>, calling <tt>hashCode</tt> on each element (entry) in the
     * Collection, and adding up the results.
     *
     * @return the hash code value for this map.
     * @see Map.Entry#hashCode()
     * @see Object#hashCode()
     * @see Object#equals(Object)
     * @see Set#equals(Object)
     */
    public int hashCode() {
        int h = 0;
        Iterator<Entry<V>> i = entrySet().iterator();
        while (i.hasNext()) {
            h += i.next().hashCode();
        }
        return h;
    }

    /**
     * Returns a string representation of this map.  The string representation consists of a list of key-value mappings
     * in the order returned by the map's <tt>entrySet</tt> view's iterator, enclosed in braces (<tt>"{}"</tt>).
     * Adjacent mappings are separated by the characters <tt>", "</tt> (comma and space).  Each key-value mapping is
     * rendered as the key followed by an equals sign (<tt>"="</tt>) followed by the associated value.  Keys and values
     * are converted to strings as by <tt>String.valueOf(Object)</tt>.<p>
     *
     * This implementation creates an empty string buffer, appends a left brace, and iterates over the map's
     * <tt>entrySet</tt> view, appending the string representation of each <tt>map.entry</tt> in turn.  After appending
     * each entry except the last, the string <tt>", "</tt> is appended.  Finally a right brace is appended.  A string
     * is obtained from the stringbuffer, and returned.
     *
     * @return a String representation of this map.
     */
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("{");

        Iterator<Entry<V>> i = entrySet().iterator();
        boolean hasNext = i.hasNext();
        while (hasNext) {
            Entry<V> e = i.next();
            long key = e.getKey();
            V value = e.getValue();

            buf.append(key);
            buf.append("=");
            if (value == this) {
                buf.append("(this Map)");
            } else {
                buf.append(value);
            }
            hasNext = i.hasNext();
            if (hasNext) {
                buf.append(", ");
            }
        }

        buf.append("}");
        return buf.toString();
    }

    /**
     * Returns a shallow copy of this <tt>AbstractMap</tt> instance: the keys and values themselves are not cloned.
     *
     * @return a shallow copy of this map.
     */
    @SuppressWarnings("unchecked")
    protected Object clone() throws CloneNotSupportedException {
        LongAbstractMap<V> result = (LongAbstractMap<V>) super.clone();
        result.keySet = null;
        result.values = null;
        return result;
    }

    /**
     * This should be made public as soon as possible.  It greatly simplifies the task of implementing Map.
     */
    static class SimpleEntry<VL> implements Entry<VL> {

        long key;
        VL value;

        public SimpleEntry(long key, VL value) {
            this.key = key;
            this.value = value;
        }

        public SimpleEntry(SimpleEntry<VL> e) {
            this.key = e.getKey();
            this.value = e.getValue();
        }

        public long getKey() {
            return key;
        }

        public VL getValue() {
            return value;
        }

        public VL setValue(VL value) {
            VL oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public boolean equals(Object o) {
            if (!(o instanceof SimpleEntry)) {
                return false;
            }
            SimpleEntry e = (SimpleEntry) o;
            return key == e.getKey() && eq(value, e.getValue());
        }

        public int hashCode() {
            return ((int) key) ^ ((int) (key >> 32) ^ ((value == null) ? 0 : value.hashCode()));
        }

        public String toString() {
            return key + "=" + value;
        }

        private static boolean eq(Object o1, Object o2) {
            return (o1 == null ? o2 == null : o1.equals(o2));
        }
    }
}

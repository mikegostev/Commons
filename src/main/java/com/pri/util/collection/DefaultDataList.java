/*
 * Created on Nov 1, 2005
 */
package com.pri.util.collection;

import com.pri.util.ListDataModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.swing.AbstractListModel;
import javax.swing.event.ListDataListener;

/**
 * @author vaz <p> Completely synchronized List implementation. Use synchronized(list) block when iterating over this
 * list. This way you won't get ConcurrentModificationException: <code> ... list = new ArrayListModel () ; ...
 * synchronized (list){ for (Object o:list){ // do you dirty work here } } </code>
 */
public class DefaultDataList<T> implements DataList<T> {

    private Model model;
    private List<T> delegate;

    public DefaultDataList() {
        this(new ArrayList<T>());
    }

    public DefaultDataList(Collection<? extends T> c) {
        this(new ArrayList<T>(c));
    }

    public DefaultDataList(List<T> delegate) {
        this.delegate = delegate;
        model = new Model(delegate);
    }

    @SuppressWarnings("unchecked")
    synchronized public void add(int index, T element) {
        delegate.add(index, element);
        model.fireIntervalAdded((T[]) new Object[]{element}, index, index);
    }

    @SuppressWarnings("unchecked")
    synchronized public boolean add(T o) {
        int index = size();
        boolean result = delegate.add(o);
        if (result) {
            model.fireIntervalAdded((T[]) new Object[]{o}, index, index);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    synchronized public boolean addAll(Collection<? extends T> c) {
        int from = size();
        boolean result = delegate.addAll(c);
        int to = size();
        if (result) {
            model.fireIntervalAdded(c.toArray((T[]) new Object[c.size()]), from, to);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    synchronized public boolean addAll(int index, Collection<? extends T> c) {
        int from = index;
        boolean result = delegate.addAll(index, c);
        int to = from + c.size();
        if (result) {
            model.fireIntervalAdded(c.toArray((T[]) new Object[c.size()]), from, to);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    synchronized public void clear() {
        int last = size();
        T[] oldData = delegate.toArray((T[]) new Object[delegate.size()]);
        delegate.clear();
        if (last > 0) {
            model.fireIntervalRemoved(oldData, 0, last - 1);
        }
    }

    synchronized public Object clone() {
        DefaultDataList<T> alm = new DefaultDataList<T>();
        alm.addAll(this);
        return alm;
    }

    @SuppressWarnings("unchecked")
    synchronized public T remove(int index) {
        T t = delegate.remove(index);
        model.fireIntervalRemoved((T[]) new Object[]{t}, index, index);
        return t;
    }

    @SuppressWarnings("unchecked")
    synchronized public boolean remove(Object o) {
        int index = indexOf(o);
        T[] oldData = (T[]) new Object[]{o};
        boolean result = delegate.remove(o);
        if (result) {
            model.fireIntervalRemoved(oldData, index, index);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    synchronized public T set(int index, T element) {
        T t = delegate.set(index, element);
        model.fireIntervalChanged((T[]) new Object[]{element}, (T[]) new Object[]{t}, index, index);
        return t;
    }

    synchronized public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        Iterator<?> e = iterator();
        while (e.hasNext()) {
            if (c.contains(e.next())) {
                e.remove();
                modified = true;
            }
        }
        return modified;
    }

    synchronized public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Iterator<T> e = iterator();
        while (e.hasNext()) {
            if (!c.contains(e.next())) {
                e.remove();
                modified = true;
            }
        }
        return modified;
    }

    synchronized public int getSize() {
        return model.getSize();
    }

    synchronized public Object getElementAt(int index) {
        return model.getElementAt(index);
    }

    synchronized public void addListDataListener(ListDataListener l) {
        model.addListDataListener(l);
    }

    synchronized public void removeListDataListener(ListDataListener l) {
        model.removeListDataListener(l);
    }

    synchronized public void addListListener(ListListener<T> listener) {
        model.addListListener(listener);
    }

    synchronized public void removeListListener(ListListener<T> listener) {
        model.removeListListener(listener);
    }

    synchronized public int size() {
        return delegate.size();
    }

    synchronized public boolean isEmpty() {
        return delegate.isEmpty();
    }

    synchronized public boolean contains(Object o) {
        return delegate.contains(o);
    }

    synchronized public Iterator<T> iterator() {
        return delegate.iterator();
    }

    synchronized public Object[] toArray() {
        return delegate.toArray();
    }

    synchronized public <E> E[] toArray(E[] a) {
        return delegate.toArray(a);
    }

    synchronized public boolean containsAll(Collection<?> c) {
        return delegate.contains(c);
    }

    synchronized public T get(int index) {
        return delegate.get(index);
    }

    synchronized public int indexOf(Object o) {
        return delegate.indexOf(o);
    }

    synchronized public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    synchronized public ListIterator<T> listIterator() {
        return delegate.listIterator();
    }

    synchronized public ListIterator<T> listIterator(int index) {
        return delegate.listIterator(index);
    }

    synchronized public List<T> subList(int fromIndex, int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }

    synchronized public T getByID(@SuppressWarnings("unused") int id) {
        throw new UnsupportedOperationException("I have no idea how to extract IDs from these elements.");
    }

    private class Model extends AbstractListModel implements ListDataModel<T> {

        private List<T> list;

        public Model(List<T> list) {
            super();
            this.list = list;
        }

        synchronized public int getSize() {
            return list.size();
        }

        synchronized public T getElementAt(int index) {
            return list.get(index);
        }

        synchronized public void addListListener(ListListener<T> listener) {
            super.listenerList.add(ListListener.class, listener);
        }

        synchronized public void removeListListener(ListListener<T> listener) {
            super.listenerList.remove(ListListener.class, listener);
        }

        @SuppressWarnings("unchecked")
        synchronized public void fireIntervalAdded(T[] newValues, int from, int to) {
            Object[] listeners = listenerList.getListenerList();

            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == ListListener.class) {
                    Object l = listeners[i + 1];
                    ListListener<T> ll = (ListListener<T>) l;
                    (ll).intervalAdded(newValues, from, to);
                }
            }

            fireIntervalAdded(this, from, to);
        }

        @SuppressWarnings("unchecked")
        synchronized public void fireIntervalRemoved(T[] oldValues, int from, int to) {
            Object[] listeners = listenerList.getListenerList();

            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == ListListener.class) {
                    ((ListListener<T>) listeners[i + 1]).intervalRemoved(oldValues, from, to);
                }
            }

            fireIntervalRemoved(this, from, to);
        }

        @SuppressWarnings("unchecked")
        synchronized public void fireIntervalChanged(T[] newValues, T[] oldValues, int from, int to) {
            Object[] listeners = listenerList.getListenerList();

            for (int i = listeners.length - 2; i >= 0; i -= 2) {
                if (listeners[i] == ListListener.class) {
                    ((ListListener<T>) listeners[i + 1]).intervalChanged(newValues, oldValues, from, to);
                }
            }

            fireContentsChanged(this, from, to);
        }
    }

}

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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.AbstractListModel;
import javax.swing.event.ListDataListener;

/**
 * <p> Completely synchronized {@link DataList} implementation. The idea is the same as in {@link DefaultDataList} but
 * this implementation can use an external {@link java.util.concurrent.locks.Lock} object for synchronization purposes.
 * The lock to use must be given in the constructor. If null is given, then a new instance of {@link
 * java.util.concurrent.locks.ReentrantLock} will be used. Use {@link #getLock} method to retrieve the lock.
 *
 * @author vaz
 */
public class LockedDataList<T> implements DataList<T> {

    private Model model;
    private List<T> delegate;
    private Lock lock;

    public LockedDataList(Lock lock) {
        this(new ArrayList<T>(), lock);
    }

    public LockedDataList(Collection<? extends T> c, Lock lock) {
        this(new ArrayList<T>(c), lock);
    }

    public LockedDataList(List<T> delegate, Lock lock) {
        this.delegate = delegate;
        this.lock = lock == null ? new ReentrantLock() : lock;
        model = new Model(delegate);
    }

    public Lock getLock() {
        return lock;
    }

    @SuppressWarnings("unchecked")
    public void add(int index, T element) {
        lock.lock();
        try {
            delegate.add(index, element);
            model.fireIntervalAdded((T[]) new Object[]{element}, index, index);
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public boolean add(T o) {
        lock.lock();
        try {
            int index = size();
            boolean result = delegate.add(o);
            if (result) {
                model.fireIntervalAdded((T[]) new Object[]{o}, index, index);
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public boolean addAll(Collection<? extends T> c) {
        lock.lock();
        try {
            int from = size();
            boolean result = delegate.addAll(c);
            int to = size();
            if (result) {
                model.fireIntervalAdded(c.toArray((T[]) new Object[c.size()]), from, to);
            }
            return result;

        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public boolean addAll(int index, Collection<? extends T> c) {
        lock.lock();
        try {
            int from = index;
            boolean result = delegate.addAll(index, c);
            int to = from + c.size();
            if (result) {
                model.fireIntervalAdded(c.toArray((T[]) new Object[c.size()]), from, to);
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public void clear() {
        lock.lock();
        try {
            int last = size();
            T[] oldData = delegate.toArray((T[]) new Object[delegate.size()]);
            delegate.clear();
            if (last > 0) {
                model.fireIntervalRemoved(oldData, 0, last - 1);
            }
        } finally {
            lock.unlock();
        }
    }

    public Object clone() {
        lock.lock();
        try {
            LockedDataList<T> alm = new LockedDataList<T>(lock);
            alm.addAll(this);
            return alm;
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public T remove(int index) {
        lock.lock();
        try {
            T t = delegate.remove(index);
            model.fireIntervalRemoved((T[]) new Object[]{t}, index, index);
            return t;
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public boolean remove(Object o) {
        lock.lock();
        try {
            int index = indexOf(o);
            T[] oldData = (T[]) new Object[]{o};
            boolean result = delegate.remove(o);
            if (result) {
                model.fireIntervalRemoved(oldData, index, index);
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("unchecked")
    public T set(int index, T element) {
        lock.lock();
        try {
            T t = delegate.set(index, element);
            model.fireIntervalChanged((T[]) new Object[]{element}, (T[]) new Object[]{t}, index, index);
            return t;
        } finally {
            lock.unlock();
        }
    }

    public boolean removeAll(Collection<?> c) {
        lock.lock();
        try {
            boolean modified = false;
            Iterator<?> e = iterator();
            while (e.hasNext()) {
                if (c.contains(e.next())) {
                    e.remove();
                    modified = true;
                }
            }
            return modified;
        } finally {
            lock.unlock();
        }
    }

    public boolean retainAll(Collection<?> c) {
        lock.lock();
        try {
            boolean modified = false;
            Iterator<T> e = iterator();
            while (e.hasNext()) {
                if (!c.contains(e.next())) {
                    e.remove();
                    modified = true;
                }
            }
            return modified;
        } finally {
            lock.unlock();
        }
    }

    public int getSize() {
        lock.lock();
        try {
            return model.getSize();
        } finally {
            lock.unlock();
        }
    }

    public Object getElementAt(int index) {
        lock.lock();
        try {
            return model.getElementAt(index);
        } finally {
            lock.unlock();
        }
    }

    public void addListDataListener(ListDataListener l) {
        lock.lock();
        try {
            model.addListDataListener(l);
        } finally {
            lock.unlock();
        }
    }

    public void removeListDataListener(ListDataListener l) {
        lock.lock();
        try {
            model.removeListDataListener(l);
        } finally {
            lock.unlock();
        }
    }

    public void addListListener(ListListener<T> listener) {
        lock.lock();
        try {
            model.addListListener(listener);
        } finally {
            lock.unlock();
        }
    }

    public void removeListListener(ListListener<T> listener) {
        lock.lock();
        try {
            model.removeListListener(listener);
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return delegate.size();
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return delegate.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    public boolean contains(Object o) {
        lock.lock();
        try {
            return delegate.contains(o);
        } finally {
            lock.unlock();
        }
    }

    public Iterator<T> iterator() {
        lock.lock();
        try {
            return delegate.iterator();
        } finally {
            lock.unlock();
        }
    }

    public Object[] toArray() {
        lock.lock();
        try {
            return delegate.toArray();
        } finally {
            lock.unlock();
        }
    }

    public <E> E[] toArray(E[] a) {
        lock.lock();
        try {
            return delegate.toArray(a);
        } finally {
            lock.unlock();
        }
    }

    public boolean containsAll(Collection<?> c) {
        lock.lock();
        try {
            return delegate.contains(c);
        } finally {
            lock.unlock();
        }
    }

    public T get(int index) {
        lock.lock();
        try {
            return delegate.get(index);
        } finally {
            lock.unlock();
        }
    }

    public int indexOf(Object o) {
        lock.lock();
        try {
            return delegate.indexOf(o);
        } finally {
            lock.unlock();
        }
    }

    public int lastIndexOf(Object o) {
        lock.lock();
        try {
            return delegate.lastIndexOf(o);
        } finally {
            lock.unlock();
        }
    }

    public ListIterator<T> listIterator() {
        lock.lock();
        try {
            return delegate.listIterator();
        } finally {
            lock.unlock();
        }
    }

    public ListIterator<T> listIterator(int index) {
        lock.lock();
        try {
            return delegate.listIterator(index);
        } finally {
            lock.unlock();
        }
    }

    public List<T> subList(int fromIndex, int toIndex) {
        lock.lock();
        try {
            return delegate.subList(fromIndex, toIndex);
        } finally {
            lock.unlock();
        }
    }

    public T getByID(@SuppressWarnings("unused") int id) {
        throw new UnsupportedOperationException("I have no idea how to extract IDs from these elements.");
    }

    private class Model extends AbstractListModel implements ListDataModel<T> {

        private List<T> list;

        public Model(List<T> list) {
            super();
            this.list = list;
        }

        public int getSize() {
            lock.lock();
            try {
                return list.size();
            } finally {
                lock.unlock();
            }
        }

        public T getElementAt(int index) {
            lock.lock();
            try {
                return list.get(index);
            } finally {
                lock.unlock();
            }
        }

        public void addListListener(ListListener<T> listener) {
            lock.lock();
            try {
                super.listenerList.add(ListListener.class, listener);
            } finally {
                lock.unlock();
            }
        }

        public void removeListListener(ListListener<T> listener) {
            lock.lock();
            try {
                super.listenerList.remove(ListListener.class, listener);
            } finally {
                lock.unlock();
            }
        }

        @SuppressWarnings("unchecked")
        public void fireIntervalAdded(T[] newValues, int from, int to) {
            lock.lock();
            try {
                Object[] listeners = listenerList.getListenerList();

                for (int i = listeners.length - 2; i >= 0; i -= 2) {
                    if (listeners[i] == ListListener.class) {
                        Object l = listeners[i + 1];
                        ListListener<T> ll = (ListListener<T>) l;
                        (ll).intervalAdded(newValues, from, to);
                    }
                }
                fireIntervalAdded(this, from, to);
            } finally {
                lock.unlock();
            }

        }

        @SuppressWarnings("unchecked")
        public void fireIntervalRemoved(T[] oldValues, int from, int to) {
            lock.lock();
            try {
                Object[] listeners = listenerList.getListenerList();

                for (int i = listeners.length - 2; i >= 0; i -= 2) {
                    if (listeners[i] == ListListener.class) {
                        ((ListListener<T>) listeners[i + 1]).intervalRemoved(oldValues, from, to);
                    }
                }

                fireIntervalRemoved(this, from, to);
            } finally {
                lock.unlock();
            }
        }

        @SuppressWarnings("unchecked")
        public void fireIntervalChanged(T[] newValues, T[] oldValues, int from, int to) {
            lock.lock();
            try {
                Object[] listeners = listenerList.getListenerList();

                for (int i = listeners.length - 2; i >= 0; i -= 2) {
                    if (listeners[i] == ListListener.class) {
                        ((ListListener<T>) listeners[i + 1]).intervalChanged(newValues, oldValues, from, to);
                    }
                }

                fireContentsChanged(this, from, to);
            } finally {
                lock.unlock();
            }
        }
    }
}

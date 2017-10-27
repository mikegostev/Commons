/*
 * Created on Nov 28, 2005
 */
package com.pri.util.collection;

import java.util.EventListener;


public interface ListListener<T> extends EventListener {

    public void intervalAdded(T[] newValues, int from, int to);

    public void intervalRemoved(T[] oldValues, int from, int to);

    public void intervalChanged(T[] newValues, T[] oldValues, int from, int to);
}

/*
 * Created on 29.06.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.util.collection;

import java.util.Iterator;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class ArrayIterator implements Iterator {

    private Object[] array;
    private int pos;

    /**
     *
     */
    public ArrayIterator(Object[] ar) {
        super();
        array = ar;
        pos = 0;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove() {
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return array != null && pos < array.length;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public Object next() {
        return array[pos++];
    }

}

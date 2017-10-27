/*
 * Created on 16.07.2004
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
public class UnionIterator implements Iterator {

    private Iterator[] iters;
    private int iPtr;

    /**
     *
     */
    public UnionIterator(Iterator[] iters) {
        this.iters = iters;
        iPtr = 0;
    }


    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        iters[iPtr].remove();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        if (iPtr >= iters.length) {
            return false;
        }

        if (iters[iPtr].hasNext()) {
            return true;
        }

        iPtr++;

        return hasNext();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public Object next() {
        if (iPtr >= iters.length) {
            return null;
        }

        hasNext();
        return iters[iPtr].next();
    }

}

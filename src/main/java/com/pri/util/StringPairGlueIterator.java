/*
 * Created on 29.06.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.util;

import java.util.Iterator;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class StringPairGlueIterator implements Iterator {

    private String glue;
    Iterator iter;

    /**
     *
     */
    public StringPairGlueIterator(Iterator it, String glue) {
        this.glue = glue;
        this.iter = it;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    public void remove() {
        iter.remove();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    public boolean hasNext() {
        return iter.hasNext();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    public Object next() {
        StringPair pair = (StringPair) iter.next();
        return pair.getFirst() + glue + pair.getSecond();
    }

}

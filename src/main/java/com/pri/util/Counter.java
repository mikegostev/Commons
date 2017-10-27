/*
 * Created on 21.05.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.util;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class Counter extends Number {

    private static final long serialVersionUID = 698465133888087160L;

    private int count;

    /**
     *
     */
    public Counter() {
        count = 0;
    }

    public Counter(int init) {
        count = init;
    }

    public int inc() {
        return ++count;
    }

    public int add(int v) {
        return count += v;
    }


    public int dec() {
        return --count;
    }

    public int intValue() {
        return count;
    }

    public String toString() {
        return String.valueOf(count);
    }

    /* (non-Javadoc)
     * @see java.lang.Number#doubleValue()
     */
    public double doubleValue() {
        // TODO Auto-generated method stub
        return count;
    }

    /* (non-Javadoc)
     * @see java.lang.Number#floatValue()
     */
    public float floatValue() {
        // TODO Auto-generated method stub
        return count;
    }

    /* (non-Javadoc)
     * @see java.lang.Number#longValue()
     */
    public long longValue() {
        // TODO Auto-generated method stub
        return count;
    }

}

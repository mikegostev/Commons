/*
 * Created on 14.07.2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.pri.util;

import java.io.Serializable;


/**
 * @author mg
 *
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and
 * Comments
 */
public class FloatInterval implements Serializable {

    private float begin, end;

    /**
     * @param begin
     * @param end
     */
    public FloatInterval(float begin, float end) {
        this.begin = begin;
        this.end = end;
    }

    public FloatInterval() {
        begin = end = 0;
    }

    /**
     * @return Returns the begin.
     */
    public float getBegin() {
        return begin;
    }

    /**
     * @param begin The begin to set.
     */
    public void setBegin(float begin) {
        this.begin = begin;
    }

    /**
     * @return Returns the end.
     */
    public float getEnd() {
        return end;
    }

    /**
     * @param end The end to set.
     */
    public void setEnd(float end) {
        this.end = end;
    }

    public boolean contains(float i) {
        return (begin <= i && i <= end);
    }
}

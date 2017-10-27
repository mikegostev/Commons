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
public class Interval implements Serializable {

    private int begin, end;

    /**
     * @param begin
     * @param end
     */
    public Interval(int begin, int end) {
        this.begin = begin;
        this.end = end;
    }

    public Interval() {
        begin = end = 0;
    }

    /**
     * @return Returns the begin.
     */
    public int getBegin() {
        return begin;
    }

    /**
     * @param begin The begin to set.
     */
    public void setBegin(int begin) {
        this.begin = begin;
    }

    /**
     * @return Returns the end.
     */
    public int getEnd() {
        return end;
    }

    /**
     * @param end The end to set.
     */
    public void setEnd(int end) {
        this.end = end;
    }

    public boolean contains(int i) {
        return (begin <= i && i <= end);
    }
}

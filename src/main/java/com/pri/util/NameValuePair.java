/*
 * Created on 29.06.2004
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
public class NameValuePair extends StringPair {

    /**
     *
     */
    public NameValuePair() {
        super();
    }

    /**
     * @param s1
     * @param s2
     */
    public NameValuePair(String s1, String s2) {
        super(s1, s2);
    }

    public String getName() {
        return getFirst();
    }

    public String getValue() {
        return getSecond();
    }

    public void setName(String str) {
        setFirst(str);
    }

    public void setValue(String str) {
        setSecond(str);
    }
}
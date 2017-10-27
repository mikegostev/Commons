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
public class StringPair {

    String str1;
    String str2;

    /**
     *
     */
    public StringPair() {
    }

    public StringPair(String s1, String s2) {
        str1 = s1;
        str2 = s2;
    }

    public String getFirst() {
        return str1;
    }

    public String getSecond() {
        return str2;
    }

    public void setFirst(String s) {
        str1 = s;
    }

    public void setSecond(String s) {
        str2 = s;
    }

}

package com.pri.util;

import java.io.Serializable;

public class StringHolder implements CharSequence, Serializable {

    private String str;

    public StringHolder(String s) {
        str = s;
    }

    public int length() {
        return str.length();
    }

    public char charAt(int index) {
        return str.charAt(index);
    }

    public CharSequence subSequence(int start, int end) {
        return str.subSequence(start, end);
    }

    public void setString(String str) {
        this.str = str;
    }

    @Override
    public String toString() {
        return str;
    }

    @Override
    public int hashCode() {
        return str.hashCode();
    }

}

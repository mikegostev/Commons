package com.pri.util;

import java.util.NoSuchElementException;

public class SimpleTokenizer {

    private char sep;
    private String str;
    private boolean hasToken = true;
    private int cpos = 0;
    private int sepPos;

    public SimpleTokenizer(String str, char sepch) {
        sep = sepch;
        this.str = str;
        sepPos = str.indexOf(sep);
    }

    public boolean hasMoreTokens() {
        return hasToken;
    }

    public String nextToken() {
        if (!hasToken) {
            throw new NoSuchElementException("No more tokens");
        }

        if (sepPos == -1) {
            hasToken = false;
            if (cpos < str.length()) {
                return str.substring(cpos);
            } else {
                return null;
            }
        }

        String res = null;

        if (cpos < sepPos) {
            res = str.substring(cpos, sepPos);
        }

        cpos = sepPos + 1;
        sepPos = str.indexOf(sep, cpos);

        return res;
    }

}

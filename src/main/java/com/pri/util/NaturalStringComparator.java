package com.pri.util;

import java.io.Serializable;
import java.util.Comparator;

public class NaturalStringComparator implements Comparator<String>, Serializable {

    private static final long serialVersionUID = 1L;

    private static NaturalStringComparator instance = new NaturalStringComparator();

    public static NaturalStringComparator getInstance() {
        return instance;
    }

    @Override
    public int compare(String o1, String o2) {
        return StringUtils.naturalCompare(o1, o2);
    }

}

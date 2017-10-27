package com.pri.util;

import java.io.Serializable;

public class Pair<First, Second> implements Serializable {

    private First first;
    private Second second;

    public Pair() {
    }

    public Pair(First first, Second second) {
        this.first = first;
        this.second = second;
    }


    public First getFirst() {
        return first;
    }

    public void setFirst(First first) {
        this.first = first;
    }

    public Second getSecond() {
        return second;
    }

    public void setSecond(Second second) {
        this.second = second;
    }

    public String toString() {
        return first.toString() + " => " + second.toString();
    }
}

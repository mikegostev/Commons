package com.pri.util;

public class Random {

    public Random() {
        super();
        // TODO Auto-generated constructor stub
    }

    static public int randInt(int min, int max) {
        return min + (int) ((max - min + 1) * Math.random());
    }
}

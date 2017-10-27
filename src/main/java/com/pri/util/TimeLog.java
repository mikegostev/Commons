package com.pri.util;

public class TimeLog {

    private static long startTime = System.currentTimeMillis();
    private static long lastEvent = startTime;

    public static void reportEvent(String msg) {
        long prev = lastEvent;
        lastEvent = System.currentTimeMillis();
        System.out.println(msg + " Time: " + (lastEvent - startTime) + "ms Delta: " + (lastEvent - prev) + "ms");
    }
}

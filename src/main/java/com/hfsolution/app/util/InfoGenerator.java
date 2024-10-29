package com.hfsolution.app.util;

public class InfoGenerator {
    public static String generateInfo(String methodName, long startTime) {
        long endTime = System.currentTimeMillis();
        return "[method = " + methodName + "] - [start-time = " + startTime + "] - [end-time=" + endTime + "]";
    }
}

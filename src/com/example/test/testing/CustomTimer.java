package com.example.test.testing;

/**
 * Class provides methods for counting time intervals
 * Created by Evgeniy Bezkorovayniy
 * thirteenisluckynumber@gmail.com
 *
 * 30.04.2020
 */
public class CustomTimer {
    private static long startTimeMs, stopTimeMs;

    public static void startTimer() {
        startTimeMs = System.currentTimeMillis();
        stopTimeMs = startTimeMs;
    }
    public static double stopTimer() {
        stopTimeMs = System.currentTimeMillis();
        return (double)(stopTimeMs - startTimeMs) / 1000;
    }
}

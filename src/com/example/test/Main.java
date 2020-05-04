package com.example.test;

import com.example.test.testing.RecognizingTest;

import static com.example.test.ApplicationConfig.SystemType.*;

/**
 * Class contains application entry point and calls to 2 testing methods
 * Created by Evgeniy Bezkorovayniy
 * thirteenisluckynumber@gmail.com
 *
 * 30.04.2020
 */
public class Main {

    public static void main(String[] args) {
        boolean printDetails = false;
        boolean useExistingRecognizer = true;

        new RecognizingTest(SOURCE_SYSTEM, useExistingRecognizer, printDetails).start();
        new RecognizingTest(MODIFIED_SYSTEM, useExistingRecognizer, printDetails).start();
    }
}

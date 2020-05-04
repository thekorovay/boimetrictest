package com.example.test;

/**
 * Class stores main constants
 * Created by Evgeniy Bezkorovayniy
 * thirteenisluckynumber@gmail.com
 *
 * 30.04.2020
 */
public class ApplicationConfig {
    public enum SystemType {
        SOURCE_SYSTEM,
        MODIFIED_SYSTEM
    }

    public static final String HAAR_CLASSIFIER_FILE_PATH = "C:\\IntellijIDEAProjects\\BiometricTests\\files\\cascade\\haarcascade_frontalface_alt.xml";

    public static final String LBPH_RECOGNIZER_FILE_PATH = "C:\\IntellijIDEAProjects\\BiometricTests\\files\\recognizers\\lbph.xml";
    public static final String EIGENFACE_RECOGNIZER_FILE_PATH = "C:\\IntellijIDEAProjects\\BiometricTests\\files\\recognizers\\eigenface.xml";
    public static final String FISHERFACE_RECOGNIZER_FILE_PATH = "C:\\IntellijIDEAProjects\\BiometricTests\\files\\recognizers\\fisherface.xml";

    public static final String SOURCE_FACE_DATASET_PATH = "C:\\IntellijIDEAProjects\\BiometricTests\\files\\faces_source\\";
    public static final String MODOFIED_FACE_DATASET_PATH = "C:\\IntellijIDEAProjects\\BiometricTests\\files\\faces_modified\\";
    public static final String CONTROL_FACE_DATASET_PATH = "C:\\IntellijIDEAProjects\\BiometricTests\\files\\faces_control\\";
}
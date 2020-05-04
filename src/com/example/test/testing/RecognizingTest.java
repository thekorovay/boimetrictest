package com.example.test.testing;

import com.example.test.core.CVFaceDetector;
import com.example.test.core.CVFaceRecognizer;
import com.example.test.core.ControlData;
import com.example.test.core.TrainingData;
import java.text.DecimalFormat;

import static com.example.test.ApplicationConfig.*;

/**
 * Class contains logic for testing biometric system's accuracy
 * Created by Evgeniy Bezkorovayniy
 * thirteenisluckynumber@gmail.com
 *
 * 30.04.2020
 */
public class RecognizingTest {
    private final boolean printDetails;
    private final SystemType systemType;
    private CVFaceRecognizer faceRecognizer;

    public RecognizingTest(SystemType systemType, boolean useExistingRecognizer, boolean printDetails) {
        this.systemType = systemType;
        this.printDetails = printDetails;

        faceRecognizer = new CVFaceRecognizer(systemType == SystemType.SOURCE_SYSTEM ?
                CVFaceRecognizer.RecognizingAlgorithm.EIGENFACE : CVFaceRecognizer.RecognizingAlgorithm.LBPH);

        prepareRecognizer(useExistingRecognizer);
    }

    private void prepareRecognizer(boolean useExistingRecognizer) {
        String systemTypeName = systemType == SystemType.SOURCE_SYSTEM ? "Source system" : "Modified system";
        System.out.println("Preparing " + systemTypeName + ".");

        String recognizerPath = systemType == SystemType.SOURCE_SYSTEM ?
                EIGENFACE_RECOGNIZER_FILE_PATH :
                LBPH_RECOGNIZER_FILE_PATH;

        String trainingSetPath = systemType == SystemType.SOURCE_SYSTEM ?
                SOURCE_FACE_DATASET_PATH :
                MODOFIED_FACE_DATASET_PATH;

        if (useExistingRecognizer) {
            faceRecognizer.loadRecognizer(recognizerPath);
            System.out.println(systemTypeName + " recognizer successfully loaded.");
        } else {
            try {
                System.out.println("\nPreparing training data.");
                TrainingData trainingData = CVFaceDetector.prepareTrainingData(
                        trainingSetPath,
                        systemType == SystemType.MODIFIED_SYSTEM,
                        printDetails
                );
                if (trainingData == null) {
                    System.out.println("\nError: Unable to prepare training data.");
                    return;
                }
                System.out.println("\nTraining data successfully prepared.");

                faceRecognizer.trainRecognizer(trainingData, recognizerPath);
                System.out.println(systemTypeName + " recognizer successfully trained and saved.");
            } catch (Exception ex) {
                faceRecognizer = null;
                System.out.println("Error occurred while training recognizer.");
            }
        }
    }

    public void start() {
        System.out.println("\nPreparing control data.");
        ControlData controlData = CVFaceDetector.prepareControlData(
                CONTROL_FACE_DATASET_PATH,
                systemType == SystemType.MODIFIED_SYSTEM,
                printDetails
        );
        if (controlData == null) {
            System.out.println("\nError: Unable to prepare control data.");
            return;
        }
        System.out.println("\nControl data successfully prepared.");

        String systemTypeName = systemType == SystemType.SOURCE_SYSTEM ? "Source system" : "Modified system";
        System.out.println("\n\nStarted testing " + systemTypeName);

        CustomTimer.startTimer();
        int tries = (int)controlData.getFaces().size();
        int mistakes = 0;

        for (int i = 0; i < tries; i++) {
            int faceLabel = controlData.getLabels().get(i);
            int prediction = faceRecognizer.predict(controlData.getFaces().get(i));

            if (printDetails)
                System.out.println(systemTypeName + " predicted that face_" + faceLabel + " is face_" + prediction);

            if (faceLabel != prediction)
                mistakes++;
        }

        double accuracy = 1.0 - ((double)mistakes / (double)tries);
        double timeSpent = CustomTimer.stopTimer();
        printResults(systemTypeName, tries, mistakes, accuracy, timeSpent);
    }
    
    private void printResults(String systemTypeName, int tries, int mistakes, double accuracy, double timeSpent) {
        double accuracyInPercents = accuracy * 100;

        System.out.println("\n" + systemTypeName + " results:");
        System.out.println("Tries: " + tries);
        System.out.println("Mistakes: " + mistakes);
        System.out.println("Accuracy: " + formatAsAccuracy(accuracy) + "(" + formatAsPercents(accuracyInPercents) + ")");
        System.out.println("Time spent: " + formatAsTime(timeSpent) + " seconds");
    }

    private String formatAsTime(double value) { return new DecimalFormat("###.#####").format(value); }
    private String formatAsAccuracy(double value) { return new DecimalFormat("#.#####").format(value); }
    private String formatAsPercents(double value) { return new DecimalFormat("##.###'%'").format(value); }
}

package com.example.test.core;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_face.EigenFaceRecognizer;
import org.bytedeco.opencv.opencv_face.FaceRecognizer;
import org.bytedeco.opencv.opencv_face.FisherFaceRecognizer;
import org.bytedeco.opencv.opencv_face.LBPHFaceRecognizer;

/**
 * Class for recognizing faces on the image
 * Created by Evgeniy Bezkorovayniy
 * thirteenisluckynumber@gmail.com
 *
 * 30.04.2020
 */
public class CVFaceRecognizer {
    public enum RecognizingAlgorithm {
        LBPH,
        FISHERFACE,
        EIGENFACE
    }

    private FaceRecognizer faceRecognizer;
    private RecognizingAlgorithm algorithm;

    public CVFaceRecognizer(RecognizingAlgorithm algorithm) {
        this.algorithm = algorithm;
        faceRecognizer = algorithm == RecognizingAlgorithm.LBPH ? LBPHFaceRecognizer.create() :
                algorithm == RecognizingAlgorithm.EIGENFACE ? EigenFaceRecognizer.create() :
                        FisherFaceRecognizer.create();
    }

    public void trainRecognizer(TrainingData trainingData, String filePath) {
        faceRecognizer.train(trainingData.getFaces(), trainingData.getLabels());
        faceRecognizer.write(filePath);
    }

    public void loadRecognizer(String recognizerFile) { faceRecognizer.read(recognizerFile); }

    public int predict(Mat face) {
        IntPointer label = new IntPointer(1);
        faceRecognizer.predict(face, label, new DoublePointer(1));
        return label.get(0);
    }
}

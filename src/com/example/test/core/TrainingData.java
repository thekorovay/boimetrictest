package com.example.test.core;

import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;

/**
 * DTO for training data
 * Created by Evgeniy Bezkorovayniy
 * thirteenisluckynumber@gmail.com
 *
 * 30.04.2020
 */
public class TrainingData {
    private final MatVector faces;
    private final Mat labels;

    public TrainingData(MatVector faces, Mat labels) {
        this.faces = faces;
        this.labels = labels;
    }

    public MatVector getFaces() { return faces; }
    public Mat getLabels() { return labels; }
}

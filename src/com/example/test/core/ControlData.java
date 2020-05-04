package com.example.test.core;

import org.bytedeco.opencv.opencv_core.MatVector;

import java.nio.IntBuffer;

/**
 * DTO for control data
 * Created by Evgeniy Bezkorovayniy
 * thirteenisluckynumber@gmail.com
 *
 * 30.04.2020
 */
public class ControlData {
    private final MatVector faces;
    private final IntBuffer labels;

    public ControlData(MatVector faces, IntBuffer labels) {
        this.faces = faces;
        this.labels = labels;
    }

    public MatVector getFaces() { return faces; }
    public IntBuffer getLabels() { return labels; }
}

package com.example.test.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
 
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.MatVector;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;

import static com.example.test.ApplicationConfig.*;
import static org.bytedeco.opencv.global.opencv_core.CV_32SC1;

/**
 * Class for detecting faces on the image
 * Created by Evgeniy Bezkorovayniy
 * thirteenisluckynumber@gmail.com
 *
 * 30.04.2020
 */
public class CVFaceDetector {
    public static final int DETECTED_FACE_OUTPUT_SIZE_PX = 128;
    public static final double MODIFIED_START_DETECTING_WINDOW_SCALE = 0.3;
    public static final double DEFAULT_SCALE_RATE = 1.1;
    public static final int MIN_NEIGHBOURS = 3;
    public static final int FLAGS = 0;

    private static OpenCVFrameConverter.ToMat converterToMat;
    private static Java2DFrameConverter converterToFrame;
    private static CascadeClassifier cascadeClassifier;

    public static ControlData prepareControlData(String controlSetPath, boolean modifiedWindow, boolean printDetails) {
        List<Mat> facesList = new ArrayList<>();
        List<Integer> labelsList = new ArrayList<>();

        try {
            fillArraysWithFaceData(controlSetPath, facesList, labelsList, modifiedWindow, printDetails);
        } catch (Exception e) {
            return null;
        }

        // Convert objects with detected faces and labels to OpenCV objects
        MatVector faces = new MatVector(facesList.size());
        IntBuffer labelsBuffer = IntBuffer.allocate(facesList.size());

        for (int i = 0; i < facesList.size(); i++) {
            faces.put(i, facesList.get(i));
            labelsBuffer.put(labelsList.get(i));
        }

        labelsBuffer.rewind();

        return new ControlData(faces, labelsBuffer);
    }

    public static TrainingData prepareTrainingData(String trainingSetPath, boolean modifiedWindow, boolean printDetails) {
        List<Mat> facesList = new ArrayList<>();
        List<Integer> labelsList = new ArrayList<>();

        try {
            fillArraysWithFaceData(trainingSetPath, facesList, labelsList, modifiedWindow, printDetails);
        } catch (Exception e) {
            return null;
        }

        // Convert objects with detected faces and labels to OpenCV objects
        MatVector faces = new MatVector(facesList.size());
        Mat labels = new Mat(facesList.size(), 1, CV_32SC1);
        IntBuffer labelsBuffer = labels.createBuffer();

        for (int i = 0; i < facesList.size(); i++) {
            faces.put(i, facesList.get(i));
            labelsBuffer.put(i, labelsList.get(i));
        }

        return new TrainingData(faces, labels);
    }

    private static void fillArraysWithFaceData(String facesPath, List<Mat> facesList, List<Integer> labelsList,
                                               boolean modifiedWindow, boolean printDetails) throws Exception {
        Files.walk(Paths.get(facesPath)).forEach(path -> {
            if (!Files.exists(path) || Files.isDirectory(path))
                return;

            int label = Integer.parseInt(path.getFileName().toString().split("-")[0]);

            Mat face;
            if (modifiedWindow)
                face = getFace(readImageFromFile(path.toString()), MODIFIED_START_DETECTING_WINDOW_SCALE);
            else
                face = getFace(readImageFromFile(path.toString()));

            if (face != null) {
                facesList.add(face);
                labelsList.add(label);
                if (printDetails)
                    System.out.println("Successfully added face_" + label);
            } else {
                if (printDetails)
                    System.out.println("Detector didn't find face on face_" + path);
            }
        });
    }

    public static BufferedImage readImageFromFile(String file) {
        BufferedImage image = null;
        try {
            image = javax.imageio.ImageIO.read(new File(file));
        } catch (IOException ignored) { }
        return image;
    }

    private static Mat getFace(BufferedImage image) {
        return getDetectedFaceOnMat(getNormalizedFace(image));
    }

    private static Mat getFace(BufferedImage image, double startSize) {
        return getDetectedFaceOnMat(getNormalizedFace(image), startSize);
    }

    private static Mat getNormalizedFace(BufferedImage image) {
        if (converterToMat == null) {
            converterToMat = new OpenCVFrameConverter.ToMat();
            converterToFrame = new Java2DFrameConverter();
            cascadeClassifier = new CascadeClassifier(new File(HAAR_CLASSIFIER_FILE_PATH).getAbsolutePath());
        }

        return normalize(converterToFrame.getFrame(image));
    }

    private static Mat normalize(Frame frame) {
        Mat matSourceImage = converterToMat.convertToMat(frame);
        Mat matGrayImage = new Mat();

        // Convert colors to grayscale and equalize histogram (normalize)
        opencv_imgproc.cvtColor(matSourceImage, matGrayImage, opencv_imgproc.COLOR_BGR2GRAY);
        opencv_imgproc.equalizeHist(matGrayImage, matGrayImage);

        return matGrayImage;
    }

    private static Mat getDetectedFaceOnMat(Mat matImage) {
        RectVector detectedFaces = new RectVector();
        cascadeClassifier.detectMultiScale(matImage, detectedFaces);

        return getDetectedFaceFromRectVector(matImage, detectedFaces);
    }

    private static Mat getDetectedFaceOnMat(Mat matImage, double startSizeScale) {
        RectVector detectedFaces = new RectVector();
        Size minSize = new Size((int)(matImage.arrayHeight() * startSizeScale));
        Size maxSize = matImage.size();
        cascadeClassifier.detectMultiScale(
                matImage,
                detectedFaces,
                DEFAULT_SCALE_RATE,
                MIN_NEIGHBOURS,
                FLAGS,
                minSize,
                maxSize
        );

        return getDetectedFaceFromRectVector(matImage, detectedFaces);
    }

    private static Mat getDetectedFaceFromRectVector(Mat matImage, RectVector detectedFaces) {
        if (detectedFaces.empty()) {
            return null;
        } else {
            Mat detectedFace = new Mat();
            opencv_imgproc.resize(
                    new Mat(matImage, detectedFaces.get(0)),
                    detectedFace,
                    new Size(DETECTED_FACE_OUTPUT_SIZE_PX, DETECTED_FACE_OUTPUT_SIZE_PX)
            );
            return detectedFace;
        }
    }
}

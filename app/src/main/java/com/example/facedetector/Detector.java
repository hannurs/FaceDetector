package com.example.facedetector;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.util.ArrayList;
import java.util.List;

abstract public class Detector {

    CascadeClassifier faceDetector = new CascadeClassifier();
    CascadeClassifier eyeDetector = new CascadeClassifier();
    CascadeClassifier smileDetector = new CascadeClassifier();

    public Detector() {
    }

    public MatOfRect detectFaces(Mat frame) {
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(frame, faceDetections);

        return faceDetections;
    }

    public MatOfRect filterEyes(MatOfRect eyes, MatOfRect faces) {
        MatOfRect filteredEyes = new MatOfRect();
        List<Rect> eyesArray = new ArrayList<>();

        for (Rect face : faces.toArray()) {
            for (Rect eye : eyes.toArray()) {
                if (eye.y < face.y + face.height * 3 / 5 && eye.y > face.y
                    && eye.x > face.x && eye.x + eye.width < face.x + face.width) {
                    eyesArray.add(eye);
                }
            }
        }

        filteredEyes.fromList(eyesArray);

        return filteredEyes;
    }

    public MatOfRect detectEyes(Mat frame, MatOfRect faces) {
        MatOfRect eyeDetections = new MatOfRect();
        if (!eyeDetector.empty()) {
            eyeDetector.detectMultiScale(frame, eyeDetections);
        }

        MatOfRect filteredEyeDetections = filterEyes(eyeDetections, faces);

        return filteredEyeDetections;
    }

    public MatOfRect filterSmiles(MatOfRect smiles, MatOfRect faces) {
        MatOfRect filteredSmiles = new MatOfRect();
        List<Rect> smilesArray = new ArrayList<>();

        for (Rect face : faces.toArray()) {
            for (Rect smile : smiles.toArray()) {
                if (smile.y > face.y + face.height * 3 / 5 && smile.y + smile.height < face.y + face.height
                        && Math.abs((smile.x + smile.width / 2)) - (face.x + face.width / 2) < face.width / 10
                        && smile.x > face.x && smile.x + smile.width < face.x + face.width) {
                    smilesArray.add(smile);
                }
            }
        }

        filteredSmiles.fromList(smilesArray);

        return filteredSmiles;
    }

    public MatOfRect detectSmiles(Mat frame, MatOfRect faces) {
        MatOfRect smileDetections = new MatOfRect();
        if (!smileDetector.empty()) {
            smileDetector.detectMultiScale(frame, smileDetections);
        }

        MatOfRect filteredSmileDetections = filterSmiles(smileDetections, faces);

//        return smileDetections;
        return filteredSmileDetections;
    }
}

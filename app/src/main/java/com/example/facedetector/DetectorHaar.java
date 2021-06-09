package com.example.facedetector;

import android.app.Activity;
import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.objdetect.CascadeClassifier;



public class DetectorHaar extends Detector {

    private void loadFaceCascade(Context context) throws IOException {
        InputStream is = context.getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
        File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);

        File cascFile = new File(cascadeDir, "haarcascade_frontalface_alt2.xml");

        FileOutputStream fos = new FileOutputStream(cascFile);

        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = is.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
        is.close();
        fos.close();

        faceDetector = new CascadeClassifier(cascFile.getAbsolutePath());

        if (faceDetector.empty()) {
            faceDetector = null;
        }
        else {
            cascadeDir.delete();
        }
    }

    private void loadEyeCascade(Context context) throws IOException {
        InputStream is = context.getResources().openRawResource(R.raw.haarcascade_eye);
        File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);

        File cascFile = new File(cascadeDir, "haarcascade_eye.xml");

        FileOutputStream fos = new FileOutputStream(cascFile);

        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = is.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
        is.close();
        fos.close();

        eyeDetector = new CascadeClassifier(cascFile.getAbsolutePath());

        if (eyeDetector.empty()) {
            eyeDetector = null;
        }
        else {
            cascadeDir.delete();
        }
    }

    private void loadSmileCascade(Context context) throws IOException {
        InputStream is = context.getResources().openRawResource(R.raw.haarcascade_smile);
        File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);

        File cascFile = new File(cascadeDir, "haarcascade_smile.xml");

        FileOutputStream fos = new FileOutputStream(cascFile);

        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = is.read(buffer)) != -1) {
            fos.write(buffer, 0, bytesRead);
        }
        is.close();
        fos.close();

        smileDetector = new CascadeClassifier(cascFile.getAbsolutePath());

        if (smileDetector.empty()) {
            smileDetector = null;
        }
        else {
            cascadeDir.delete();
        }
    }

    public DetectorHaar(Context context) throws IOException {
        super();
        loadFaceCascade(context);
        loadEyeCascade(context);
        loadSmileCascade(context);
    }
}

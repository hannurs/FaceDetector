package com.example.facedetector;

import android.content.Context;

import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DetectorLBP extends Detector {

    private void loadFaceCascade(Context context) throws IOException {
        InputStream is = context.getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
        File cascadeDir = context.getDir("cascade", Context.MODE_PRIVATE);
        File cascFile = new File(cascadeDir, "lbpcascade_frontalface_improved.xml");

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


    public DetectorLBP(Context context) throws IOException {
        super();
        loadFaceCascade(context);
    }

}

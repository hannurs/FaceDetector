package com.example.facedetector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    JavaCameraView javaCameraView;
    Button buttonHaar;
    Button buttonLBP;
    boolean detectorOn;
    Context appContext;
    final int FRONT_CAMERA = 1;
    final int BACK_CAMERA = 0;
    int mCameraIndex;

//    File cascFile;

    private TextureView mTextureView;

//    CascadeClassifier faceDetector;
    Detector detector;
    private Mat mRgba, mGray, mRotatedRgba;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        javaCameraView = (JavaCameraView)findViewById(R.id.javaCamView);
        buttonHaar = (Button)findViewById(R.id.buttonHaar);
        buttonHaar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    detector = new DetectorHaar(appContext);
                    detectorOn = true;
                    buttonHaar.setBackgroundColor(Color.GREEN);
                    buttonLBP.setBackgroundColor(Color.RED);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonLBP = (Button)findViewById(R.id.buttonLBP);
        buttonLBP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    detector = new DetectorLBP(appContext);
                    detectorOn = true;
                    buttonHaar.setBackgroundColor(Color.RED);
                    buttonLBP.setBackgroundColor(Color.GREEN);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, baseCallback);
        }
        else {
            try {
                baseCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        javaCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        mGray = new Mat();
        mRotatedRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mRotatedRgba.release();
    }

    Point fixPoint(Point p) {
        double center_x = mRgba.cols() / 2.0;
        double center_y = mRgba.rows() / 2.0;
        double radians = 0;
        Point fixed = new Point(0, 0);
        if (mCameraIndex == FRONT_CAMERA) {
            radians = Math.toRadians(90);
            fixed = new Point(((p.x - center_x) * Math.cos(radians)) - ((p.y - center_y) * Math.sin(radians)) + center_y * 27 / 16,
//                    fixed = new Point(((p.x - center_x) * Math.cos(radians)) - ((p.y - center_y) * Math.sin(radians)) + center_y * 13 / 8,
                    ((p.x - center_x) * Math.sin(radians)) + ((p.y - center_y) * Math.cos(radians)) + center_x);
        }
        else if (mCameraIndex == BACK_CAMERA) {
            radians = Math.toRadians(-90);
            fixed = new Point(((p.x - center_x) * Math.cos(radians)) - ((p.y - center_y) * Math.sin(radians)) + center_y,
                    ((p.x - center_x) * Math.sin(radians)) + ((p.y - center_y) * Math.cos(radians)) + center_x / 2);
        }


        return fixed;
    }

    public void drawFaceRectangles(MatOfRect faceDetections) {

        for (Rect rect: faceDetections.toArray()) {
            Point p1 = new Point(rect.x, rect.y);
            Point p2 = new Point(rect.x + rect.width, rect.y + rect.height);

            Point rotatedp1 = fixPoint(p1);
            Point rotatedp2 = fixPoint(p2);

            Imgproc.rectangle(mRgba, rotatedp1, rotatedp2, new Scalar(255, 0, 0), 5);
//            Imgproc.rectangle(mRgba, p1, p2, new Scalar(255, 0, 0), 5);
        }
    }

    public void drawEyeRectangles(MatOfRect eyeDetections) {
        for (Rect rect: eyeDetections.toArray()) {
            Point p1 = new Point(rect.x, rect.y);
            Point p2 = new Point(rect.x + rect.width, rect.y + rect.height);

            Point rotatedp1 = fixPoint(p1);
            Point rotatedp2 = fixPoint(p2);

            Imgproc.rectangle(mRgba, rotatedp1, rotatedp2, new Scalar(0, 255, 0), 5);
        }
    }

    public void drawSmileRectangles(MatOfRect smileDetections) {
        for (Rect rect: smileDetections.toArray()) {
            Point p1 = new Point(rect.x, rect.y);
            Point p2 = new Point(rect.x + rect.width, rect.y + rect.height);

            Point rotatedp1 = fixPoint(p1);
            Point rotatedp2 = fixPoint(p2);

            Imgproc.rectangle(mRgba, rotatedp1, rotatedp2, new Scalar(0, 0, 255), 5);
        }
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        if (mCameraIndex == FRONT_CAMERA) {
            Core.rotate(mRgba, mRotatedRgba, Core.ROTATE_90_COUNTERCLOCKWISE);
        }
        else if (mCameraIndex == BACK_CAMERA)
        {
            Core.rotate(mRgba, mRotatedRgba, Core.ROTATE_90_CLOCKWISE);
        }

        mGray = inputFrame.gray();

        if (detectorOn) {
            // detect face
            MatOfRect faceDetections = detector.detectFaces(mRotatedRgba);
            drawFaceRectangles(faceDetections);

            MatOfRect eyeDetections = detector.detectEyes(mRotatedRgba, faceDetections);
            drawEyeRectangles(eyeDetections);
//
            MatOfRect smileDetections = detector.detectSmiles(mRotatedRgba, faceDetections);
            drawSmileRectangles(smileDetections);
        }

        return mRgba;
    }

    private BaseLoaderCallback baseCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) throws IOException {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    appContext = this.mAppContext;
                    detectorOn = false;
                    mCameraIndex = FRONT_CAMERA;
//                    mCameraIndex = BACK_CAMERA;
                    javaCameraView.setCameraIndex(mCameraIndex);

                    javaCameraView.enableView();
                }
                break;

                default:
                {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };
}
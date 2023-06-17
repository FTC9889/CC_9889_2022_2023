package com.team9889.lib.detectors;

import android.util.Log;

import com.acmerobotics.dashboard.config.Config;
import com.team9889.ftc2021.auto.AutoModeBase;
import com.team9889.ftc2021.subsystems.Robot;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

/**
 * Created by edm11 on 6/25/2022.
 */

@Config
public class ScanForSignal extends OpenCvPipeline {
    public int height = 15, width = 10, x = 172, y = 90, blackY = 96;
    public static boolean drawRect = true;

    public boolean left = false;

    public static Scalar blue = new Scalar(35, 60, 140), red = new Scalar(182, 78, 92),
            green = new Scalar(0, 0, 0), black = new Scalar(15, 23, 24);

    Scalar rgb = new Scalar(0, 0, 0), blackRGB = new Scalar(0, 0, 0);
    public double average = 0;

    public Scalar getRGB() {
        return rgb;
    }

    public Scalar getBlackRGB() {
        return blackRGB;
    }
    public double getBlackAverage() {return (blackRGB.val[0] + blackRGB.val[1] + blackRGB.val[2]) / 3;}

    public int getSignal() {
//        if (Math.abs(rgb.val[0] - (blue.val[0] - (black.val[0] - blackRGB.val[0]))) < 30
//                && Math.abs(rgb.val[1] - (blue.val[1] - (black.val[1] - blackRGB.val[1]))) < 30
//                && Math.abs(rgb.val[2] - (blue.val[2] - (black.val[2] - blackRGB.val[2]))) < 30) {
//            return 1;
//        } else if (Math.abs(rgb.val[0] - (red.val[0] - (black.val[0] - blackRGB.val[0]))) < 30
//                && Math.abs(rgb.val[1] - (red.val[1] - (black.val[1] - blackRGB.val[1]))) < 30
//                && Math.abs(rgb.val[2] - (red.val[2] - (black.val[2] - blackRGB.val[2]))) < 30) {
//            return 2;
//        } else {
//            return 3;
//        }


        if (rgb.val[1] > (rgb.val[0] + rgb.val[2]) / 2) {
            return 3;
        } else if (rgb.val[2] > (rgb.val[0] + rgb.val[1]) / 2) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public Mat processFrame(Mat input) {
        int x = this.x + (left ? -35 : 0);

        Rect color = new Rect(x, y, width, height);
        rgb = calculateAverageRGBInRoi(input, color);
        Imgproc.rectangle(input, new Rect(x, y, width, height), new Scalar(255, 0, 0));

        Rect blackRect = new Rect(x, blackY, width, height);
        blackRGB = calculateAverageRGBInRoi(input, blackRect);

        if (drawRect) {
            Imgproc.rectangle(input, new Point(0, 0), new Point(input.width(), y), rgb, -1);
            Imgproc.rectangle(input, new Point(0, y + height), new Point(input.width(), input.height()), rgb, -1);
            Imgproc.rectangle(input, new Point(0, 0), new Point(x, input.height()), rgb, -1);
            Imgproc.rectangle(input, new Point(x + width, 0), new Point(input.width(), input.height()), rgb, -1);
        }

        return input;
    }


    private Scalar calculateAverageRGBInRoi(Mat mat, Rect roi) {
        Mat image = new Mat(mat, roi);

        Scalar avgRGB = Core.mean(image);

        for (int i = 0; i < 3; i++) {
            avgRGB.val[i] = Math.round(avgRGB.val[i]);
        }

        return avgRGB;
    }

    private boolean isScalarInThreshold(Scalar scalar, Scalar thresholdLow, Scalar thresholdHigh) {
        double red = scalar.val[0];
        double green = scalar.val[1];
        double blue = scalar.val[2];
        return thresholdLow.val[0] < red && thresholdHigh.val[0] > red
                && thresholdLow.val[1] < green && thresholdHigh.val[1] > green
                && thresholdLow.val[2] < blue && thresholdHigh.val[2] > blue;
    }
}

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
    public static int height = 15, width = 15, x = 60, y = 160, tolerance = 10;
    public static boolean drawRect = true;

    public static int oneR = 0, oneG = 0, oneB = 0;
    public static int twoR = 0, twoG = 0, twoB = 0;

    Scalar rgb = new Scalar(0, 0, 0);

    public Scalar getRGB() {
        return rgb;
    }

    public int getSignal() {
        if (Math.abs(rgb.val[0] - oneR) < tolerance
                && Math.abs(rgb.val[1] - oneG) < tolerance
                && Math.abs(rgb.val[2] - oneB) < tolerance) {
            return 1;
        } else if (Math.abs(rgb.val[0] - twoR) < tolerance
                && Math.abs(rgb.val[1] - twoG) < tolerance
                && Math.abs(rgb.val[2] - twoB) < tolerance) {
            return 2;
        } else {
            return 3;
        }
    }

    @Override
    public Mat processFrame(Mat input) {
        Rect redLeft = new Rect(x, y, width, height);

        rgb = calculateAverageRGBInRoi(input, redLeft);


        if (drawRect) {
            Imgproc.rectangle(input, new Rect(x, y, width, height), new Scalar(255, 0, 0));
//            Imgproc.rectangle(input, redLeft, leftRGB);
        }

        Imgproc.rectangle(input, new Point(0, 0), new Point(input.width(), y), rgb, -1);
        Imgproc.rectangle(input, new Point(0, y + height), new Point(input.width(), input.height()), rgb, -1);
        Imgproc.rectangle(input, new Point(0, 0), new Point(x, input.height()), rgb, -1);
        Imgproc.rectangle(input, new Point(x + width, 0), new Point(input.width(), input.height()), rgb, -1);

        return input;
    }


    private Scalar calculateAverageRGBInRoi(Mat mat, Rect roi) {
        Mat image = new Mat(mat, roi);

        Scalar avgRGB = Core.mean(image);

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

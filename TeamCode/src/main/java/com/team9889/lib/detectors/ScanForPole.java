package com.team9889.lib.detectors;

import android.util.Log;

import com.acmerobotics.dashboard.config.Config;
import com.team9889.ftc2021.Team9889Linear;
import com.team9889.ftc2021.subsystems.Robot;
import com.team9889.lib.detectors.util.HSV;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Rect2d;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshua9889 on 11/25/2019.
 */

@Config
public class ScanForPole extends OpenCvPipeline {
    public static int area = 0, maxArea = 10000000, minRation = 0, maxRation = 1;

    public double width;

    //Outputs
    private Mat cvResizeOutput = new Mat();
    private Mat blurOutput = new Mat();
    private Mat hsvThresholdOutput = new Mat();
    private ArrayList<MatOfPoint> findContoursOutput = new ArrayList<MatOfPoint>();
    private ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();

    // With LEDs
    public static HSV poleHSV = new HSV(60, 100,
            100, 255, 200, 255);

    // Without LEDs
//    public static HSV poleHSV = new HSV(90, 100,
//            100, 255, 100, 255);

    private Point point = new Point(1e10, 1e10);

    public Point getPoint() {
        return point;
    }

    public ScanForPole() {

    }

    @Override
    public Mat processFrame(Mat input) {
        if (Team9889Linear.useLEDs) {
            poleHSV = new HSV(60, 100,
                    100, 255, 200, 255);
        } else {
            poleHSV = new HSV(90, 100,
                    100, 255, 100, 255);
        }

        // Step CV_resize0:
        Mat cvResizeSrc = input;
        Size cvResizeDsize = new Size(0, 0);
        double cvResizeFx = 1;
        double cvResizeFy = 1;
        int cvResizeInterpolation = Imgproc.INTER_LINEAR;
        cvResize(cvResizeSrc, cvResizeDsize, cvResizeFx, cvResizeFy, cvResizeInterpolation, cvResizeOutput);

//        Imgproc.rectangle(cvResizeOutput, new Rect(0, 190, 320, 100), new Scalar(0, 0, 0), -1);

        // Step HSV_Threshold0:
        Mat hsvThresholdInput = cvResizeOutput;
        hsvThreshold(hsvThresholdInput, poleHSV.getH(), poleHSV.getS(), poleHSV.getV(), hsvThresholdOutput);

        // Step Find_Contours0:
        Mat findContoursInput = hsvThresholdOutput;
        boolean findContoursExternalOnly = false;
        findContours(findContoursInput, findContoursExternalOnly, findContoursOutput);

        // Step Filter_Contours0:
        ArrayList<MatOfPoint> filterContoursContours = findContoursOutput;
        double filterContoursMinArea = area;
        double filterContoursMaxArea = maxArea;
        double filterContoursMinPerimeter = 0;
        double filterContoursMinWidth = 0;
        double filterContoursMaxWidth = 10000;
        double filterContoursMinHeight = 0;
        double filterContoursMaxHeight = 10000;
        double[] filterContoursSolidity = {0, 100};
        double filterContoursMaxVertices = 1000000;
        double filterContoursMinVertices = 0;
        double filterContoursMinRatio = minRation;
        double filterContoursMaxRatio = maxRation;
        filterContours(filterContoursContours, filterContoursMinArea, filterContoursMaxArea, filterContoursMinPerimeter, filterContoursMinWidth, filterContoursMaxWidth, filterContoursMinHeight, filterContoursMaxHeight, filterContoursSolidity, filterContoursMaxVertices, filterContoursMinVertices, filterContoursMinRatio, filterContoursMaxRatio, contours);


        List<Moments> mu = new ArrayList<>();

        for (int i = 0; i < contours.size(); i++) {
            mu.add(Imgproc.moments(contours.get(i)));
        }

        List<Point> mc = new ArrayList<>();
        for (int i = 0; i < contours.size(); i++) {
            mc.add(new Point(mu.get(i).m10 / mu.get(i).m00 + 1e-5, mu.get(i).m01 / mu.get(i).m00 + 1e-5));
        }

        int index = 0;
        for (int i = 0; i < contours.size(); i++) {
            if (Imgproc.boundingRect(contours.get(i)).width > Imgproc.boundingRect(contours.get(index)).width) {
                index = i;
            }
        }

        if (contours.size() > 0) {
            RotatedRect rect = Imgproc.minAreaRect(new MatOfPoint2f(contours.get(index).toArray()));
            Point[] points = new Point[4];
            rect.points(points);

            Point corner1 = new Point(1000, 1000), corner2 = new Point(1000, 1000);
            for (int i = 0; i < 4; i++) {
                Imgproc.line(cvResizeOutput, points[i], points[(i+1)%4], new Scalar(0, 255, 0));
                if (corner1.y > points[i].y) {
                    corner2 = corner1;
                    corner1 = points[i];
                } else if (corner2.y > points[i].y) {
                    corner2 = points[i];
                }
            }

            Imgproc.line(cvResizeOutput, corner1, corner2, new Scalar(255, 0, 0));

            double angle = Math.toRadians(rect.angle > 45 ? rect.angle - 90 : rect.angle);
            width = Math.cos(angle) * rect.boundingRect().width
            - Math.abs(Math.sin(angle) * rect.boundingRect().height);
            Log.v("Angle", Math.toDegrees(angle) + ", " + rect.boundingRect().width + ", " + width);


            point = mc.get(index);
            point.x = point.x + (Math.tan(angle) * point.y);
            point.y = (corner1.y + corner2.y) / 2;
            Imgproc.circle(cvResizeOutput, point, 4, new Scalar(255, 0, 0), -1);
        }





//        cvResize(cvResizeOutput, cvResizeDsize, 3, 3, cvResizeInterpolation, cvResizeOutput);

        return cvResizeOutput;
    }

    /**
     * This method is a generated getter for the output of a CV_resize.
     * @return Mat output from CV_resize.
     */
    public Mat cvResizeOutput() {
        return cvResizeOutput;
    }

    /**
     * This method is a generated getter for the output of a Blur.
     * @return Mat output from Blur.
     */
    public Mat blurOutput() {
        return blurOutput;
    }

    /**
     * This method is a generated getter for the output of a HSV_Threshold.
     * @return Mat output from HSV_Threshold.
     */
    public Mat hsvThresholdOutput() {
        return hsvThresholdOutput;
    }

    /**
     * This method is a generated getter for the output of a Find_Contours.
     * @return ArrayList<MatOfPoint> output from Find_Contours.
     */
    public ArrayList<MatOfPoint> findContoursOutput() {
        return findContoursOutput;
    }

    /**
     * This method is a generated getter for the output of a Filter_Contours.
     * @return ArrayList<MatOfPoint> output from Filter_Contours.
     */
    public ArrayList<MatOfPoint> filterContoursOutput() {
        return contours;
    }


    /**
     * Resizes an image.
     * @param src The image to resize.
     * @param dSize size to set the image.
     * @param fx scale factor along X axis.
     * @param fy scale factor along Y axis.
     * @param interpolation type of interpolation to use.
     * @param dst output image.
     */
    private void cvResize(Mat src, Size dSize, double fx, double fy, int interpolation,
                          Mat dst) {
        if (dSize==null) {
            dSize = new Size(0,0);
        }
        Imgproc.resize(src, dst, dSize, fx, fy, interpolation);
    }

    /**
     * An indication of which type of filter to use for a blur.
     * Choices are BOX, GAUSSIAN, MEDIAN, and BILATERAL
     */
    enum BlurType{
        BOX("Box Blur"), GAUSSIAN("Gaussian Blur"), MEDIAN("Median Filter"),
        BILATERAL("Bilateral Filter");

        private final String label;

        BlurType(String label) {
            this.label = label;
        }

        public static BlurType get(String type) {
            if (BILATERAL.label.equals(type)) {
                return BILATERAL;
            }
            else if (GAUSSIAN.label.equals(type)) {
                return GAUSSIAN;
            }
            else if (MEDIAN.label.equals(type)) {
                return MEDIAN;
            }
            else {
                return BOX;
            }
        }

        @Override
        public String toString() {
            return this.label;
        }
    }

    /**
     * Softens an image using one of several filters.
     * @param input The image on which to perform the blur.
     * @param type The blurType to perform.
     * @param doubleRadius The radius for the blur.
     * @param output The image in which to store the output.
     */
    private void blur(Mat input, BlurType type, double doubleRadius,
                      Mat output) {
        int radius = (int)(doubleRadius + 0.5);
        int kernelSize;
        switch(type){
            case BOX:
                kernelSize = 2 * radius + 1;
                Imgproc.blur(input, output, new Size(kernelSize, kernelSize));
                break;
            case GAUSSIAN:
                kernelSize = 6 * radius + 1;
                Imgproc.GaussianBlur(input,output, new Size(kernelSize, kernelSize), radius);
                break;
            case MEDIAN:
                kernelSize = 2 * radius + 1;
                Imgproc.medianBlur(input, output, kernelSize);
                break;
            case BILATERAL:
                Imgproc.bilateralFilter(input, output, -1, radius, radius);
                break;
        }
    }

    /**
     * Segment an image based on hue, saturation, and value ranges.
     *
     * @param input The image on which to perform the HSL threshold.
     * @param hue The min and max hue
     * @param sat The min and max saturation
     * @param val The min and max value
     */
    private void hsvThreshold(Mat input, double[] hue, double[] sat, double[] val,
                              Mat out) {
        Imgproc.cvtColor(input, out, Imgproc.COLOR_BGR2HSV);
        Core.inRange(out, new Scalar(hue[0], sat[0], val[0]),
                new Scalar(hue[1], sat[1], val[1]), out);
    }

    private void findContours(Mat input, boolean externalOnly,
                              List<MatOfPoint> contours) {
        Mat hierarchy = new Mat();
        contours.clear();
        int mode;
        if (externalOnly) {
            mode = Imgproc.RETR_EXTERNAL;
        }
        else {
            mode = Imgproc.RETR_LIST;
        }
        int method = Imgproc.CHAIN_APPROX_SIMPLE;
        Imgproc.findContours(input, contours, hierarchy, mode, method);
    }


    /**
     * Filters out contours that do not meet certain criteria.
     * @param inputContours is the input list of contours
     * @param output is the the output list of contours
     * @param minArea is the minimum area of a contour that will be kept
     * @param minPerimeter is the minimum perimeter of a contour that will be kept
     * @param minWidth minimum width of a contour
     * @param maxWidth maximum width
     * @param minHeight minimum height
     * @param maxHeight maximimum height
     * @param minVertexCount minimum vertex Count of the contours
     * @param maxVertexCount maximum vertex Count
     * @param minRatio minimum ratio of width to height
     * @param maxRatio maximum ratio of width to height
     */
    private void filterContours(List<MatOfPoint> inputContours, double minArea, double maxArea,
                                double minPerimeter, double minWidth, double maxWidth, double minHeight, double
                                        maxHeight, double[] solidity, double maxVertexCount, double minVertexCount, double
                                        minRatio, double maxRatio, List<MatOfPoint> output) {
        final MatOfInt hull = new MatOfInt();
        output.clear();
        //operation
        for (int i = 0; i < inputContours.size(); i++) {
            final MatOfPoint contour = inputContours.get(i);
            final Rect bb = Imgproc.boundingRect(contour);
            if (bb.width < minWidth || bb.width > maxWidth) continue;
            if (bb.height < minHeight || bb.height > maxHeight) continue;
            final double area = Imgproc.contourArea(contour);
            if (area < minArea || area > maxArea) continue;
            if (Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true) < minPerimeter) continue;
            Imgproc.convexHull(contour, hull);
            MatOfPoint mopHull = new MatOfPoint();
            mopHull.create((int) hull.size().height, 1, CvType.CV_32SC2);
            for (int j = 0; j < hull.size().height; j++) {
                int index = (int)hull.get(j, 0)[0];
                double[] point = new double[] { contour.get(index, 0)[0], contour.get(index, 0)[1]};
                mopHull.put(j, 0, point);
            }
            final double solid = 100 * area / Imgproc.contourArea(mopHull);
            if (solid < solidity[0] || solid > solidity[1]) continue;
            if (contour.rows() < minVertexCount || contour.rows() > maxVertexCount)	continue;
            final double ratio = bb.width / (double)bb.height;
            if (ratio < minRatio || ratio > maxRatio) continue;
            output.add(contour);
        }
    }
}

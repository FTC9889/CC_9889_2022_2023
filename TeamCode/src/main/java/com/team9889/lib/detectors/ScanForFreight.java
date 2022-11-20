//package com.team9889.lib.detectors;
//
//import com.acmerobotics.dashboard.config.Config;
//import com.team9889.lib.detectors.util.HSV;
//
//import org.opencv.core.Core;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfInt;
//import org.opencv.core.MatOfPoint;
//import org.opencv.core.MatOfPoint2f;
//import org.opencv.core.Rect;
//import org.opencv.core.Scalar;
//import org.opencv.core.Size;
//import org.opencv.imgproc.Imgproc;
//import org.openftc.easyopencv.OpenCvPipeline;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Eric on 12/18/2021.
// */
//
//@Config
//public class ScanForFreight extends OpenCvPipeline {
//
//    //Outputs
//    private Mat cvResizeOutput = new Mat();
//    private Mat blurOutput = new Mat();
//    private Mat hsvThreshold0Output = new Mat();
//    private Mat hsvThreshold1Output = new Mat();
//    private Mat cvAddOutput = new Mat();
//    private ArrayList<MatOfPoint> findContoursOutput = new ArrayList<MatOfPoint>();
//    private ArrayList<MatOfPoint> filterContoursOutput = new ArrayList<MatOfPoint>();
//
//    public static HSV goldHSV = new HSV(0, 180, 0, 255, 0, 255);
//    public static HSV silverHSV = new HSV(0, 180, 0, 255, 0, 255);
//
//    /**
//     * This is the primary method that runs the entire pipeline and updates the outputs.
//     */
//    @Override
//    public Mat processFrame(Mat input) {
//        // Step CV_resize0:
//        Mat cvResizeSrc = input;
//        Size cvResizeDsize = new Size(0, 0);
//        double cvResizeFx = 0.05;
//        double cvResizeFy = 0.05;
//        int cvResizeInterpolation = Imgproc.INTER_LINEAR;
//        cvResize(cvResizeSrc, cvResizeDsize, cvResizeFx, cvResizeFy, cvResizeInterpolation, cvResizeOutput);
//
//        // Step Blur0:
//        Mat blurInput = cvResizeOutput;
//        BlurType blurType = BlurType.get("Box Blur");
//        double blurRadius = 3.603603603603604;
//        blur(blurInput, blurType, blurRadius, blurOutput);
//
//        // Step HSV_Threshold0:
//        Mat hsvThreshold0Input = blurOutput;
//        hsvThreshold(hsvThreshold0Input, goldHSV.getH(), goldHSV.getS(), goldHSV.getV(), hsvThreshold0Output);
//
//        // Step HSV_Threshold1:
//        Mat hsvThreshold1Input = blurOutput;
//        hsvThreshold(hsvThreshold1Input, silverHSV.getH(), silverHSV.getS(), silverHSV.getV(), hsvThreshold1Output);
//
//        // Step CV_add0:
//        Mat cvAddSrc1 = hsvThreshold0Output;
//        Mat cvAddSrc2 = hsvThreshold1Output;
//        cvAdd(cvAddSrc1, cvAddSrc2, cvAddOutput);
//
//        // Step Find_Contours0:
//        Mat findContoursInput = cvAddOutput;
//        boolean findContoursExternalOnly = true;
//        findContours(findContoursInput, findContoursExternalOnly, findContoursOutput);
//
//        // Step Filter_Contours0:
//        ArrayList<MatOfPoint> filterContoursContours = findContoursOutput;
//        double filterContoursMinArea = 30.0;
//        double filterContoursMinPerimeter = 25.0;
//        double filterContoursMinWidth = 0;
//        double filterContoursMaxWidth = 1000;
//        double filterContoursMinHeight = 0;
//        double filterContoursMaxHeight = 1000;
//        double[] filterContoursSolidity = {0, 100};
//        double filterContoursMaxVertices = 1000000;
//        double filterContoursMinVertices = 0;
//        double filterContoursMinRatio = 0;
//        double filterContoursMaxRatio = 1000;
//        filterContours(filterContoursContours, filterContoursMinArea, filterContoursMinPerimeter, filterContoursMinWidth, filterContoursMaxWidth, filterContoursMinHeight, filterContoursMaxHeight, filterContoursSolidity, filterContoursMaxVertices, filterContoursMinVertices, filterContoursMinRatio, filterContoursMaxRatio, filterContoursOutput);
//
//        for (int i = 0; i < filterContoursOutput.size(); i++) {
//            Imgproc.drawContours(cvResizeOutput, filterContoursOutput, i, new Scalar(255, 0, 0));
//        }
//
//        return cvResizeOutput;
//    }
//
//    /**
//     * This method is a generated getter for the output of a CV_resize.
//     * @return Mat output from CV_resize.
//     */
//    public Mat cvResizeOutput() {
//        return cvResizeOutput;
//    }
//
//    /**
//     * This method is a generated getter for the output of a Blur.
//     * @return Mat output from Blur.
//     */
//    public Mat blurOutput() {
//        return blurOutput;
//    }
//
//    /**
//     * This method is a generated getter for the output of a HSV_Threshold.
//     * @return Mat output from HSV_Threshold.
//     */
//    public Mat hsvThreshold0Output() {
//        return hsvThreshold0Output;
//    }
//
//    /**
//     * This method is a generated getter for the output of a HSV_Threshold.
//     * @return Mat output from HSV_Threshold.
//     */
//    public Mat hsvThreshold1Output() {
//        return hsvThreshold1Output;
//    }
//
//    /**
//     * This method is a generated getter for the output of a CV_add.
//     * @return Mat output from CV_add.
//     */
//    public Mat cvAddOutput() {
//        return cvAddOutput;
//    }
//
//    /**
//     * This method is a generated getter for the output of a Find_Contours.
//     * @return ArrayList<MatOfPoint> output from Find_Contours.
//     */
//    public ArrayList<MatOfPoint> findContoursOutput() {
//        return findContoursOutput;
//    }
//
//    /**
//     * This method is a generated getter for the output of a Filter_Contours.
//     * @return ArrayList<MatOfPoint> output from Filter_Contours.
//     */
//    public ArrayList<MatOfPoint> filterContoursOutput() {
//        return filterContoursOutput;
//    }
//
//
//    /**
//     * Resizes an image.
//     * @param src The image to resize.
//     * @param dSize size to set the image.
//     * @param fx scale factor along X axis.
//     * @param fy scale factor along Y axis.
//     * @param interpolation type of interpolation to use.
//     * @param dst output image.
//     */
//    private void cvResize(Mat src, Size dSize, double fx, double fy, int interpolation,
//                          Mat dst) {
//        if (dSize==null) {
//            dSize = new Size(0,0);
//        }
//        Imgproc.resize(src, dst, dSize, fx, fy, interpolation);
//    }
//
//
//    /**
//     * An indication of which type of filter to use for a blur.
//     * Choices are BOX, GAUSSIAN, MEDIAN, and BILATERAL
//     */
//    enum BlurType{
//        BOX("Box Blur"), GAUSSIAN("Gaussian Blur"), MEDIAN("Median Filter"),
//        BILATERAL("Bilateral Filter");
//
//        private final String label;
//
//        BlurType(String label) {
//            this.label = label;
//        }
//
//        public static BlurType get(String type) {
//            if (BILATERAL.label.equals(type)) {
//                return BILATERAL;
//            }
//            else if (GAUSSIAN.label.equals(type)) {
//                return GAUSSIAN;
//            }
//            else if (MEDIAN.label.equals(type)) {
//                return MEDIAN;
//            }
//            else {
//                return BOX;
//            }
//        }
//
//        @Override
//        public String toString() {
//            return this.label;
//        }
//    }
//
//    /**
//     * Softens an image using one of several filters.
//     * @param input The image on which to perform the blur.
//     * @param type The blurType to perform.
//     * @param doubleRadius The radius for the blur.
//     * @param output The image in which to store the output.
//     */
//    private void blur(Mat input, BlurType type, double doubleRadius,
//                      Mat output) {
//        int radius = (int)(doubleRadius + 0.5);
//        int kernelSize;
//        switch(type){
//            case BOX:
//                kernelSize = 2 * radius + 1;
//                Imgproc.blur(input, output, new Size(kernelSize, kernelSize));
//                break;
//            case GAUSSIAN:
//                kernelSize = 6 * radius + 1;
//                Imgproc.GaussianBlur(input,output, new Size(kernelSize, kernelSize), radius);
//                break;
//            case MEDIAN:
//                kernelSize = 2 * radius + 1;
//                Imgproc.medianBlur(input, output, kernelSize);
//                break;
//            case BILATERAL:
//                Imgproc.bilateralFilter(input, output, -1, radius, radius);
//                break;
//        }
//    }
//
//    /**
//     * Segment an image based on hue, saturation, and value ranges.
//     *
//     * @param input The image on which to perform the HSL threshold.
//     * @param hue The min and max hue
//     * @param sat The min and max saturation
//     * @param val The min and max value
////     * @param output The image in which to store the output.
//     */
//    private void hsvThreshold(Mat input, double[] hue, double[] sat, double[] val,
//                              Mat out) {
//        Imgproc.cvtColor(input, out, Imgproc.COLOR_BGR2HSV);
//        Core.inRange(out, new Scalar(hue[0], sat[0], val[0]),
//                new Scalar(hue[1], sat[1], val[1]), out);
//    }
//
//    /**
//     * Calculates the sum of two Mats.
//     * @param src1 the first Mat
//     * @param src2 the second Mat
//     * @param out the Mat that is the sum of the two Mats
//     */
//    private void cvAdd(Mat src1, Mat src2, Mat out) {
//        Core.add(src1, src2, out);
//    }
//
//    /**
//     * Sets the values of pixels in a binary image to their distance to the nearest black pixel.
//     * @param input The image on which to perform the Distance Transform.
////     * @param type The Transform.
////     * @param maskSize the size of the mask.
////     * @param output The image in which to store the output.
//     */
//    private void findContours(Mat input, boolean externalOnly,
//                              List<MatOfPoint> contours) {
//        Mat hierarchy = new Mat();
//        contours.clear();
//        int mode;
//        if (externalOnly) {
//            mode = Imgproc.RETR_EXTERNAL;
//        }
//        else {
//            mode = Imgproc.RETR_LIST;
//        }
//        int method = Imgproc.CHAIN_APPROX_SIMPLE;
//        Imgproc.findContours(input, contours, hierarchy, mode, method);
//    }
//
//
//    /**
//     * Filters out contours that do not meet certain criteria.
//     * @param inputContours is the input list of contours
//     * @param output is the the output list of contours
//     * @param minArea is the minimum area of a contour that will be kept
//     * @param minPerimeter is the minimum perimeter of a contour that will be kept
//     * @param minWidth minimum width of a contour
//     * @param maxWidth maximum width
//     * @param minHeight minimum height
//     * @param maxHeight maximimum height
////     * @param Solidity the minimum and maximum solidity of a contour
//     * @param minVertexCount minimum vertex Count of the contours
//     * @param maxVertexCount maximum vertex Count
//     * @param minRatio minimum ratio of width to height
//     * @param maxRatio maximum ratio of width to height
//     */
//    private void filterContours(List<MatOfPoint> inputContours, double minArea,
//                                double minPerimeter, double minWidth, double maxWidth, double minHeight, double
//                                        maxHeight, double[] solidity, double maxVertexCount, double minVertexCount, double
//                                        minRatio, double maxRatio, List<MatOfPoint> output) {
//        final MatOfInt hull = new MatOfInt();
//        output.clear();
//        //operation
//        for (int i = 0; i < inputContours.size(); i++) {
//            final MatOfPoint contour = inputContours.get(i);
//            final Rect bb = Imgproc.boundingRect(contour);
//            if (bb.width < minWidth || bb.width > maxWidth) continue;
//            if (bb.height < minHeight || bb.height > maxHeight) continue;
//            final double area = Imgproc.contourArea(contour);
//            if (area < minArea) continue;
//            if (Imgproc.arcLength(new MatOfPoint2f(contour.toArray()), true) < minPerimeter) continue;
//            Imgproc.convexHull(contour, hull);
//            MatOfPoint mopHull = new MatOfPoint();
//            mopHull.create((int) hull.size().height, 1, CvType.CV_32SC2);
//            for (int j = 0; j < hull.size().height; j++) {
//                int index = (int)hull.get(j, 0)[0];
//                double[] point = new double[] { contour.get(index, 0)[0], contour.get(index, 0)[1]};
//                mopHull.put(j, 0, point);
//            }
//            final double solid = 100 * area / Imgproc.contourArea(mopHull);
//            if (solid < solidity[0] || solid > solidity[1]) continue;
//            if (contour.rows() < minVertexCount || contour.rows() > maxVertexCount)	continue;
//            final double ratio = bb.width / (double)bb.height;
//            if (ratio < minRatio || ratio > maxRatio) continue;
//            output.add(contour);
//        }
//    }
//}

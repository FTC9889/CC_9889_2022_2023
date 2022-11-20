package com.team9889.lib.detectors;

import com.acmerobotics.dashboard.config.Config;

import org.opencv.core.Mat;
import org.openftc.easyopencv.OpenCvPipeline;

/**
 * Created by joshua9889 on 11/25/2019.
 */

@Config
public class Blank extends OpenCvPipeline {
    public Blank() {

    }

    @Override
    public Mat processFrame(Mat input) {
        return input;
    }
}

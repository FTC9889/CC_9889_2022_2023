package com.team9889.lib.detectors.util;

/**
 * Created by Eric on 11/13/2021.
 */
public class HSV {
    public double hMin = 0, hMax = 0, sMin = 0, sMax = 0, vMin = 0, vMax = 0;

    public HSV (double hMin, double hMax, double sMin, double sMax, double vMin, double vMax) {
        this.hMin = hMin;
        this.hMax = hMax;
        this.sMin = sMin;
        this.sMax = sMax;
        this.vMin = vMin;
        this.vMax = vMax;
    }

    public double[] getH () {
        return new double[] {hMin, hMax};
    }

    public double[] getS () {
        return new double[] {sMin, sMax};
    }

    public double[] getV () {
        return new double[] {vMin, vMax};
    }
}

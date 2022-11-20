package com.team9889.lib.control.controllers;

public class FFFBMath {
    private double kV, kA, kS;

    public FFFBMath(double kV, double kA, double kS){
        this.kA = kA;
        this.kV = kV;
        this.kS = kS;
    }
    public double calculateFFFBGain(double targetVelo){
        double gain = kV*targetVelo + Math.signum(targetVelo)*kS;
        return gain;
    }
    public void FFConstants(double kV, double kA, double kS){
        this.kA = kA;
        this.kV = kV;
        this.kS = kS;
    }


}
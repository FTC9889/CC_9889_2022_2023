package com.team9889.lib.hardware;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * Created by Eric on 7/26/2019.
 */

public class Motor {
    public DcMotorEx motor;
    private int position, offsetPosition = 0, lastPosition = 0;
    private double currentPower = 0;
    private double velocity;
    private double ratio;
    public static int numHardwareUsesThisUpdate = 0;

    public Motor(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction,
                 DcMotor.ZeroPowerBehavior zeroPowerBehavior, boolean RunWithoutEncoder, boolean resetEncoder){
        this.motor = hardwareMap.get(DcMotorEx.class, id);
        numHardwareUsesThisUpdate ++;
        this.motor.setDirection(direction);
        this.motor.setZeroPowerBehavior(zeroPowerBehavior);
        if (RunWithoutEncoder)
            this.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        else
            this.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        if(resetEncoder){
            position = motor.getCurrentPosition();
            this.resetEncoder();
        }
    }

    public Motor(HardwareMap hardwareMap, String id, DcMotorSimple.Direction direction,
                 DcMotor.ZeroPowerBehavior zeroPowerBehavior, boolean RunWithoutEncoder, boolean resetEncoder, PIDFCoefficients pidf){
        this.motor = hardwareMap.get(DcMotorEx.class, id);
        numHardwareUsesThisUpdate ++;
        this.motor.setDirection(direction);
        this.motor.setZeroPowerBehavior(zeroPowerBehavior);
        if (RunWithoutEncoder)
            this.motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        else
            this.motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        if(resetEncoder){
            position = motor.getCurrentPosition();
            this.resetEncoder();
        }

        motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
    }

    public Motor(HardwareMap hardwareMap, String id) {
        this.motor = hardwareMap.get(DcMotorEx.class, id);
        numHardwareUsesThisUpdate ++;
    }

    public void setPower(double power){
        if (Math.abs(power - currentPower) > .005) {
            motor.setPower(power);
            currentPower = power;
            numHardwareUsesThisUpdate ++;

            Log.i("Power", "" + power);
        }
    }

    public void setPosition(double position) {
        motor.setTargetPosition((int) (position * 145.1) - offsetPosition);
        motor.setPower(1);
    }

    public int getPosition(){
        return position - offsetPosition;
    }

    public int getLastPosition(){
        return lastPosition - offsetPosition;
    }

    public int getDeltaPosition() { return getPosition() - getLastPosition(); }

    public double getVelocity(){
        return velocity;
    }

    public void update() {
        lastPosition = position;
        position = motor.getCurrentPosition();
        velocity = motor.getVelocity();
    }

    public void resetEncoder(){
        offsetPosition = position;
    }

    public void setRPM(double rpm) {
        motor.setVelocity(rpm, AngleUnit.DEGREES);
    }
}
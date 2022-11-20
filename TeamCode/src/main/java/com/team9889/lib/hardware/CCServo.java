package com.team9889.lib.hardware;

import android.util.Log;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.lib.CruiseLib;

/**
 * Created by Eric on 1/8/2022.
 */
public class CCServo {
    public Servo servo;
    ElapsedTime timer = new ElapsedTime();
    double position = 0, setPosition = 0, wantedPosition = 0, moveAmount = 0, speed = 0;
    private double range = 0, degreesPerSecond = 0;

    public CCServo(HardwareMap hardwareMap, String id, int range, int speed, Servo.Direction direction){
        this.servo = hardwareMap.get(Servo.class, id);
        this.range = range;
        this.speed = speed;
        this.degreesPerSecond = speed;
        this.servo.setDirection(direction);
    }

    public CCServo(HardwareMap hardwareMap, String id, int range, Servo.Direction direction){
        this.servo = hardwareMap.get(Servo.class, id);
        this.range = range;
        this.degreesPerSecond = 0;
        this.servo.setDirection(direction);
    }

    public CCServo(HardwareMap hardwareMap, String id, Servo.Direction direction){
        this.servo = hardwareMap.get(Servo.class, id);
        this.range = 180;
        this.degreesPerSecond = 0;
        this.servo.setDirection(direction);
    }

    public CCServo(HardwareMap hardwareMap, String id){
        this.servo = hardwareMap.get(Servo.class, id);
        this.range = 180;
        this.degreesPerSecond = 0;
        this.servo.setDirection(Servo.Direction.FORWARD);
    }

    public void setPosition(double position){
        wantedPosition = position;
        setPosition = position;
        moveAmount = position;
    }

    public void setPosition(double position, double time){
        double rate = speed / range;
        double error = position - this.position;
        double timeToMove = error / rate;
        moveAmount = timeToMove / time;
        wantedPosition = position;
    }

    public void setAngle(double angle){
        this.position = angle / range;
    }

    public double getPosition() {
        return position;
    }

    public double getAngle() {
        return position * range;
    }

    public void update(double loopTime) {
        if (position != setPosition) {
            double deltaAngle = CruiseLib.getSign(setPosition - position) * ((loopTime / 1000) * speed);
            position += deltaAngle / range;
        }

        if (setPosition != wantedPosition) {
            double seconds = loopTime / 1000;
            double amountToMove = seconds * (speed / range);
            setPosition += CruiseLib.getSign(wantedPosition - setPosition) * (amountToMove * Math.abs(moveAmount));
        }

        servo.setPosition(setPosition);

        Log.v("Set Position", "" + setPosition);
        Log.v("Wanted Position", "" + wantedPosition);
        Log.v("Position", "" + position);
    }
}

package com.team9889.lib.hardware;

import android.graphics.Color;
import android.os.Build;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import androidx.annotation.RequiresApi;

/**
 * Created by joshua9889 on 2/16/2018.
 *
 * Class to wrap the things into one class
 */

public class RevColorDistance{
    private ColorSensor sensorColor = null;
    private DistanceSensor sensorDistance = null;
    private HardwareMap hardwareMap = null;
    private String id = null;

    int color = 0;
    double dist = 0;

    ElapsedTime timer = new ElapsedTime();

    public RevColorDistance(String id, HardwareMap hardwareMap){
        this.hardwareMap = hardwareMap;
        this.id = id;

        if (hardwareMap != null) {
            if(sensorColor==null) {
                sensorColor = hardwareMap.get(ColorSensor.class, id);
                sensorColor.enableLed(false);
            }

            if(sensorDistance==null)
                sensorDistance = hardwareMap.get(DistanceSensor.class, id);

            RobotLog.d("===== RevColorDistance " + id + " - Initialed =====");
        }
    }

    public void update() {
        color = sensorColor.argb();
    }

    public double getIN(){
        if(sensorDistance != null) {
            if (timer.milliseconds() > 20) {
                timer.reset();
                dist = sensorDistance.getDistance(DistanceUnit.INCH);
            }

            return dist;
        } else
            return 0;
    }

    public double red(){
        return (color>>16)&0xFF;
    }

    public double green(){
        return (color>>8)&0xFF;
    }

    public double blue(){
        return (color)&0xFF;
    }
}

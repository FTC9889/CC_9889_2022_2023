package com.team9889.ftc2021.subsystems;

import android.util.Log;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.lib.CruiseLib;
import com.team9889.lib.Pose2d;
import com.team9889.lib.control.math.cartesian.Rotation2d;
import com.team9889.lib.control.math.cartesian.Vector2d;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.Arrays;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by Eric on 9/7/2019.
 */

@Config
public class MecanumDrive extends Subsystem {
    public static double tolerance = 3;
    //1.0175
    public static double yMultiplier = 1, xMultiplier = 1;

    public double xVel = 0, yVel = 0, thetaVel = 0;

    public double xSpeed, ySpeed, turnSpeed;

    public Rotation2d gyroAngle = new Rotation2d();
    public double angleFromAuton = 0, lastAngle = 0;

    ElapsedTime timer = new ElapsedTime();

    public Pose2d position = new Pose2d(0, 0, 0),
            position2WheelLeft = new Pose2d(0, 0, 0),
            position2WheelRight = new Pose2d(0, 0, 0),
            position2WheelArc = new Pose2d(0, 0, 0);
    public double angleOffset = 0;

    boolean auto;

    public enum Corner {
        TOP_RIGHT, BOTTOM_RIGHT, TOP_LEFT, BOTTOM_LEFT
    }
    public static Corner corner = Corner.TOP_RIGHT;
    public enum Sensor {
        FRONT, LEFT, BACK, RIGHT
    }
    public static Sensor xSensor = Sensor.FRONT, ySensor = Sensor.RIGHT;

    @Override
    public void init(boolean auto) {
        this.auto = auto;

        if (auto) {
            setBumpersDown();
        } else {
            setBumpersUp();
        }

        timer.reset();
    }

    @Override
    public void outputToTelemetry(Telemetry telemetry) {
//        telemetry.addData("Gyro", -CruiseLib.angleWrap(Robot.getInstance().gyro.getVoltage() / 2.75 * 360));

//        telemetry.addData("Left Light", Robot.getInstance().leftLight.getVoltage());
//        telemetry.addData("Center Light", Robot.getInstance().centerLight.getVoltage());
//        telemetry.addData("Right Light", Robot.getInstance().rightLight.getVoltage());

//        telemetry.addData("Odometry", Math.toDegrees(position.getHeading()));
//        telemetry.addData("IMU", getAngle().getTheda(AngleUnit.DEGREES));

        telemetry.addData("Odometry 1", Robot.getInstance().bRDrive.getPosition());
        telemetry.addData("Odometry 2", Robot.getInstance().bLDrive.getPosition());
        telemetry.addData("Odometry 3", Robot.getInstance().fRDrive.getPosition());

        telemetry.addData("Position", Arrays.toString(Robot.getInstance().getMecanumDrive().position.getArray()));


//        double leftPod = Robot.getInstance().bRDrive.getPosition() / 1440.0 * ((35 / 25.4) * PI) * (25.0 / 15.0);
//        double rightPod = Robot.getInstance().bLDrive.getPosition() / 1440.0 * ((35 / 25.4) * PI) * (25.0 / 15.0);
//        double middlePod = -Robot.getInstance().fRDrive.getPosition() / 1440.0 * ((35 / 25.4) * PI) * (25.0 / 15.0);
//        telemetry.addData("Track Width", (leftPod - rightPod) / (getAngle().getTheda(AngleUnit.RADIANS) + Math.toRadians(3600)));
//        telemetry.addData("Middle Wheel Offset", middlePod / (getAngle().getTheda(AngleUnit.RADIANS) + Math.toRadians(3600)));

//        telemetry.addData("Left Front", Robot.getInstance().fLDrive.getPosition());
//        telemetry.addData("Right Front", Robot.getInstance().fRDrive.getPosition());
//        telemetry.addData("Left Back", Robot.getInstance().bLDrive.getPosition());
//        telemetry.addData("Right Back", Robot.getInstance().bRDrive.getPosition());

//        telemetry.addData("Gyro", gyroAngle.getTheda(AngleUnit.DEGREES) - angleFromAuton);

//        telemetry.addData("Sensor 0", (Robot.getInstance().bulkDataMaster.getAnalogInputValue(0) * 24 / 380) + 7);
//        telemetry.addData("Sensor 1", (Robot.getInstance().bulkDataMaster.getAnalogInputValue(1) * 24 / 380) + 7);
//        telemetry.addData("Sensor 3", (Robot.getInstance().bulkDataSlave.getAnalogInputValue(0) * 24 / 380) + 7);
//        telemetry.addData("Sensor 4", (Robot.getInstance().bulkDataSlave.getAnalogInputValue(1) * 24 / 380) + 7);
    }

    @Override
    public void update() {
        if (!auto) {
            setPower(xSpeed, ySpeed, turnSpeed);
            xSpeed = 0;
            ySpeed = 0;
            turnSpeed = 0;
        }

        updateOdometry();
        update2WheelOdometryLeft();
        update2WheelOdometryRight();
        update2WheelOdometryArc();

        lastAngle = CruiseLib.angleWrapRad(getAngle().getTheda(AngleUnit.RADIANS));
    }

    @Override
    public void stop() {
        Robot.getInstance().fLDrive.setPower(0);
        Robot.getInstance().fRDrive.setPower(0);
        Robot.getInstance().bLDrive.setPower(0);
        Robot.getInstance().bRDrive.setPower(0);
    }

    public Rotation2d getAngle(){
        return gyroAngle;
    }

    public void writeAngleToFile() {
        angleFromAuton = gyroAngle.getTheda(AngleUnit.RADIANS);
    }

    public void setFieldCentricPower(double x, double y, double rotation, boolean blue) {
        double angle = gyroAngle.getTheda(AngleUnit.RADIANS);

        if (blue) {
            x = -x;
            y = -y;
        }

        double xMod = x * cos(angle - angleFromAuton) - y * sin(angle - angleFromAuton);
        double yMod = x * sin(angle - angleFromAuton) + y * cos(angle - angleFromAuton);

        Log.v("Drive", "X:" + xMod + "Y:" + yMod + "Rotation:" + rotation);
        setPower(xMod, yMod, rotation);
    }

    public void setPower(double leftStickX, double leftStickY, double rightStickX){
        double r = Math.hypot(leftStickX, leftStickY);
        double robotAngle = Math.atan2(leftStickY, leftStickX) - PI / 4;
        double rightX = rightStickX;
        final double v1 = r * cos(robotAngle) + rightX;
        final double v2 = r * sin(robotAngle) - rightX;
        final double v3 = r * sin(robotAngle) + rightX;
        final double v4 = r * cos(robotAngle) - rightX;

        Robot.getInstance().fLDrive.setPower(v1);
        Robot.getInstance().fRDrive.setPower(v2);
        Robot.getInstance().bLDrive.setPower(v3);
        Robot.getInstance().bRDrive.setPower(v4);
    }


    public void setBumpersDown() {
        Robot.getInstance().leftBumper.setPosition(0);
        Robot.getInstance().rightBumper.setPosition(0 + 0.15);
    }

    public void setBumpersUp() {
        Robot.getInstance().leftBumper.setPosition(0.35);
        Robot.getInstance().rightBumper.setPosition(0.3 + 0.15);
    }


    public double getDistance() {
        return (Robot.getInstance().distance.getVoltage() * 63.202 + 0.0626 + 3.25)
                * sin(-position.getHeading());
    }

    public double getSideDistance() {
        return (Robot.getInstance().sideDistance.getVoltage() * 63.202 + 0.0626 + 3.25)
                * sin(-position.getHeading());
    }


//  Constraints:
//      Robot must be parallel to the field walls
//      Assume Robot is always facing back wall

    //bulkDataMaster 0: Left (5.5 inches from robot center)
    //bulkDataMaster 1 Back (5.75 inches from robot center)
    //bulkDataSlave 0: Front (7 inches from robot center)
    //bulkDataSlave 1: Right (5.5 inches from robot center)
    public Vector2d getPosition (Sensor xSensor, Sensor ySensor, Corner corner) {
        double angle = gyroAngle.getTheda(AngleUnit.RADIANS), xDist = 0, yDist = 0;

        xDist = getUltrasonic(xSensor);
        yDist = getUltrasonic(ySensor);

        Vector2d robotPos = new Vector2d(), globalPos = new Vector2d();

        double adjustedAngle = angle;

        if (angle > PI / 4.0){
            adjustedAngle -= PI / 2.0;
        }else if (angle < -PI / 4.0){
            adjustedAngle += PI / 2.0;
        }

        if (angle > PI / 4.0){
            adjustedAngle -= PI / 2.0;
        }else if (angle < -PI / 4.0){
            adjustedAngle += PI / 2.0;
        }

        Log.v("Adjusted Angle", angle + ", " + adjustedAngle);

        robotPos.setX((xDist * cos(0)) + (yDist * sin(0)));
        robotPos.setY((yDist * cos(0)) + (xDist * sin(0)));

        switch (corner) {
            case TOP_RIGHT:
                globalPos = new Vector2d(72,-72);
                robotPos.setX(-robotPos.getX());
                break;

            case BOTTOM_RIGHT:
                globalPos = new Vector2d(-72,-72);
                break;

            case TOP_LEFT:
                globalPos = new Vector2d(72,72);
                robotPos.setX(-robotPos.getX());
                robotPos.setY(-robotPos.getY());
                break;

            case BOTTOM_LEFT:
                globalPos = new Vector2d(-72,72);
                robotPos.setY(-robotPos.getY());
                break;
        }

        globalPos = globalPos.add(robotPos);

        //Robot.getInstance().rr.setPoseEstimate(new Pose2d(globalPos.getX(), globalPos.getY(), angle));

        Log.v("New Position", globalPos.getX() + ", " + globalPos.getY());

        return globalPos;
    }

    double getUltrasonic(Sensor sensor) {
        double dist = 0;
        switch (sensor) {
            case FRONT:
                dist = (Robot.getInstance().hubs.get(1).getBulkData().getAnalogInputVoltage(0) * 24.0 / 380.0) + 7;
                break;

            case LEFT:
                dist = (Robot.getInstance().hubs.get(0).getBulkData().getAnalogInputVoltage(0) * 24.0 / 380.0) + 5.5;
                break;

            case BACK:
                dist = (Robot.getInstance().hubs.get(0).getBulkData().getAnalogInputVoltage(1) * 24.0 / 380.0) + 5.75;
                break;

            case RIGHT:
                dist = (Robot.getInstance().hubs.get(0).getBulkData().getAnalogInputVoltage(1) * 24.0 / 380.0) + 5.5;
                break;
        }

        return dist;
    }


    double lastIMU = 0;
    public void updateOdometry() {
        double trackWidth = 10.03514883514424;
        double middleToCenter = 4.4499722;

        double leftPod = Robot.getInstance().bLDrive.getDeltaPosition() / 1440.0 * ((35 / 25.4) * PI) * (25.0 / 15.0);
        double rightPod = Robot.getInstance().bRDrive.getDeltaPosition() / 1440.0 * ((35 / 25.4) * PI) * (25.0 / 15.0);
        double middlePod = -Robot.getInstance().fRDrive.getDeltaPosition() / 1440.0 * ((35 / 25.4) * PI) * (25.0 / 15.0);

        double deltaAngle = ((leftPod - rightPod) / trackWidth);
        Log.v("Delta Angle", "" + deltaAngle);

        double y = ((leftPod + rightPod) / 2) * yMultiplier;
        double x = (middlePod * xMultiplier) - (deltaAngle * middleToCenter);


        xVel = x * (1000 / timer.milliseconds());
        yVel = -y * (1000 / timer.milliseconds());
        thetaVel = Math.toDegrees(deltaAngle) * (1000 / timer.milliseconds());
        timer.reset();


        if (Math.abs(deltaAngle) < 0.1) {
            position.setHeading(getAngle().getTheda(AngleUnit.RADIANS));
        } else {
            position.setHeading(CruiseLib.angleWrapRad(position.getHeading() + deltaAngle));
        }
        
        position.addX(((x * cos(position.getHeading())) + (y * sin(position.getHeading()))));
        position.addY(-(y * cos(position.getHeading())) + (x * sin(position.getHeading())));
    }



    public void update2WheelOdometryLeft() {
        double normalToCenter = 10.03514883514424 / 2.0;
        double sidewaysToCenter = 4.4499722;

        double normalPod = Robot.getInstance().bLDrive.getDeltaPosition() / 1440.0 * ((35 / 25.4) * PI) * (25.0 / 15.0);
        double sidewaysPod = -Robot.getInstance().fRDrive.getDeltaPosition() / 1440.0 * ((35 / 25.4) * PI) * (25.0 / 15.0);


        double y = normalPod - (CruiseLib.angleWrapRad(getAngle().getTheda(AngleUnit.RADIANS) - lastAngle) * normalToCenter);
        double x = sidewaysPod - (CruiseLib.angleWrapRad(getAngle().getTheda(AngleUnit.RADIANS) - lastAngle) * sidewaysToCenter);

        position2WheelLeft.setHeading(getAngle().getTheda(AngleUnit.RADIANS));
        position2WheelLeft.addX((x * cos(position2WheelLeft.getHeading())) + (y * sin(position2WheelLeft.getHeading())));
        position2WheelLeft.addY((-y * cos(position2WheelLeft.getHeading())) + (x * sin(position2WheelLeft.getHeading())));
    }

    public void update2WheelOdometryRight() {
        double normalToCenter = -10 / 2.0;
        double sidewaysToCenter = 4.4499722;

        double normalPod = Robot.getInstance().bRDrive.getDeltaPosition() / 1440.0 * ((35 / 25.4) * PI) * (25.0 / 15.0);
        double sidewaysPod = -Robot.getInstance().fRDrive.getDeltaPosition() / 1440.0 * ((35 / 25.4) * PI) * (25.0 / 15.0);


        double y = normalPod - (CruiseLib.angleWrapRad(getAngle().getTheda(AngleUnit.RADIANS) - lastAngle) * normalToCenter);
        double x = sidewaysPod - (CruiseLib.angleWrapRad(getAngle().getTheda(AngleUnit.RADIANS) - lastAngle) * sidewaysToCenter);

        position2WheelRight.setHeading(getAngle().getTheda(AngleUnit.RADIANS));
        position2WheelRight.addX((x * cos(position2WheelRight.getHeading())) + (y * sin(position2WheelRight.getHeading())));
        position2WheelRight.addY((-y * cos(position2WheelRight.getHeading())) + (x * sin(position2WheelRight.getHeading())));
    }

    void update2WheelOdometryArc() {
        double normalToCenter = 10.03514883514424 / 2.0;
        double sidewaysToCenter = 4.4499722;

        double forwardPod = Robot.getInstance().fLDrive.getDeltaPosition() / 1440.0 * ((35 / 25.4) * PI) * (25.0 / 15.0);
        double sidewaysPod = -Robot.getInstance().bLDrive.getDeltaPosition() / 1440.0 * ((35 / 25.4) * PI) * (25.0 / 15.0);
        double angle = getAngle().getTheda(AngleUnit.RADIANS);
        double difference = CruiseLib.angleWrapRad(angle - lastAngle);

        double y = forwardPod - difference * normalToCenter;
        double x = sidewaysPod - difference * sidewaysToCenter;

        if(Math.abs(difference) > 0.001) {
            double r0 = forwardPod / difference;
            double r1 = sidewaysPod / difference;

            double relativeDx = r0 * sin(difference) - r1 * (1 - cos(difference));
            double relativeDy = r1 * sin(difference) + r0 * (1 - cos(difference));
            x = relativeDx * cos(angle) - relativeDy * sin(angle);
            y = -relativeDy * cos(angle) - relativeDx * sin(angle);
        } else {
            x = (x * cos(position2WheelArc.getHeading())) + (y * sin(position2WheelArc.getHeading()));
            y = (-y * cos(position2WheelArc.getHeading())) + (x * sin(position2WheelArc.getHeading()));
        }

        position2WheelArc.setHeading(angle);
        position2WheelArc.addX(x);
        position2WheelArc.addY(y);
    }
}
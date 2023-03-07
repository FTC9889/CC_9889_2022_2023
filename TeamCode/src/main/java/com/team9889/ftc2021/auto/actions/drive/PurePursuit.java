package com.team9889.ftc2021.auto.actions.drive;

import android.util.Log;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.auto.actions.ActionVariables;
import com.team9889.ftc2021.subsystems.Robot;
import com.team9889.lib.CruiseLib;
import com.team9889.lib.Pose;
import com.team9889.lib.Pose2d;
import com.team9889.lib.control.controllers.PID;

import java.util.ArrayList;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.hypot;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;

/**
 * Created by Eric on 3/4/2022.
 */

@Config
public class PurePursuit extends Action {
    public static double divider = 6;

    public static PID xPID = new PID(0.0025, 0, 0.4, 0),
            yPID = new PID(0.0025, 0, 0.4, 0),
            thetaPID = new PID(0.0008, 0, 0.03);

    double curXVel = 0, curYVel = 0, curThetaVel = 0;

    double maxSpeed = 0, timeout = -1, endTheta = 1000, maxStrafeVel = 24;
    ElapsedTime timer = new ElapsedTime();

    ArrayList<Pose> path;
    Pose tolerance = new Pose(2, 2, 3);
    int step = 1;

    boolean overshot = false, error = false, stopWall = false;

    public PurePursuit(ArrayList<Pose> path) {
        this.path = path;
    }

    public PurePursuit(ArrayList<Pose> path, double endTheta) {
        this.path = path;
        this.endTheta = endTheta;
    }

    public PurePursuit(ArrayList<Pose> path, double endTheta, boolean higherMaxStrafeVel) {
        this.path = path;
        this.endTheta = endTheta;
        maxStrafeVel = (higherMaxStrafeVel ? 40 : 24);
    }

    public PurePursuit(ArrayList<Pose> path, double endTheta, double timeout) {
        this.path = path;
        this.timeout = timeout;
        this.endTheta = endTheta;
    }

    public PurePursuit(ArrayList<Pose> path, double endTheta, double timeout, boolean fieldCentric, boolean stopWall) {
        this.path = path;
        this.timeout = timeout;
        this.endTheta = endTheta;
        this.overshot = fieldCentric;
        this.stopWall = stopWall;
    }

    public PurePursuit(ArrayList<Pose> path, double endTheta, double timeout, boolean fieldCentric) {
        this.path = path;
        this.timeout = timeout;
        this.endTheta = endTheta;
        this.overshot = fieldCentric;
    }

    public PurePursuit(ArrayList<Pose> path, double endTheta, double timeout, boolean fieldCentric, double maxStrafeVel) {
        this.path = path;
        this.timeout = timeout;
        this.endTheta = endTheta;
        this.overshot = fieldCentric;
        this.maxStrafeVel = maxStrafeVel;
    }

    public PurePursuit(ArrayList<Pose> path, Pose tolerance) {
        this.path = path;
        this.tolerance = tolerance;
    }

    public PurePursuit(ArrayList<Pose> path, Pose tolerance, double timeout) {
        this.path = path;
        this.timeout = timeout;
        this.tolerance = tolerance;
    }

    public PurePursuit(ArrayList<Pose> path, Pose tolerance, double endTheta, double timeout) {
        this.path = path;
        this.timeout = timeout;
        this.tolerance = tolerance;
        this.endTheta = endTheta;
    }

    public PurePursuit(ArrayList<Pose> path, Pose tolerance, double endTheta, double timeout, boolean fieldCentric) {
        this.path = path;
        this.timeout = timeout;
        this.tolerance = tolerance;
        this.endTheta = endTheta;
        this.overshot = fieldCentric;
    }

    @Override
    public void start() {
//        if (!Robot.getInstance().isRed) {
//            for (int i = 0; i < path.size(); i++) {
//                path.get(i).y *= -1;
//                path.get(i).theta *= -1;
//            }
//        }

        Pose2d pose = Robot.getInstance().getMecanumDrive().position;
        path.add(0, new Pose(pose.getX(), pose.getY(), toDegrees(pose.getHeading())));

        timer.reset();
        ActionVariables.doneDriving = false;

//        for (int i = 0; i < path.size(); i++) {
//            path.get(i).maxSpeed *= 0.8;
//        }
    }

    @Override
    public void update() {
        Pose2d pose = Robot.getInstance().getMecanumDrive().position;

        if (path.size() - 1 > step) {
            Pose error = Pose.getError(Pose.Pose2dToPose(Robot.getInstance().getMecanumDrive().position),
                    path.get(step));
            if (hypot(error.x, error.y) < path.get(step).radius) {
                step++;
            }
        }

        Pose point = RobotToLine(path.get(step - 1), path.get(step), pose, path.get(step).radius);

        if (point.x == 10000) {
            point = path.get(step);
            error = true;
        }


        // Speed
        maxSpeed = path.get(step).maxSpeed;
        double xSpeed = 1, ySpeed = 1;

        if ((point.x == path.get(path.size() - 1).x && point.y == path.get(path.size() - 1).y && !error) || overshot) {
            double x = CruiseLib.limitValue(((point.x - pose.getX()) / path.get(step).radius), 0, -1, 0, 1);
            x *= CruiseLib.limitValue((abs(point.x - pose.getX()) / divider), 0, -1, 0, 1);
            x = CruiseLib.limitValue(-x, -0.15, -maxSpeed, 0.15, maxSpeed);

            double y = CruiseLib.limitValue(((point.y - pose.getY()) / path.get(step).radius), 0, -1, 0, 1);
            y *= CruiseLib.limitValue((abs(point.y - pose.getY()) / divider), 0, -1, 0, 1);
            y = CruiseLib.limitValue(y, -0.15, -maxSpeed, 0.15, maxSpeed);

            xSpeed = x * Math.cos(pose.getHeading()) - y * Math.sin(pose.getHeading());
            ySpeed = y * Math.cos(pose.getHeading()) + x * Math.sin(pose.getHeading());

            overshot = true;
        } else {
            double relativeDist = Math.sqrt(Math.pow(point.x - pose.getX(), 2) + Math.pow(point.y - pose.getY(), 2));

            double speed = Range.clip((abs(relativeDist) / path.get(step).radius), 0, 1);
            speed *= Range.clip((abs(relativeDist) / ((divider / 10) * path.get(step).radius)), 0, 1);
            speed = CruiseLib.limitValue(speed, -0.1, -maxSpeed, 0.1, maxSpeed);

            xSpeed = speed * Math.sin(Math.toRadians(path.get(step).theta));
            ySpeed = speed * Math.cos(Math.toRadians(path.get(step).theta));
        }


        //Turn
        double relativePointAngle;

        if ((point.x == path.get(path.size() - 1).x && point.y == path.get(path.size() - 1).y && !error) || overshot) {
            double angleToPoint;
            if (endTheta == 1000) {
                 angleToPoint = toDegrees(atan2(path.get(path.size() - 1).x - path.get(path.size() - 2).x,
                        path.get(path.size() - 1).y - path.get(path.size() - 2).y)) + (path.get(step).theta);
            } else {
                angleToPoint = -endTheta;
            }
            relativePointAngle = -CruiseLib.angleWrap(angleToPoint + toDegrees(pose.getHeading()));

            Log.v("Angle FC", angleToPoint + ", " + relativePointAngle + ", " + toDegrees(pose.getHeading()));

            overshot = true;
        } else {
            double angleToPoint = toDegrees(atan2(point.x-pose.getX(), point.y-pose.getY())) + (path.get(step).theta);
            relativePointAngle = -CruiseLib.angleWrap(angleToPoint + toDegrees(pose.getHeading()));

            Log.v("Angle", angleToPoint + ", " + relativePointAngle + ", " + toDegrees(pose.getHeading()));
        }

        double turnSpeed = CruiseLib.limitValue(relativePointAngle / 70.0,
                0, -path.get(step).turnSpeed, 0, path.get(step).turnSpeed);

        xSpeed = CruiseLib.limitValue(xSpeed, 1 - Math.abs(turnSpeed * 1.5));
        ySpeed = CruiseLib.limitValue(ySpeed, 1 - Math.abs(turnSpeed * 1.5));

        xSpeed *= maxStrafeVel;
        ySpeed *= 48;
        turnSpeed *= 220;

        curXVel += xPID.update(-Robot.getInstance().getMecanumDrive().xVel, xSpeed);
        curYVel += yPID.update(Robot.getInstance().getMecanumDrive().yVel, ySpeed);
        curThetaVel += thetaPID.update(Robot.getInstance().getMecanumDrive().thetaVel, turnSpeed);

        curXVel = CruiseLib.limitValue(curXVel, 1);
        curYVel = CruiseLib.limitValue(curYVel, 1);
        curThetaVel = CruiseLib.limitValue(curThetaVel, 1);

        Robot.getInstance().getMecanumDrive().setPower(curXVel, curYVel, curThetaVel);

        error = false;
        TelemetryPacket packet = new TelemetryPacket();
        for (int i = 0; i < path.size() - 1; i++) {
            packet.fieldOverlay()
                    .setStroke("red")
                    .strokeLine(path.get(i).x, path.get(i).y, path.get(i + 1).x, path.get(i + 1).y);
        }

        packet.fieldOverlay()
                .setFill("green")
                .fillRect(pose.getX() - 6.5, pose.getY() - 6.5, 13, 13);

        packet.fieldOverlay()
                .setStroke("blue")
                .strokeCircle(pose.getX(), pose.getY(), path.get(step).radius);

        packet.fieldOverlay()
                .setStroke("black")
                .strokeLine(pose.getX(), pose.getY(), point.x, point.y);

        packet.put("Wanted Vel", xSpeed);
        packet.put("Current Vel", -Robot.getInstance().getMecanumDrive().xVel);
        packet.put("Power", curXVel * 100);

        FtcDashboard.getInstance().sendTelemetryPacket(packet);
    }

    int count;
    @Override
    public boolean isFinished() {
        Pose error = Pose.getError(Pose.Pose2dToPose(Robot.getInstance().getMecanumDrive().position),
                path.get(path.size() - 1));

        boolean theta = false;
        if (endTheta == 1000) {
            theta = true;
        } else if (Math.abs(Math.toDegrees(Robot.getInstance().getMecanumDrive().position.getHeading())
                - endTheta) < tolerance.theta) {
            theta = true;
        }

        if (((abs(error.x) < tolerance.x && abs(error.y) < tolerance.y) && theta
                && step == path.size() - 1) || (timeout != -1 && timer.milliseconds() > timeout) ||
                (stopWall && hypot(Robot.getInstance().getMecanumDrive().xVel,
                        Robot.getInstance().getMecanumDrive().yVel) < 5 && step == path.size() - 1)) {
            count++;
        } else {
            count = 0;
        }
        return count > 3;
    }

    @Override
    public void done() {
        Robot.getInstance().getMecanumDrive().setPower(0, 0, 0);
        ActionVariables.doneDriving = true;

        Log.v("Angle", "Done");
    }


    Pose RobotToLine (Pose point1, Pose point2, Pose2d pose, double radius) {
        double xDiff = point2.x - point1.x;
        if (xDiff <= 0.1)
            xDiff = 0.1;

        double slope = (point2.y - point1.y) / xDiff;
        double yIntercept = point2.y - (slope * point2.x);

        double xOnLine = (-yIntercept*slope + pose.getX() + pose.getY()*slope) / (pow(slope, 2) + 1);
        double yOnLine = (xOnLine-pose.getX()) * (-1/slope) + pose.getY();

        pose = new Pose2d(xOnLine, yOnLine, 0);

        double a = 1 + pow(slope, 2);
        double b = ((slope * yIntercept) * 2) + (-pose.getX() * 2) + ((-pose.getY() * 2) * slope);
        double c = pow(yIntercept, 2) + ((-pose.getY() * 2) * yIntercept) + (-pow(radius, 2) + pow(pose.getX(), 2) + pow(pose.getY(), 2));

        double d = pow(b, 2) - (4 * a * c);

        if (d >= 0) {
            double sol1 = (-b - sqrt(d)) / (2 * a);
            double sol2 = (-b + sqrt(d)) / (2 * a);

            double y1 = slope * sol1 + yIntercept;
            double y2 = slope * sol2 + yIntercept;

            if (y1 - point1.y < 0 && point2.y - y1 > 0)
                if (y2 - point1.y < 0 && point2.y - y2 > 0)
                    return new Pose(10000, 10000, 10000);

            if (sol1 - point1.x < 0 && point2.x - sol1 > 0)
                if (sol2 - point1.x < 0 && point2.x - sol2 > 0)
                    return new Pose(10000, 10000, 10000);

            double error1 = abs(point2.x - sol1) + abs(point2.y - y1);
            double error2 = abs(point2.x - sol2) + abs(point2.y - y2);

            Pose follow = new Pose(sol1, y1, 0);
            if (error1 > error2)
                follow = new Pose(sol2, y2, 0);

//            follow = Pose.limitPoseInLine(follow, point1, point2);

            if (sqrt(pow(point2.x - pose.getX(), 2) + pow(point2.y - pose.getY(), 2)) < radius) {
                follow = point2;
            }

            return follow;
        } else {
            return new Pose(10000, 10000, 10000);
        }
    }
}
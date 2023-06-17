package com.team9889.ftc2021.auto.actions.drive;

import android.util.Log;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.auto.actions.ActionVariables;
import com.team9889.ftc2021.subsystems.Robot;
import com.team9889.lib.CruiseLib;
import com.team9889.lib.Pose2d;
import com.team9889.lib.control.controllers.PID;
import com.team9889.lib.control.math.cartesian.Vector2d;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.hypot;
import static java.lang.Math.toDegrees;

/**
 * Created by edm11 on 6/10/2023.
 */
public class DriveOnLine extends Action {
    Vector2d leftToRobot = new Vector2d(38 / 25.4, 104 / 25.4), centerToRobot = new Vector2d(8 / 25.4, 104 / 25.4), rightToRobot = new Vector2d(-42 / 25.4, 104 / 25.4);

    public static PID xPID = new PID(0.0025, 0, 0.4, 0),
            yPID = new PID(0.0015, 0, 0.05, 0),
            thetaPID = new PID(0.0003, 0, 0.005);

    boolean center = false;
    int centerCount = 0;

    double curXVel = 0, curYVel = 0, curThetaVel = 0, timeout = 10000;
    ElapsedTime timer = new ElapsedTime();

    boolean left;

    public DriveOnLine(boolean left) {
        this.left = left;
    }

    public DriveOnLine(boolean left, double timeout) {
        this.left = left;
        this.timeout = timeout;
    }

    @Override
    public void start() {
        timer.reset();
        ActionVariables.doneDriving = false;

        curYVel = 0.3;
    }

    @Override
    public void update() {
        Pose2d pose = Robot.getInstance().getMecanumDrive().position;


        double relativeYDist = 12 - pose.getY();
        double xSpeed = CruiseLib.limitValue((abs(relativeYDist) / 10), 0, -1, 0, 1);

        double relativeXDist = ((left ? -1 : 1) * 61) - pose.getX();
        double ySpeed = CruiseLib.limitValue((abs(relativeXDist) / 10), 0, -1, 0, 1);

        if (centerDetect() || center) {
            xSpeed = 0;

            centerCount++;
            if (centerCount >= 0) {
                center = true;
            }
        } else {
            if (leftDetect()) {
                xSpeed = -0.1;
                ySpeed = CruiseLib.limitValue((abs(relativeXDist) / 10), 0, -0.6, 0, 0.6);
                Log.v("ColorDetect", "Left");
            } else if (rightDetect()) {
                xSpeed = 0.1;
                ySpeed = CruiseLib.limitValue((abs(relativeXDist) / 10), 0, -0.6, 0, 0.6);
                Log.v("ColorDetect", "Right");
            } else if (farLeftDetect()) {
                xSpeed = -0.2;
                ySpeed = CruiseLib.limitValue((abs(relativeXDist) / 10), 0, -0.4, 0, 0.4);
                Log.v("ColorDetect", "Far Left");
            } else if (farRightDetect()) {
                xSpeed = 0.2;
                ySpeed = CruiseLib.limitValue((abs(relativeXDist) / 10), 0, -0.4, 0, 0.4);
                Log.v("ColorDetect", "Far Right");
            }

            centerCount = 0;
        }

        double relativePointAngle = CruiseLib.angleWrap(90 * (left ? -1 : 1) + toDegrees(pose.getHeading()));
        double turnSpeed = -CruiseLib.limitValue(relativePointAngle / 70.0, 0, -1, 0, 1);

        xSpeed *= 60;
        ySpeed *= 36;
        turnSpeed *= 90;

        curXVel += xPID.update(-Robot.getInstance().getMecanumDrive().xVel, xSpeed);
        curYVel += yPID.update(Robot.getInstance().getMecanumDrive().yVel, ySpeed);
        curThetaVel += thetaPID.update(Robot.getInstance().getMecanumDrive().thetaVel, turnSpeed);

        curXVel = CruiseLib.limitValue(curXVel, 1);
        curYVel = CruiseLib.limitValue(curYVel, 1);
        curThetaVel = CruiseLib.limitValue(curThetaVel, 1);

        Robot.getInstance().getMecanumDrive().setPower(curXVel, curYVel, curThetaVel);
    }

    @Override
    public boolean isFinished() {
        return timer.milliseconds() > timeout ||
                (hypot(Robot.getInstance().getMecanumDrive().xVel,
                        Robot.getInstance().getMecanumDrive().yVel) < 6 && timer.milliseconds() > 200);
//        return timer.milliseconds() > timeout;
    }

    @Override
    public void done() {
        Robot.getInstance().getMecanumDrive().setPower(0, 0, 0);
        ActionVariables.doneDriving = true;
    }

    void updateLinePos(double heading) {
        if (left) {

        } else {
            if (leftDetect() && centerDetect()) {
                Robot.getInstance().getMecanumDrive().position.setY((12 - (Math.sin(heading) * (leftToRobot.getX() + centerToRobot.getX()) / 2))
                        + (Math.cos(heading) * (leftToRobot.getY() + centerToRobot.getY()) / 2));
            } else if (rightDetect() && centerDetect()) {
                Robot.getInstance().getMecanumDrive().position.setY((12 - (Math.sin(heading) * (rightToRobot.getX() + centerToRobot.getX()) / 2))
                        + (Math.cos(heading) * (rightToRobot.getY() + centerToRobot.getY()) / 2));
            } else if (leftDetect()) {
                Robot.getInstance().getMecanumDrive().position.setY((12 - (Math.sin(heading) * leftToRobot.getX()))
                        + (Math.cos(heading) * leftToRobot.getY()));
            } else if (rightDetect()) {
                Robot.getInstance().getMecanumDrive().position.setY((12 - (Math.sin(heading) * rightToRobot.getX()))
                        + (Math.cos(heading) * rightToRobot.getY()));
            } else if (centerDetect()) {
                Robot.getInstance().getMecanumDrive().position.setY((12 - (Math.sin(heading) * centerToRobot.getX()))
                        + (Math.cos(heading) * centerToRobot.getY()));
            }
        }
    }

    boolean farLeftDetect() {
        if (Robot.getInstance().isRed)
            return Robot.getInstance().farLeftColor.red() > 250;
        else
            return Robot.getInstance().farLeftColor.blue() > 1000;
    }

    boolean leftDetect() {
        if (Robot.getInstance().isRed)
            return Robot.getInstance().leftColor.red() > 250;
        else
            return Robot.getInstance().leftColor.blue() > 1000;
    }

    boolean centerDetect() {
        if (Robot.getInstance().isRed)
            return Robot.getInstance().centerColor.red() > 400;
        else
            return Robot.getInstance().centerColor.blue() > 1000;
    }

    boolean rightDetect() {
        if (Robot.getInstance().isRed)
            return Robot.getInstance().rightColor.red() > 250;
        else
            return Robot.getInstance().rightColor.blue() > 1000;
    }

    boolean farRightDetect() {
        if (Robot.getInstance().isRed)
            return Robot.getInstance().farRightColor.red() > 250;
        else
            return Robot.getInstance().farRightColor.blue() > 1000;
    }
}
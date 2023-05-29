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
 * Created by edm11 on 5/6/2023.
 */
public class DriveOnLine extends Action {
    Vector2d leftToRobot = new Vector2d(38 / 25.4, 104 / 25.4), centerToRobot = new Vector2d(8 / 25.4, 104 / 25.4), rightToRobot = new Vector2d(-42 / 25.4, 104 / 25.4);

    public static PID xPID = new PID(0.0025, 0, 0.4, 0),
            yPID = new PID(0.0015, 0, 0.05, 0),
            thetaPID = new PID(0.0003, 0, 0.005);

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
    }

    @Override
    public void update() {
        Pose2d pose = Robot.getInstance().getMecanumDrive().position;
//        updateLinePos(pose.getHeading());

        double relativeXDist = 61 - pose.getX();
        double ySpeed = CruiseLib.limitValue((abs(relativeXDist) / 10), -0.1, -1, 0.1, 1);


        double relativeYDist = 12 - pose.getY();
        double xSpeed = CruiseLib.limitValue((abs(relativeYDist) / 10), -0.1, -1, 0.1, 1);


        double relativePointAngle = CruiseLib.angleWrap(90 * (left ? 1 : -1) + toDegrees(pose.getHeading()));
        double turnSpeed = CruiseLib.limitValue(relativePointAngle / 70.0, 0, -1, 0, 1);

        xSpeed *= 20;
        ySpeed *= 14;
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
                Robot.getInstance().getMecanumDrive().yVel) < 6 && timer.milliseconds() > 1000);
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

    boolean leftDetect() {
        return Robot.getInstance().leftColor.blue() > 60 || Robot.getInstance().leftColor.red() > 60;
    }

    boolean centerDetect() {
        return Robot.getInstance().centerColor.blue() > 400 || Robot.getInstance().centerColor.red() > 240;
    }

    boolean rightDetect() {
        return Robot.getInstance().rightColor.blue() > 180 || Robot.getInstance().rightColor.red() > 180;
    }
}

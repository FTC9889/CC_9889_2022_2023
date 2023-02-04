package com.team9889.ftc2021.auto.actions.drive;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.auto.actions.ActionVariables;
import com.team9889.ftc2021.subsystems.Robot;
import com.team9889.lib.CruiseLib;

import static java.lang.Math.atan2;
import static java.lang.Math.toDegrees;

/**
 * Created by edm11 on 1/31/2023.
 */

public class DriveToPole extends Action {
    double timeout, theta, lastTime = 0;
    ElapsedTime timer = new ElapsedTime();

    double xSpeed = 0, ySpeed = 0, maxSpeed = 0;

    public DriveToPole(double timeout, double theta) {
        this.timeout = timeout;
        this.theta = theta;
    }

    @Override
    public void start() {
        ActionVariables.doneDriving = false;
    }

    @Override
    public void update() {
        double x = (Robot.getInstance().getCamera().scanForPole.getPoint().x - 135);
        xSpeed = (-1E-07 * Math.pow(x, 3)) + (2E-19 * Math.pow(x, 2)) + (0.006 * x) * 1.5;
        ySpeed = 0.2 * Math.log(270 - Robot.getInstance().getCamera().scanForPole.width) - 0.2;

//        xSpeed = x / 50;
//        ySpeed = (50 - Robot.getInstance().getCamera().scanForPole.width) / 10;

        if (Robot.getInstance().getCamera().scanForPole.width > 64) {
            ySpeed = 0;
        }


        double angleToPoint = -theta;
        double relativePointAngle = -CruiseLib.angleWrap(angleToPoint +
                toDegrees(Robot.getInstance().getMecanumDrive().position.getHeading()));

        xSpeed = CruiseLib.limitValue(xSpeed, maxSpeed);
        ySpeed = CruiseLib.limitValue(ySpeed, maxSpeed);
        maxSpeed += (timer.milliseconds() - lastTime) * (1.0 / 500.0);
        lastTime = timer.milliseconds();

        Robot.getInstance().getMecanumDrive().setPower(-xSpeed, -ySpeed, relativePointAngle / 70);
    }

    @Override
    public boolean isFinished() {
        return timer.milliseconds() > timeout;
    }

    @Override
    public void done() {
        Robot.getInstance().getMecanumDrive().setPower(0, 0, 0);
        ActionVariables.doneDriving = true;
    }
}

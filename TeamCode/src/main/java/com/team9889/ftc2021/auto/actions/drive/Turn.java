package com.team9889.ftc2021.auto.actions.drive;

import android.util.Log;

import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.subsystems.Robot;
import com.team9889.lib.CruiseLib;

import static java.lang.Math.toDegrees;

/**
 * Created by edm11 on 11/8/2022.
 */
public class Turn extends Action {
    int theta = 0, tolerance = 3;

    public Turn(int theta) {
        this.theta = theta;
    }

    public Turn(int theta, int tolerance) {
        this.theta = theta;
        this.tolerance = tolerance;
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
        double relativeTheta = -CruiseLib.angleWrap(Math.toDegrees(Robot.getInstance().getMecanumDrive().position.getHeading()) - theta);
        Log.v("Angle", relativeTheta + "");

        double turnSpeed = CruiseLib.limitValue(relativeTheta / 120.0, -0.2, -1, 0.2, 1);

        Robot.getInstance().getMecanumDrive().setPower(0, 0, turnSpeed);
    }

    @Override
    public boolean isFinished() {
        return Math.abs(Math.toDegrees(Robot.getInstance().getMecanumDrive().position.getHeading()) - theta) < tolerance;
    }

    @Override
    public void done() {
        Robot.getInstance().getMecanumDrive().setPower(0, 0, 0);
    }
}

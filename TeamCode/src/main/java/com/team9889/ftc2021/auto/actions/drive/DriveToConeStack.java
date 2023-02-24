package com.team9889.ftc2021.auto.actions.drive;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.auto.actions.ActionVariables;
import com.team9889.ftc2021.subsystems.Robot;
import com.team9889.lib.CruiseLib;

import static java.lang.Math.toDegrees;

/**
 * Created by edm11 on 2/24/2023.
 */
class DriveToConeStack extends Action {
    double timeout;
    ElapsedTime time = new ElapsedTime();

    public DriveToConeStack (double timeout) {
        this.timeout = timeout;
    }

    @Override
    public void start() {
        ActionVariables.doneDriving = false;
    }

    @Override
    public void update() {
        double ySpeed = 0;
        if (Robot.getInstance().line1.getState()) {
            ySpeed = 0.4;
        } else if (Robot.getInstance().line2.getState()) {
            ySpeed = 0.2;
        } else if (Robot.getInstance().line4.getState()) {
            ySpeed = -0.2;
        } else if (Robot.getInstance().line5.getState()) {
            ySpeed = -0.4;
        }

        double angleToPoint = -90;
        double relativePointAngle = -CruiseLib.angleWrap(angleToPoint +
                toDegrees(Robot.getInstance().getMecanumDrive().position.getHeading()));

        Robot.getInstance().getMecanumDrive().setPower(0.25, ySpeed, relativePointAngle / 70.0);
    }

    @Override
    public boolean isFinished() {
        return time.milliseconds() > timeout;
    }

    @Override
    public void done() {
        Robot.getInstance().getMecanumDrive().setPower(0, 0, 0);
        ActionVariables.doneDriving = true;
    }
}

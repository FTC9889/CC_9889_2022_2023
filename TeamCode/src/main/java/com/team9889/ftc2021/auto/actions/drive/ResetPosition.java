package com.team9889.ftc2021.auto.actions.drive;

import android.util.Log;

import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.auto.actions.ActionVariables;
import com.team9889.ftc2021.subsystems.Lift;
import com.team9889.ftc2021.subsystems.Robot;
import com.team9889.lib.CircularBuffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by edm11 on 2/4/2023.
 */
public class ResetPosition extends Action {
    CircularBuffer xAverage = new CircularBuffer(20);
    CircularBuffer yAverage = new CircularBuffer(20);

    @Override
    public void start() {

    }

    @Override
    public void update() {
        if (ActionVariables.doneDriving) {
            if (Robot.getInstance().getMecanumDrive().getDistance() > 2) {
                xAverage.addValue(71 - Robot.getInstance().getMecanumDrive().getDistance());
            }

            double dist = Robot.getInstance().getMecanumDrive().getSideDistance();
            if (Math.abs(Robot.getInstance().getMecanumDrive().position.getHeading() + (Math.PI / 2))
                    < Math.toRadians(5) && dist > 55 && dist < 60) {
                yAverage.addValue(71 - Robot.getInstance().getMecanumDrive().getSideDistance());
                Log.v("Ultrasonic", "true: " + Robot.getInstance().getMecanumDrive().getSideDistance());
            } else {
                Log.v("Ultrasonic", "false: "
                        + Robot.getInstance().getMecanumDrive().position.getHeading() + ", " + dist);
            }
        }
    }

    @Override
    public boolean isFinished() {
        return ActionVariables.doneGrab;
    }

    @Override
    public void done() {
        Robot.getInstance().getMecanumDrive().position.setX(xAverage.getAverage());
        Robot.getInstance().getMecanumDrive().position.setY(yAverage.getAverage());
    }
}

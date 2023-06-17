package com.team9889.ftc2021.auto.actions.lift;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.auto.actions.ActionVariables;
import com.team9889.ftc2021.subsystems.Robot;

import androidx.core.app.NotificationCompat;

/**
 * Created by edm11 on 6/10/2023.
 */

public class SetLiftHeight extends Action {
    double liftHeight, wait;
    ElapsedTime timer = new ElapsedTime();

    public SetLiftHeight(double liftHeight) {
        this.liftHeight = liftHeight;
    }

    public SetLiftHeight(double liftHeight, double wait) {
        this.liftHeight = liftHeight;
        this.wait = wait;
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
        if (timer.milliseconds() > wait) {
            Robot.getInstance().getLift().wantedScoreState = null;
            Robot.getInstance().getLift().wantedLiftPosition = null;

            Robot.getInstance().getLift().setLiftPosition(liftHeight);
        }
    }

    @Override
    public boolean isFinished() {
        return ActionVariables.doneDriving;
    }

    @Override
    public void done() {
        Robot.getInstance().getLift().setLiftPower(0.05);
    }
}

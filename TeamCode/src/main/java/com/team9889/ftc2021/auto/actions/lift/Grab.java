package com.team9889.ftc2021.auto.actions.lift;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.auto.actions.ActionVariables;
import com.team9889.ftc2021.subsystems.Lift;
import com.team9889.ftc2021.subsystems.Robot;
import com.team9889.lib.CruiseLib;

/**
 * Created by edm11 on 11/12/2022.
 */

public class Grab extends Action {
    double liftHeight, wait = 0;
    ElapsedTime timer = new ElapsedTime();

    int stage = 0;

    public Grab(double liftHeight) {
        this.liftHeight = liftHeight;
    }

    public Grab(double liftHeight, double wait) {
        this.liftHeight = liftHeight;
        this.wait = wait;
    }

    @Override
    public void start() {
        ActionVariables.doneGrab = false;
        ActionVariables.grab = false;
    }

    @Override
    public void update() {
        if (stage == 0 && timer.milliseconds() > wait) {
            Robot.getInstance().getLift().wantedLiftPosition = Lift.LiftPositions.NULL;
            Robot.getInstance().getLift().wantedScoreState = Lift.ScoreStates.P_HOVER_RIGHT;

            stage = 1;
        } else if (stage == 1) {
            if (ActionVariables.grab) {
                Robot.getInstance().getLift().wantedScoreState = Lift.ScoreStates.GRAB_RIGHT;
            }

            Robot.getInstance().getLift().setLiftPosition(CruiseLib.limitValue(liftHeight, 100, 0));

            Robot.getInstance().getLift().update();
        }
    }

    @Override
    public boolean isFinished() {
        return Robot.getInstance().getLift().wantedScoreState == Lift.ScoreStates.HOLDING;
    }

    @Override
    public void done() {
        ActionVariables.grab = false;
        ActionVariables.doneGrab = true;
    }
}

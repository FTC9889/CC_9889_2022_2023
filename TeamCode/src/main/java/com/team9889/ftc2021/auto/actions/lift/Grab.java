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
    ElapsedTime timer = new ElapsedTime(), driveTimer = new ElapsedTime(), timer2 = new ElapsedTime();

    int stage = 0;
    double timerOffset = 0;

    boolean done = false;

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

        Robot.getInstance().getLift().liftInAutoPos = false;

        Robot.getInstance().getLift().autoConeCount += 1;
    }

    @Override
    public void update() {
        if (stage == 0 && timer.milliseconds() > wait) {
            Robot.getInstance().getLift().wantedLiftPosition = Lift.LiftPositions.NULL;
            Robot.getInstance().getLift().wantedScoreState = Lift.ScoreStates.P_HOVER_RIGHT;

            stage = 1;
        } else if (stage == 1) {
            if (Robot.getInstance().getLift().wantedScoreState == Lift.ScoreStates.HOLDING) {
                done = true;
            } else if (Robot.getInstance().getLift().wantedScoreState == Lift.ScoreStates.GRAB_RIGHT) {
                if (timer2.milliseconds() > 350) {
                    Robot.getInstance().getLift().liftInAutoPos =
                            Robot.getInstance().getLift().setLiftPosition(
                                    CruiseLib.limitValue(liftHeight + 10, 100, 0));
                }
            } else {
                Robot.getInstance().getLift().setLiftPosition(CruiseLib.limitValue(liftHeight, 100, 0), .3);
                timerOffset = timer.milliseconds();

//                if (driveTimer.milliseconds() > 200) {
                if (ActionVariables.doneDriving){
                    Robot.getInstance().getLift().wantedScoreState = Lift.ScoreStates.GRAB_RIGHT;
                }
            }
        }

        if (Robot.getInstance().getLift().wantedScoreState != Lift.ScoreStates.GRAB_RIGHT) {
            timer2.reset();
        }

        if (!ActionVariables.doneDriving) {
            driveTimer.reset();
        }

            Robot.getInstance().getLift().update();
    }

    @Override
    public boolean isFinished() {
        return Robot.getInstance().getLift().liftInAutoPos;
    }

    @Override
    public void done() {
        ActionVariables.grab = false;
        ActionVariables.doneGrab = true;
    }
}

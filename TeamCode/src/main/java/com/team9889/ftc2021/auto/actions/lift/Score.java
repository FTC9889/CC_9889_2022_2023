package com.team9889.ftc2021.auto.actions.lift;

import android.util.Log;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.auto.actions.ActionVariables;
import com.team9889.ftc2021.subsystems.Lift;
import com.team9889.ftc2021.subsystems.Robot;

/**
 * Created by edm11 on 11/12/2022.
 */

public class Score extends Action {
    Lift.LiftPositions liftHeight;
    boolean left;

    ElapsedTime timer = new ElapsedTime();

    public Score(Lift.LiftPositions liftHeight, boolean left) {
        this.liftHeight = liftHeight;
        this.left = left;
    }

    @Override
    public void start() {
        Robot.getInstance().getLift().wantedPickupStates = Lift.PickupStates.NULL;
        Robot.getInstance().getLift().wantedScoreState = (left ? Lift.ScoreStates.HOVER_LEFT : Lift.ScoreStates.HOVER_RIGHT);

        Robot.getInstance().getLift().wantedLiftPosition = liftHeight;

        ActionVariables.doneScore = false;
        ActionVariables.score = false;
    }

    @Override
    public void update() {
        if (left) {
            if (ActionVariables.score && Robot.getInstance().getLift().wantedScoreState == Lift.ScoreStates.HOVER_LEFT) {
                Robot.getInstance().getLift().wantedScoreState = Lift.ScoreStates.PLACE_LEFT;
            }
        } else {
            if (ActionVariables.score && Robot.getInstance().getLift().wantedScoreState == Lift.ScoreStates.HOVER_RIGHT) {
                Robot.getInstance().getLift().wantedScoreState = Lift.ScoreStates.PLACE_RIGHT;
            }
        }

        if (Robot.getInstance().getLift().wantedScoreState != Lift.ScoreStates.RETRACT) {
            timer.reset();
        }

        Robot.getInstance().getLift().update();
    }

    @Override
    public boolean isFinished() {
        return timer.milliseconds() > 200;
    }

    @Override
    public void done() {
        ActionVariables.score = false;
        ActionVariables.doneScore = true;
    }
}

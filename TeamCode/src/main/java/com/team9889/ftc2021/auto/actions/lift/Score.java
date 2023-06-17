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
    boolean left, first = true;
    double wait = 400;

    ElapsedTime timer = new ElapsedTime(), startTimer = new ElapsedTime();

    public Score(Lift.LiftPositions liftHeight, boolean left) {
        this.liftHeight = liftHeight;
        this.left = left;
    }

    public Score(Lift.LiftPositions liftHeight, boolean left, double wait) {
        this.liftHeight = liftHeight;
        this.left = left;
        this.wait = wait;
    }

    @Override
    public void start() {
        ActionVariables.doneScore = false;
        ActionVariables.score = false;
    }

    @Override
    public void update() {
        if (startTimer.milliseconds() > wait) {
            if (first) {
                Robot.getInstance().getLift().wantedScoreState = (left ? Lift.ScoreStates.HOVER_LEFT : Lift.ScoreStates.HOVER_RIGHT);
                Robot.getInstance().getLift().wantedLiftPosition = liftHeight;

                first = false;
            } else if (left) {
                if (ActionVariables.doneDriving && Robot.getInstance().getLift().wantedScoreState == Lift.ScoreStates.HOVER_LEFT) {
                    Robot.getInstance().getLift().wantedScoreState = Lift.ScoreStates.PLACE_LEFT;
                }
            } else {
                if (ActionVariables.doneDriving && Robot.getInstance().getLift().wantedScoreState == Lift.ScoreStates.HOVER_RIGHT) {
                    Robot.getInstance().getLift().wantedScoreState = Lift.ScoreStates.PLACE_RIGHT;
                }
            }
        }

        if (Robot.getInstance().getLift().wantedScoreState != Lift.ScoreStates.RETRACT) {
            timer.reset();
        }

        if (!ActionVariables.doneDriving) {
            startTimer.reset();
        }
    }

    @Override
    public boolean isFinished() {
//        return timer.milliseconds() > 25;
        return Robot.getInstance().getLift().wantedScoreState == Lift.ScoreStates.RETRACT;
    }

    @Override
    public void done() {
        ActionVariables.score = false;
        ActionVariables.doneScore = true;
    }
}

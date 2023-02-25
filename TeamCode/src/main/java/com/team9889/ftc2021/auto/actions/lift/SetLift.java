package com.team9889.ftc2021.auto.actions.lift;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.subsystems.Lift;
import com.team9889.ftc2021.subsystems.Robot;

/**
 * Created by edm11 on 2/25/2023.
 */

public class SetLift extends Action {
    Lift.LiftPositions liftPosition;
    Lift.ScoreStates scoreState = Lift.ScoreStates.NULL;
    double wait;
    ElapsedTime timer = new ElapsedTime();

    public SetLift(Lift.LiftPositions liftPosition, double wait) {
        this.liftPosition = liftPosition;
        this.wait = wait;
    }

    public SetLift(Lift.LiftPositions liftPosition, Lift.ScoreStates scoreState, double wait) {
        this.liftPosition = liftPosition;
        this.scoreState = scoreState;
        this.wait = wait;
    }

    @Override
    public void start() {
        timer.reset();
    }

    @Override
    public void update() {
        if (timer.milliseconds() > wait) {
            Robot.getInstance().getLift().wantedLiftPosition = liftPosition;
            Robot.getInstance().getLift().wantedScoreState = scoreState;
        }
    }

    @Override
    public boolean isFinished() {
        return timer.milliseconds() > wait + 100;
    }

    @Override
    public void done() {

    }
}

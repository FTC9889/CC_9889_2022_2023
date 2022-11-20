package com.team9889.ftc2021.auto.actions.lift;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.subsystems.Lift;
import com.team9889.ftc2021.subsystems.Robot;

/**
 * Created by edm11 on 11/12/2022.
 */

public class SetV4BPosition extends Action {
    Lift.V4BPositions position;
    int startTime = 0, endTime = 0;

    ElapsedTime timer = new ElapsedTime();

    public SetV4BPosition(Lift.V4BPositions position) {
        this.position = position;
    }

    public SetV4BPosition(Lift.V4BPositions position, int startTime) {
        this.position = position;
        this.startTime = startTime;
    }

    public SetV4BPosition(Lift.V4BPositions position, int startTime, int endTime) {
        this.position = position;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public void start() {
        timer.reset();
    }

    @Override
    public void update() {
        if (timer.milliseconds() > startTime) {
            Robot.getInstance().getLift().wantedV4BPosition = position;
        }
    }

    @Override
    public boolean isFinished() {
        return (startTime + (endTime == 0 ? 100 : endTime)) < timer.milliseconds();
    }

    @Override
    public void done() {

    }
}

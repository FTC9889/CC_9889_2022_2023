package com.team9889.ftc2021.auto.actions.lift;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.subsystems.Lift;
import com.team9889.ftc2021.subsystems.Robot;

/**
 * Created by edm11 on 11/12/2022.
 */

public class SetGrabber extends Action {
    boolean closed;
    int startTime = 0, endTime = 0;

    ElapsedTime timer = new ElapsedTime();

    public SetGrabber(boolean closed) {
        this.closed = closed;
    }

    public SetGrabber(boolean closed, int startTime) {
        this.closed = closed;
        this.startTime = startTime;
    }

    public SetGrabber(boolean closed, int startTime, int endTime) {
        this.closed = closed;
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
            if (closed) {
                Robot.getInstance().getLift().closeGrabber();
            } else {
                Robot.getInstance().getLift().openGrabber();
            }
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

package com.team9889.ftc2021.auto.actions.utl;

import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.subsystems.Robot;

/**
 * Created by Eric on 12/13/2019.
 */
public class RobotUpdate extends Action {
    Robot robot;

    public RobotUpdate (Robot robot) {
        this.robot = robot;
    }

    @Override
    public void start() {

    }

    @Override
    public void update() {
        robot.update();
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void done() {}
}

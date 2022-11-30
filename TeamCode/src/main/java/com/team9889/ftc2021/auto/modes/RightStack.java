package com.team9889.ftc2021.auto.modes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.team9889.ftc2021.auto.AutoModeBase;
import com.team9889.ftc2021.auto.actions.ActionVariables;
import com.team9889.ftc2021.auto.actions.drive.PurePursuit;
import com.team9889.ftc2021.auto.actions.drive.Turn;
import com.team9889.ftc2021.auto.actions.lift.Grab;
import com.team9889.ftc2021.auto.actions.lift.Score;
import com.team9889.ftc2021.auto.actions.lift.SetGrabber;
import com.team9889.ftc2021.auto.actions.lift.SetV4BPosition;
import com.team9889.ftc2021.auto.actions.utl.Wait;
import com.team9889.ftc2021.subsystems.Lift;
import com.team9889.lib.Pose;
import com.team9889.lib.Pose2d;

import java.util.ArrayList;

/**
 * Created by edm11 on 11/5/2022.
 */

@Autonomous
public class RightStack extends AutoModeBase {
    @Override
    public void initialize() {

    }

    @Override
    public void run(StartPosition startPosition) {
        ArrayList<Pose> path = new ArrayList<>();
        Robot.getMecanumDrive().position = new Pose2d(31, 65, Math.PI / 2);

        double speed = 1;

        ThreadAction(new Score(Lift.LiftPositions.HIGH, false));

        path.add(new Pose(31, 60, 90, speed, 4));
//        runAction(new PurePursuit(path));
//        path.clear();

        path.add(new Pose(18, 60, 0, speed, 8));
        path.add(new Pose(10, 35, 0, speed, 8));
        path.add(new Pose(6, 29, 0, speed, 10));
        runAction(new PurePursuit(path));
        path.clear();

        runAction(new Turn(115));

        ActionVariables.score = true;
        while (!ActionVariables.doneScore) {}


        //Cycle 1
        ThreadAction(new SetGrabber(true));
        runAction(new SetV4BPosition(Lift.V4BPositions.UP, 0, 100));

        path.add(new Pose(12, 30, 180, .7, 4));
        runAction(new PurePursuit(path));
        path.clear();

        path.add(new Pose(12, 12, 0, speed, 4));
        path.add(new Pose(40, 12, 0, speed, 8));
        runAction(new PurePursuit(path));
        path.clear();

        ThreadAction(new Grab(4));
        path.add(new Pose(65, 12, 0, speed, 8));
        runAction(new PurePursuit(path, 1500));
        path.clear();

        ActionVariables.grab = true;
        while (!ActionVariables.doneGrab) {}

//        Robot.getLift().wantedLiftPosition = Lift.LiftPositions.HIGH;
//        runAction(new Wait(300));

        ThreadAction(new Score(Lift.LiftPositions.HIGH, true));
        path.add(new Pose(30, 12, 180, speed, 8));
        path.add(new Pose(20, 15, 180, speed, 8));
        path.add(new Pose(7, 22, 180, speed, 8));
        runAction(new PurePursuit(path));
        path.clear();

        runAction(new Turn(-105));

        ActionVariables.score = true;
        while (!ActionVariables.doneScore) {}


        //Cycle 2
        ThreadAction(new SetGrabber(true));
        runAction(new SetV4BPosition(Lift.V4BPositions.UP, 0, 100));

        path.add(new Pose(12, 12, 0, speed, 4));
        path.add(new Pose(40, 12, 0, speed, 8));
        runAction(new PurePursuit(path));
        path.clear();

        ThreadAction(new Grab(4));
        path.add(new Pose(65, 12, 0, speed, 8));
        runAction(new PurePursuit(path, 1500));
        path.clear();

        ActionVariables.grab = true;
        while (!ActionVariables.doneGrab) {}

//        Robot.getLift().wantedLiftPosition = Lift.LiftPositions.HIGH;
//        runAction(new Wait(300));

        ThreadAction(new Score(Lift.LiftPositions.HIGH, true));
        path.add(new Pose(30, 12, 180, speed, 8));
        path.add(new Pose(20, 15, 180, speed, 8));
        path.add(new Pose(7, 22, 180, speed, 8));
        runAction(new PurePursuit(path));
        path.clear();

        runAction(new Turn(-105));

        ActionVariables.score = true;
        while (!ActionVariables.doneScore) {}


        //Park
        Robot.getLift().wantedLiftPosition = Lift.LiftPositions.NULL;
        Robot.getLift().setLiftPosition(0);
        path.add(new Pose(10, 12, 0, speed, 8));
        switch (signal) {
            case 2:
                path.add(new Pose(36, 12, 0, speed, 8));
                break;

            case 3:
                path.add(new Pose(60, 12, 0, speed, 8));
                break;
        }
        runAction(new PurePursuit(path));
        path.clear();

        if (signal == 1) {
            runAction(new Turn(180));
        }
    }

    @Override
    public StartPosition side() {
        return null;
    }
}

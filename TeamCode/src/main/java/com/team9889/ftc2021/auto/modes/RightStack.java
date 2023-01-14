package com.team9889.ftc2021.auto.modes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;
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

@Autonomous(preselectTeleOp = "Teleop")
public class RightStack extends AutoModeBase {
    ElapsedTime timer = new ElapsedTime();

    @Override
    public void initialize() {

    }

    @Override
    public void run(StartPosition startPosition) {
        ArrayList<Pose> path = new ArrayList<>();
        Robot.getMecanumDrive().position = new Pose2d(31, 65, Math.PI / 2);
        Robot.getMecanumDrive().angleOffset = Math.PI / 2;

        double speed = 1;

        timer.reset();
        ThreadAction(new Score(Lift.LiftPositions.HIGH, false));

        path.add(new Pose(31, 60, 90, speed, 2));
//        runAction(new PurePursuit(path));
//        path.clear();

        path.add(new Pose(19, 60, 0, speed, 8));
        runAction(new PurePursuit(path, 90, 1200));
        path.clear();

        runAction(new Wait(50));

        path.add(new Pose(16, 30, 0, speed, 8, 0.4));
        path.add(new Pose(6, 27, 0, speed, 10));
        runAction(new PurePursuit(path, 115));
        path.clear();

//        runAction(new Turn(115));

        ActionVariables.score = true;
        while (!ActionVariables.doneScore) {}


        //Cycle 1
        ThreadAction(new SetGrabber(true));
        runAction(new SetV4BPosition(Lift.V4BPositions.UP, 0, 100));

        path.add(new Pose(12, 30, 180, speed, 4));
        runAction(new PurePursuit(path));
        path.clear();

        path.add(new Pose(12, 16, 0, speed, 4));

        for (int i = 0; i < 5 && timer.milliseconds() < 24500; i++) {
            path.add(new Pose(40, 13, 0, speed, 8));
            runAction(new PurePursuit(path));
            path.clear();

            ThreadAction(new Grab(4 - i));
            path.add(new Pose(50, 13, 0, speed, 10));
            path.add(new Pose(63, 13, 0, 0.4, 8));
            runAction(new PurePursuit(path, -90, 800));
            path.clear();

            ActionVariables.grab = true;
            while (!ActionVariables.doneGrab) {
            }

            ThreadAction(new Score(Lift.LiftPositions.HIGH, true));
            path.add(new Pose(35, 10, 180, speed, 8));
            path.add(new Pose(30, 4, 180, 0.7, 8));
            runAction(new PurePursuit(path, -50, 1500));
            path.clear();

            ActionVariables.score = true;
            while (!ActionVariables.doneScore) {
            }
        }


        //Park
        Robot.getLift().wantedLiftPosition = Lift.LiftPositions.NULL;
        Robot.getLift().setLiftPosition(0);
        switch (signal) {
            case 1:
                path.add(new Pose(12, 12, 0, speed, 8));
                runAction(new PurePursuit(path, 90));
                break;
            case 2:
                path.add(new Pose(35, 12, 0, speed, 8));
                runAction(new PurePursuit(path, -90));
                break;
            case 3:
                path.add(new Pose(60, 12, 0, speed, 8));
                runAction(new PurePursuit(path));
                break;
        }
        path.clear();
    }

    @Override
    public StartPosition side() {
        return null;
    }
}
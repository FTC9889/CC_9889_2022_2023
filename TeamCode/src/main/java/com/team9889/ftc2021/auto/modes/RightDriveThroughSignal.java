package com.team9889.ftc2021.auto.modes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.ftc2021.auto.AutoModeBase;
import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.auto.actions.ActionVariables;
import com.team9889.ftc2021.auto.actions.drive.DriveToPole;
import com.team9889.ftc2021.auto.actions.drive.PurePursuit;
import com.team9889.ftc2021.auto.actions.drive.ResetPosition;
import com.team9889.ftc2021.auto.actions.drive.Turn;
import com.team9889.ftc2021.auto.actions.lift.Grab;
import com.team9889.ftc2021.auto.actions.lift.Score;
import com.team9889.ftc2021.auto.actions.lift.SetGrabber;
import com.team9889.ftc2021.auto.actions.lift.SetV4BPosition;
import com.team9889.ftc2021.auto.actions.utl.ParallelAction;
import com.team9889.ftc2021.auto.actions.utl.Wait;
import com.team9889.ftc2021.subsystems.Lift;
import com.team9889.ftc2021.subsystems.Robot;
import com.team9889.ftc2021.test.drive.DriveToPoleWithCam;
import com.team9889.lib.CruiseLib;
import com.team9889.lib.Pose;
import com.team9889.lib.Pose2d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by edm11 on 11/5/2022.
 */

@Autonomous(preselectTeleOp = "Teleop")
public class RightDriveThroughSignal extends AutoModeBase {
    ElapsedTime timer = new ElapsedTime();

    @Override
    public void initialize() {

    }

    @Override
    public void run(StartPosition startPosition) {
        ArrayList<Pose> path = new ArrayList<>();
        Robot.getMecanumDrive().position = new Pose2d(31, 64, 0);
        Robot.getMecanumDrive().angleOffset = 0;

        timer.reset();
        Robot.getLift().wantedScoreState = Lift.ScoreStates.GROUND_RIGHT;

        path.add(new Pose(35, 57, -150, 1, 6));
        path.add(new Pose(36.1, 12, -180, 1, 8));
        path.add(new Pose(26, 7, -180, .4, 8));
        runAction(new ParallelAction(Arrays.asList(new PurePursuit(path, -45, 2700),
                new Score(Lift.LiftPositions.HIGH, true, 1000))));
        path.clear();

        Robot.getMecanumDrive().setBumpersDown();


        for (int i = 0; i < 5 && timer.milliseconds() < 25000 && opModeIsActive(); i++) {
            path.add(new Pose(36, 12, 0, 1, 4));
            path.add(new Pose(44, 12, 0, 1, 4));
            runAction(new PurePursuit(path, new Pose(3, 3, 4), -90, 2000, true));
            path.clear();

            path.add(new Pose(63, 12, 0, 0.2, 4));
            runAction(new ParallelAction(Arrays.asList(new PurePursuit(path, -90, 1000, true),
                    new Grab(CruiseLib.limitValue(3.5 - (i * (13.0 / 8.0)), 10, 0)))));
            path.clear();

            Robot.getMecanumDrive().position.setX(60);


            path.add(new Pose(45, 10, -180, 1, 8));
            path.add(new Pose(28, 7, -180, .5, 6));
            runAction(new ParallelAction(Arrays.asList(new PurePursuit(path, -45, 2100),
                    new Score(Lift.LiftPositions.HIGH, true, 250))));
            path.clear();
        }


        //Park
        Robot.getMecanumDrive().setBumpersUp();
        Robot.getLift().wantedLiftPosition = Lift.LiftPositions.NULL;
        Robot.getLift().setLiftPosition(0);
        switch (signal) {
            case 1:
                path.add(new Pose(15, 12, 0, 1, 8));
                runAction(new PurePursuit(path, 90));
                break;
            case 2:
                path.add(new Pose(35, 12, 0, 1, 8));
                runAction(new PurePursuit(path, -90));
                break;
            case 3:
                path.add(new Pose(60, 12, 0, 1, 8));
                runAction(new PurePursuit(path));
                break;
        }
        path.clear();
        runAction(new Wait(1000));
    }

    @Override
    public StartPosition side() {
        return null;
    }
}
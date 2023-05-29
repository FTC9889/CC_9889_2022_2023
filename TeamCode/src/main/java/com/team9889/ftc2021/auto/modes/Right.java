package com.team9889.ftc2021.auto.modes;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.ftc2021.auto.AutoModeBase;
import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.auto.actions.ActionVariables;
import com.team9889.ftc2021.auto.actions.drive.DetectLine;
import com.team9889.ftc2021.auto.actions.drive.DriveOnLine;
import com.team9889.ftc2021.auto.actions.drive.DriveToPole;
import com.team9889.ftc2021.auto.actions.drive.PurePursuit;
import com.team9889.ftc2021.auto.actions.drive.ResetPosition;
import com.team9889.ftc2021.auto.actions.drive.Turn;
import com.team9889.ftc2021.auto.actions.lift.Grab;
import com.team9889.ftc2021.auto.actions.lift.Score;
import com.team9889.ftc2021.auto.actions.lift.SetGrabber;
import com.team9889.ftc2021.auto.actions.lift.SetLift;
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
public class Right extends AutoModeBase {
    ElapsedTime timer = new ElapsedTime();

    @Override
    public void initialize() {
        Robot.getCamera().scanForSignal.left = false;
    }

    @Override
    public void run(StartPosition startPosition) {
        ArrayList<Pose> path = new ArrayList<>();
        Robot.getMecanumDrive().position = new Pose2d(35, 61, 0);
        Robot.getMecanumDrive().angleOffset = 0;

        Robot.camera.setPipeline(Robot.getCamera().scanForPole);
        Robot.leds.setPower(1);

        timer.reset();
        Robot.getLift().wantedScoreState = Lift.ScoreStates.GROUND_RIGHT;
        Robot.getMecanumDrive().setBumpersUp();

        path.add(new Pose(35, 22, 180, 1, 8));
        path.add(new Pose(30, 4, 180, .5, 8));
        runAction(new ParallelAction(Arrays.asList(new PurePursuit(path, -57, 3000),
                new SetLift(Lift.LiftPositions.HIGH, Lift.ScoreStates.HOVER_LEFT, 1000))));
        path.clear();

        runAction(new Score(Lift.LiftPositions.HIGH, true, 0));

        if (!useLEDs) {
            Robot.leds.setPower(0);
        }

        for (int i = 0; i < 5 && timer.milliseconds() < 24000 && opModeIsActive(); i++) {
            Robot.getLift().wantedScoreState = Lift.ScoreStates.GROUND_LEFT;

            path.add(new Pose(38, 12, 0, 1, 5));
            path.add(new Pose(50, 12, 0, 1, 5));
            runAction(new PurePursuit(path, -90, 5000, true));
            path.clear();

            Robot.getMecanumDrive().setBumpersDown();
            runAction(new Wait(150));

            runAction(new ParallelAction(Arrays.asList(
                    new DriveOnLine(false, 2000),
                    new Grab(CruiseLib.limitValue(3.5 - (i * (13.0 / 8.0)), 10, 0)))
            ));

            if (Math.abs(Robot.getMecanumDrive().position.getX() + 60) < 15) {
                Robot.getMecanumDrive().position.setX(60);
            }

            path.add(new Pose(39, 12, 180, .9, 6));
            path.add(new Pose(30, 4.5, 180, .7, 3));
            runAction(new ParallelAction(Arrays.asList(new PurePursuit(path, new Pose(1, 1, 2), -58, 3000),
                    new SetLift(Lift.LiftPositions.HIGH, Lift.ScoreStates.HOVER_LEFT, 200))));
            path.clear();

            Robot.getMecanumDrive().setBumpersUp();
            runAction(new Wait(200));

//            runAction(new DriveToPole(1500, new Pose(24, 0, 0), 7));

            Robot.getLift().wantedScoreState = Lift.ScoreStates.PLACE_LEFT;
            while (Robot.getLift().wantedScoreState != Lift.ScoreStates.RETRACT && opModeIsActive()) {
                Robot.update();
            }
            runAction(new Wait(200));
        }


        //Park
        Robot.getMecanumDrive().setBumpersUp();
        Robot.getLift().wantedLiftPosition = Lift.LiftPositions.NULL;
        Robot.getLift().setLiftPosition(0);
        switch (signal) {
            case 1:
//                path.add(new Pose(-32, 10, 0, 1, 4));
//                path.add(new Pose(-12, 16, 0, 1, 8, .5));
//                runAction(new PurePursuit(path, 90, true));

                path.add(new Pose(33, 12, 0, 1, 4));
                path.add(new Pose(10, 16, 0, 1, 8));
                runAction(new PurePursuit(path, 0, 5000, true, 40));
                break;
            case 2:
                path.add(new Pose(36, 14, 0, 1, 8));
                runAction(new PurePursuit(path, 0));
                break;
            case 3:
                path.add(new Pose(60, 14, 0, 1, 8));
                runAction(new PurePursuit(path, -90));
                break;
        }
        path.clear();
        runAction(new Wait(1000));
    }

    @Override
    public StartPosition side() {
        return StartPosition.RIGHT;
    }
}

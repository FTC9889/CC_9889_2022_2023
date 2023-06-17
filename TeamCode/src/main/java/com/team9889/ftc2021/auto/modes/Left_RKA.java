package com.team9889.ftc2021.auto.modes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.ftc2021.auto.AutoModeBase;
import com.team9889.ftc2021.auto.actions.drive.DetectLine;
import com.team9889.ftc2021.auto.actions.drive.DriveToPole;
import com.team9889.ftc2021.auto.actions.drive.PurePursuit;
import com.team9889.ftc2021.auto.actions.drive.Turn;
import com.team9889.ftc2021.auto.actions.lift.Grab;
import com.team9889.ftc2021.auto.actions.lift.Score;
import com.team9889.ftc2021.auto.actions.lift.SetLift;
import com.team9889.ftc2021.auto.actions.utl.ParallelAction;
import com.team9889.ftc2021.auto.actions.utl.Wait;
import com.team9889.ftc2021.subsystems.Lift;
import com.team9889.lib.CruiseLib;
import com.team9889.lib.Pose;
import com.team9889.lib.Pose2d;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by edm11 on 11/5/2022.
 */

@Autonomous(preselectTeleOp = "Teleop")
public class Left_RKA extends AutoModeBase {
    ElapsedTime timer = new ElapsedTime();

    @Override
    public void initialize() {
        Robot.getCamera().scanForSignal.left = true;
    }

    @Override
    public void run(StartPosition startPosition) {
        ArrayList<Pose> path = new ArrayList<>();
        Robot.getMecanumDrive().position = new Pose2d(-35, 61, 0);
        Robot.getMecanumDrive().angleOffset = 0;

        Robot.camera.setPipeline(Robot.getCamera().scanForPole);
        Robot.leds.setPower(1);

        timer.reset();
        Robot.getLift().wantedScoreState = Lift.ScoreStates.GROUND_RIGHT;

        path.add(new Pose(-35, 22, 180, 1, 8));
        path.add(new Pose(-34, 0, 180, .5, 8));
        runAction(new ParallelAction(Arrays.asList(new PurePursuit(path, 0, 3000),
                new SetLift(Lift.LiftPositions.HIGH, Lift.ScoreStates.HOVER_LEFT, 1000))));
//                new Score(Lift.LiftPositions.HIGH, true, 1000))));
        path.clear();

        runAction(new Turn(90));

        path.add(new Pose(-32, 0, 180, .5, 5));
        runAction(new PurePursuit(path, 90));
        path.clear();

        runAction(new DriveToPole(1500, new Pose(24, 0, 0), 7));

        while (timer.milliseconds() < 9000) {
            path.add(new Pose(-32, 0, 180, .5, 5));
            runAction(new PurePursuit(path, 90, 1000, true));
            path.clear();
        }

        runAction(new ParallelAction(Arrays.asList(new DriveToPole(1500, new Pose(-24, 0, 0), 7),
                new Score(Lift.LiftPositions.HIGH, true, 0))));
        path.clear();

        Robot.getMecanumDrive().setBumpersDown();
        if (!useLEDs) {
            Robot.leds.setPower(0);
        }

        for (int i = 0; i < 5 && timer.milliseconds() < 24000 && opModeIsActive(); i++) {
            path.add(new Pose(-35, 12, 0, 1, 5));
            runAction(new PurePursuit(path, 90));
            path.clear();

            path.add(new Pose(-50, 12, 0, .8, 5));
            path.add(new Pose(-53, 12, 0, .6, 5));
//            runAction(new PurePursuit(path, new Pose(3, 3, 4), 90, 2000));
//            path.clear();

            path.add(new Pose(-63, 12, 0, 0.4, 3));
            runAction(new ParallelAction(Arrays.asList(
                    new PurePursuit(path, 90, 3000, true, true),
                    new DetectLine(true, true),
                    new Grab(CruiseLib.limitValue(3.5 - (i * (13.0 / 8.0)), 10, 0)))));
            path.clear();

            if (Math.abs(Robot.getMecanumDrive().position.getX() + 60) < 15) {
                Robot.getMecanumDrive().position.setX(-61);
            }

//            Robot.getMecanumDrive().position.setHeading(Robot.getMecanumDrive().getAngle().getTheda(AngleUnit.RADIANS));


            path.add(new Pose(-42, 12, 180, .9, 6));
//            path.add(new Pose(-30, 6, -180, .6, 4));
            path.add(new Pose(-30, 4, 180, .5, 3));
            runAction(new ParallelAction(Arrays.asList(new PurePursuit(path, new Pose(1, 1, 2), 54, 3000),
                    new SetLift(Lift.LiftPositions.HIGH, Lift.ScoreStates.HOVER_LEFT, 200))));
            path.clear();


            runAction(new Wait(200));

            runAction(new DriveToPole(1500, new Pose(-24, 0, 0), 7));

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
            case 3:
//                path.add(new Pose(-32, 10, 0, 1, 4));
//                path.add(new Pose(-12, 16, 0, 1, 8, .5));
//                runAction(new PurePursuit(path, 90, true));

                path.add(new Pose(-33, 12, 0, 1, 4));
                path.add(new Pose(-10, 16, 0, 1, 8));
                runAction(new PurePursuit(path, 0, 5000, true, 40));
                break;
            case 2:
                path.add(new Pose(-36, 14, 0, 1, 8));
                runAction(new PurePursuit(path, 0));
                break;
            case 1:
                path.add(new Pose(-59, 14, 0, 1, 8));
                runAction(new PurePursuit(path, 90));
                break;
        }
        path.clear();
        runAction(new Wait(1000));
    }

    @Override
    public StartPosition side() {
        return StartPosition.LEFT;
    }
}
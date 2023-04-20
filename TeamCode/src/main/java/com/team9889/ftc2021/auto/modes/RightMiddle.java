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

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by edm11 on 11/5/2022.
 */

@Autonomous(preselectTeleOp = "Teleop")
public class RightMiddle extends AutoModeBase {
    ElapsedTime timer = new ElapsedTime();
    int a = 0;

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

        path.add(new Pose(35, 22, 180, 1, 8));
        path.add(new Pose(30, 4, 180, .5, 8));
        runAction(new ParallelAction(Arrays.asList(new PurePursuit(path, -57, 3000),
                new SetLift(Lift.LiftPositions.HIGH, Lift.ScoreStates.HOVER_LEFT, 1000))));
//                new Score(Lift.LiftPositions.HIGH, true, 1000))));
        path.clear();

        runAction(new Wait(200));

        runAction(new ParallelAction(Arrays.asList(new DriveToPole(1500, new Pose(24, 0, 0), 7),
                new Score(Lift.LiftPositions.HIGH, true, 0))));
        path.clear();

        Robot.getMecanumDrive().setBumpersDown();
        if (!useLEDs) {
            Robot.leds.setPower(0);
        }


        for (int i = 0; i < 5 && timer.milliseconds() < 24000 && opModeIsActive(); i++) {
//            path.add(new Pose(-40, 12, 0, .3, 3));
            path.add(new Pose(38, 12, 0, .7, 3));
            runAction(new PurePursuit(path, new Pose(3, 3, 4)));
            path.clear();

            runAction(new Turn(-90));

            path.add(new Pose(53, 12, 0, .6, 5));

            path.add(new Pose(61, 12, 0, 0.4, 4));
            runAction(new ParallelAction(Arrays.asList(
                    new PurePursuit(path, -90, 3000, true, true),
                    new DetectLine(true, false),
                    new Grab(CruiseLib.limitValue(3.5 - (i * (13.0 / 8.0)), 10, 0)))));
            path.clear();

            if (Math.abs(Robot.getMecanumDrive().position.getX() + 60) < 15) {
                Robot.getMecanumDrive().position.setX(60);
            }

            Robot.getMecanumDrive().position.setHeading(Robot.getMecanumDrive().getAngle().getTheda(AngleUnit.RADIANS));


            if (i == 4 && signal == 3) {
                path.add(new Pose(15, 12, -180, .9, 6));
                path.add(new Pose(6, 21, -180, .6, 8));
                runAction(new ParallelAction(Arrays.asList(new PurePursuit(path, new Pose(1, 1, 2), -132, 2400),
                        new SetLift(Lift.LiftPositions.HIGH, Lift.ScoreStates.HOVER_LEFT, 200))));
                path.clear();

                runAction(new Wait(200));

                runAction(new ParallelAction(Arrays.asList(new DriveToPole(300, new Pose(0, 24, 0), 7, 130, 116),
                        new Score(Lift.LiftPositions.HIGH, true, 0))));
                path.clear();
            } else {
//                path.add(new Pose(-45, 16, -180, .9, 6));
//                path.add(new Pose(-29, 20, -180, .6, 2));
                path.add(new Pose(39, 12, -180, .9, 8));
                runAction(new ParallelAction(Arrays.asList(new PurePursuit(path, new Pose(3, 3, 3), -90, 1800))));
                path.clear();

                runAction(new Turn(-135));

                path.add(new Pose(29, 19, -180, .5, 3));
                runAction(new ParallelAction(Arrays.asList(new PurePursuit(path, new Pose(2, 2, 2), -135, 1800),
                        new SetLift(Lift.LiftPositions.MEDIUM, Lift.ScoreStates.HOVER_LEFT, 200))));
                path.clear();

                runAction(new Wait(200));

                runAction(new ParallelAction(Arrays.asList(new DriveToPole(400, new Pose(24, 24, 0), 5, 120),
                        new Score(Lift.LiftPositions.MEDIUM, true, 0))));
                path.clear();
            }
        }


        //Park
        Robot.getMecanumDrive().setBumpersUp();
        Robot.getLift().wantedLiftPosition = Lift.LiftPositions.NULL;
        Robot.getLift().setLiftPosition(0);
        switch (signal) {
            case 1:
                path.add(new Pose(12, 8, 0, 1, 8));
                path.add(new Pose(12, 12, 0, 1, 8));
                runAction(new PurePursuit(path, 180, 5000, true, 40));
                break;
            case 2:
                path.add(new Pose(38, 14, 0, 1, 8));
                runAction(new PurePursuit(path, 0));
                break;
            case 3:
                path.add(new Pose(60, 14, 0, 1, 8));
                runAction(new PurePursuit(path));
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

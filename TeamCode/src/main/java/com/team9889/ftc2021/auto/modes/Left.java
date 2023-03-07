package com.team9889.ftc2021.auto.modes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.ftc2021.auto.AutoModeBase;
import com.team9889.ftc2021.auto.actions.drive.DetectLine;
import com.team9889.ftc2021.auto.actions.drive.DriveToPole;
import com.team9889.ftc2021.auto.actions.drive.PurePursuit;
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
public class Left extends AutoModeBase {
    ElapsedTime timer = new ElapsedTime();

    @Override
    public void initialize() {

    }

    @Override
    public void run(StartPosition startPosition) {
        ArrayList<Pose> path = new ArrayList<>();
        Robot.getMecanumDrive().position = new Pose2d(-35, 60, 0);
        Robot.getMecanumDrive().angleOffset = 0;

        Robot.camera.setPipeline(Robot.getCamera().scanForPole);

        timer.reset();
        Robot.getLift().wantedScoreState = Lift.ScoreStates.GROUND_RIGHT;

        path.add(new Pose(-35, 22, -180, 1, 8));
        path.add(new Pose(-29, 6, -180, .5, 8));
        runAction(new ParallelAction(Arrays.asList(new PurePursuit(path, 35, 3000),
                new SetLift(Lift.LiftPositions.HIGH, Lift.ScoreStates.HOVER_LEFT, 1000))));
//                new Score(Lift.LiftPositions.HIGH, true, 1000))));
        path.clear();

        runAction(new Wait(100));

        runAction(new ParallelAction(Arrays.asList(new DriveToPole(1000, new Pose(-24, 0, 0)),
                new Score(Lift.LiftPositions.HIGH, true, 0))));
        path.clear();

        Robot.getMecanumDrive().setBumpersDown();


        for (int i = 0; i < 5 && timer.milliseconds() < 24300 && opModeIsActive(); i++) {
            path.add(new Pose(-38, 12, 0, 1, 5));
            path.add(new Pose(-53, 12, 0, 1, 5));
//            runAction(new PurePursuit(path, new Pose(3, 3, 4), 90, 2000));
//            path.clear();

            path.add(new Pose(-63, 12, 0, 0.25, 8));
            runAction(new ParallelAction(Arrays.asList(
                    new PurePursuit(path, 90, 3000, true, true),
                    new DetectLine(true, true),
                    new Grab(CruiseLib.limitValue(3.5 - (i * (13.0 / 8.0)), 10, 0)))));
            path.clear();

            Robot.getMecanumDrive().position.setX(-60);


            path.add(new Pose(-39, 12, -180, 1, 6));
            path.add(new Pose(-30, 7, -180, .4, 4));
            runAction(new ParallelAction(Arrays.asList(new PurePursuit(path, new Pose(3, 3, 4), 50, 2100),
                    new SetLift(Lift.LiftPositions.HIGH, Lift.ScoreStates.HOVER_LEFT, 200))));
            path.clear();

            runAction(new Wait(200));

            runAction(new ParallelAction(Arrays.asList(new DriveToPole(2000, new Pose(-24, 0, 0)),
                    new Score(Lift.LiftPositions.HIGH, true, 0))));
            path.clear();
        }


        //Park
        Robot.getMecanumDrive().setBumpersUp();
        Robot.getLift().wantedLiftPosition = Lift.LiftPositions.NULL;
        Robot.getLift().setLiftPosition(0);
        switch (signal) {
            case 1:
                path.add(new Pose(-12, 14, 0, 1, 8));
                runAction(new PurePursuit(path, -90));
                break;
            case 2:
                path.add(new Pose(-36, 14, 0, 1, 8));
                runAction(new PurePursuit(path, 0));
                break;
            case 3:
                path.add(new Pose(-60, 14, 0, 1, 8));
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
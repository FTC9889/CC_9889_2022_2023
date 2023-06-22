package com.team9889.ftc2021.auto;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.team9889.ftc2021.Team9889Linear;
import com.team9889.ftc2021.auto.actions.drive.DriveOnLine;
import com.team9889.ftc2021.auto.actions.drive.DriveToPole;
import com.team9889.ftc2021.auto.actions.drive.PurePursuit;
import com.team9889.ftc2021.auto.actions.drive.Turn;
import com.team9889.ftc2021.auto.actions.lift.Grab;
import com.team9889.ftc2021.auto.actions.lift.Score;
import com.team9889.ftc2021.auto.actions.lift.SetLift;
import com.team9889.ftc2021.auto.actions.lift.SetLiftHeight;
import com.team9889.ftc2021.auto.actions.utl.ParallelAction;
import com.team9889.ftc2021.subsystems.Lift;
import com.team9889.lib.CruiseLib;
import com.team9889.lib.Pose;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Jin on 11/10/2017.
 */

public class Autonomous extends AutoModeBase {
    ArrayList<Pose> path = new ArrayList<>();

    enum Junctions{
        CENTER_HIGH, MEDIUM, SAFE_HIGH, NULL;
    }
    Junctions lastJunction = Junctions.NULL;

    @Override
    public void initialize() {

    }

    @Override
    public void run(StartPosition startPosition) {

    }

    @Override
    public StartPosition side() {
        return null;
    }

    // |-------------------------|
    // |        Preloaded        |
    // |-------------------------|
    public void PreloadedHighJunction() {
        path.add(new Pose(35 * StartPosition.getNum(side()), 22, 180, 1, 8));
        path.add(new Pose(30.5 * StartPosition.getNum(side()), 6, 180, .5, 12));
        runAction(new ParallelAction(Arrays.asList(
                new PurePursuit(path, new Pose(1, 1, 1), -42 * StartPosition.getNum(side()), 3000),
                new SetLift(Lift.LiftPositions.HIGH, Lift.ScoreStates.HOVER_LEFT, 800))));
        path.clear();

        runAction(new ParallelAction(Arrays.asList(
                new DriveToPole(1500, new Pose(24 * StartPosition.getNum(side()), 0, 0), 2),
                new Score(Lift.LiftPositions.HIGH, true, 0))));
        path.clear();
    }

    public void PreloadedHighJunctionDefensive() {
        path.add(new Pose(35 * StartPosition.getNum(side()), 22, 180, 1, 8));
        path.add(new Pose(32 * StartPosition.getNum(side()), 0, 180, .5, 12));
        runAction(new ParallelAction(Arrays.asList(
                new PurePursuit(path, new Pose(1, 1, 1), -90 * StartPosition.getNum(side()), 3000),
                new SetLift(Lift.LiftPositions.HIGH, Lift.ScoreStates.HOVER_LEFT, 800))));
        path.clear();

        runAction(new ParallelAction(Arrays.asList(
                new DriveToPole(1500, new Pose(24 * StartPosition.getNum(side()), 0, 0), 2),
                new Score(Lift.LiftPositions.HIGH, true, 0))));
        path.clear();
    }

    public void PreloadedMediumJunction() {
        path.add(new Pose(35 * StartPosition.getNum(side()), 22, 180, 1, 8));
        path.add(new Pose(34 * StartPosition.getNum(side()), 12, 180, 1, 12));
        runAction(new ParallelAction(Arrays.asList(
                new PurePursuit(path, new Pose(3, 3, 4), 0 * StartPosition.getNum(side()), 3000),
                new SetLift(Lift.LiftPositions.MEDIUM, Lift.ScoreStates.HOVER_LEFT, 800))));
        path.clear();

        path.add(new Pose(30.5 * StartPosition.getNum(side()), 19, 180, 1, 12));
        runAction(new ParallelAction(Arrays.asList(
                new PurePursuit(path, new Pose(2, 2, 2), -132 * StartPosition.getNum(side()), 3000, 48),
                new SetLift(Lift.LiftPositions.MEDIUM, Lift.ScoreStates.HOVER_LEFT, 800))));
        path.clear();

        runAction(new ParallelAction(Arrays.asList(
                new DriveToPole(1500, new Pose(24 * StartPosition.getNum(side()), 0, 0), 2, 160, 124),
                new Score(Lift.LiftPositions.MEDIUM, true, 0))));
//        runAction(new Score(Lift.LiftPositions.MEDIUM, true, 0));
        path.clear();
    }

    public void PreloadedSafeHighJunction() {
        path.add(new Pose(35 * StartPosition.getNum(side()), 22, 180, 1, 8));
        path.add(new Pose(30.5 * StartPosition.getNum(side()), 10, 180, .5, 6));
        path.add(new Pose(6.5 * StartPosition.getNum(side()), 18, 180, .5, 6));
        runAction(new ParallelAction(Arrays.asList(
                new PurePursuit(path, new Pose(1, 1, 1), -132 * StartPosition.getNum(side()), 3000),
                new SetLift(Lift.LiftPositions.HIGH, Lift.ScoreStates.HOVER_LEFT, 800))));
        path.clear();

        runAction(new ParallelAction(Arrays.asList(
                new DriveToPole(1500, new Pose(24 * StartPosition.getNum(side()), 0, 0), 2),
                new Score(Lift.LiftPositions.HIGH, true, 0))));
        path.clear();

        path.add(new Pose(31 * StartPosition.getNum(side()), 12, 180, .7, 6));
    }



    // |-------------------------|
    // |          Stack          |
    // |-------------------------|
    public void GrabOffStack(int num) {
        path.add(new Pose(38 * StartPosition.getNum(side()), 12, 0, 1, 5));
        path.add(new Pose(52 * StartPosition.getNum(side()), (lastJunction == Junctions.MEDIUM ? 13 : 12), 0, 1, 5));
        runAction(new ParallelAction(Arrays.asList(
                new PurePursuit(path, new Pose(3, 3, 5), -90 * StartPosition.getNum(side()), 5000, true, true),
                new SetLiftHeight(3.5 - (num * (13.0 / 8.0)), 300))));
        path.clear();

        Robot.getMecanumDrive().setBumpersDown();
        Robot.getLift().wantedV4BPosition = Lift.V4BPositions.RIGHT;
        Robot.getLift().openGrabber();

        runAction(new DriveOnLine(side() == StartPosition.LEFT, 5000));

        runAction(new Grab(CruiseLib.limitValue(3.5 - (num * (13.0 / 8.0)), 10, 0)));

        Robot.getMecanumDrive().setBumpersUp();
    }



    // |-------------------------|
    // |          Place          |
    // |-------------------------|
    public void PlaceOnHighJunction() {
        path.add(new Pose(44 * StartPosition.getNum(side()), 12, 180, 1, 4));
        path.add(new Pose(31 * StartPosition.getNum(side()), 6, 180, .7, 6));
        runAction(new ParallelAction(Arrays.asList(new PurePursuit(path, new Pose(1, 1, 1), -48 * StartPosition.getNum(side()), 3000),
                new SetLift(Lift.LiftPositions.HIGH, Lift.ScoreStates.HOVER_LEFT, 200))));
        path.clear();

        runAction(new DriveToPole(1500, new Pose(24 * StartPosition.getNum(side()), 0, 0), 2));

        Robot.getLift().wantedScoreState = Lift.ScoreStates.PLACE_LEFT;
        while (Robot.getLift().wantedScoreState != Lift.ScoreStates.RETRACT && opModeIsActive()) {
            Robot.update();
        }

        lastJunction = Junctions.CENTER_HIGH;
    }

    public void PlaceOnHighJunctionDefensive() {
        path.add(new Pose(39 * StartPosition.getNum(side()), 12, 180, 1, 4));
        path.add(new Pose(32 * StartPosition.getNum(side()), 0, 180, .7, 6));
        runAction(new ParallelAction(Arrays.asList(new PurePursuit(path, new Pose(1, 1, 1), -90 * StartPosition.getNum(side()), 3000),
                new SetLift(Lift.LiftPositions.HIGH, Lift.ScoreStates.HOVER_LEFT, 200))));
        path.clear();

        runAction(new DriveToPole(1500, new Pose(24 * StartPosition.getNum(side()), 0, 0), 2));

        Robot.getLift().wantedScoreState = Lift.ScoreStates.PLACE_LEFT;
        while (Robot.getLift().wantedScoreState != Lift.ScoreStates.RETRACT && opModeIsActive()) {
            Robot.update();
        }

        lastJunction = Junctions.CENTER_HIGH;
    }

    public void PlaceOnMediumJunction() {
        path.add(new Pose(44 * StartPosition.getNum(side()), 12, 180, 1, 4));
        path.add(new Pose(30.5 * StartPosition.getNum(side()), 19, 180, .55, 6));
        runAction(new ParallelAction(Arrays.asList(new PurePursuit(path, new Pose(1, 1, 1), -134 * StartPosition.getNum(side()), 3000),
                new SetLift(Lift.LiftPositions.MEDIUM, Lift.ScoreStates.HOVER_LEFT, 200))));
        path.clear();

        runAction(new DriveToPole(1500, new Pose(24 * StartPosition.getNum(side()), 24, 0), 2, 160, 136));

        Robot.getLift().wantedScoreState = Lift.ScoreStates.PLACE_LEFT;
        while (Robot.getLift().wantedScoreState != Lift.ScoreStates.RETRACT && opModeIsActive()) {
            Robot.update();
        }

        lastJunction = Junctions.MEDIUM;
    }

    public void PlaceOnSafeHighJunction() {
        path.add(new Pose(15 * StartPosition.getNum(side()), 12, 180, 1, 4));
        path.add(new Pose(7 * StartPosition.getNum(side()), 18, 180, .7, 6));

        Robot.getLift().wantedLiftPosition = Lift.LiftPositions.DOWN;
        runAction(new ParallelAction(Arrays.asList(new PurePursuit(path, new Pose(1, 1, 1), -136 * StartPosition.getNum(side()), 3000),
                new SetLift(Lift.LiftPositions.HIGH, Lift.ScoreStates.HOVER_LEFT, 1200))));
        path.clear();

        runAction(new DriveToPole(1500, new Pose(0, 24, 0), 2));

        Robot.getLift().wantedScoreState = Lift.ScoreStates.PLACE_LEFT;
        while (Robot.getLift().wantedScoreState != Lift.ScoreStates.RETRACT && opModeIsActive()) {
            Robot.update();
        }

        lastJunction = Junctions.SAFE_HIGH;

        path.add(new Pose(31 * StartPosition.getNum(side()), 12, 180, 1, 6));
    }



    // |-------------------------|
    // |          Park           |
    // |-------------------------|
    public void Park() {
        path.clear();

        Robot.getMecanumDrive().setBumpersUp();
        Robot.getLift().wantedLiftPosition = Lift.LiftPositions.NULL;
        Robot.getLift().setLiftPosition(0);

        if (side() == StartPosition.LEFT) {
            switch (signal) {
                case 1:
                    if (lastJunction == Junctions.SAFE_HIGH)
                        path.add(new Pose(-36, 14, 0, 1, 8));

                    path.add(new Pose(-60, 14, 0, 1, 8));
                    runAction(new PurePursuit(path, 90));
                    break;
                case 2:
                    path.add(new Pose(-36, 14, 0, 1, 8));

                    if (lastJunction == Junctions.MEDIUM)
                        runAction(new PurePursuit(path, 180));
                    else
                        runAction(new PurePursuit(path, 0));
                    break;
                case 3:
                    if (lastJunction != Junctions.SAFE_HIGH)
                        path.add(new Pose(-33, 12, 0, 1, 4));

                    path.add(new Pose(-12, 14, 0, 1, 8));
                    runAction(new PurePursuit(path, 0, 5000, true, 40));
                    break;
            }
        } else {
            switch (signal) {
                case 1:
                    if (lastJunction != Junctions.SAFE_HIGH)
                        path.add(new Pose(33, 12, 0, 1, 4));

                    path.add(new Pose(12, 14, 0, 1, 8));

                    if (lastJunction == Junctions.MEDIUM)
                        runAction(new PurePursuit(path, -90, 5000, true, 40));
                    else
                        runAction(new PurePursuit(path, 0, 5000, true, 40));
                    break;
                case 2:
                    path.add(new Pose(36, 14, 0, 1, 8));

                    if (lastJunction == Junctions.MEDIUM)
                        runAction(new PurePursuit(path, 180));
                    else
                        runAction(new PurePursuit(path, 0));
                    break;
                case 3:
                    if (lastJunction == Junctions.SAFE_HIGH)
                        path.add(new Pose(36, 14, 0, 1, 8));

                    path.add(new Pose(60, 14, 0, 1, 8));
                    runAction(new PurePursuit(path, -90));
                    break;
            }
        }
        path.clear();
    }
}

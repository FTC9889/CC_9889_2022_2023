package com.team9889.ftc2021.auto.modes.test;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.team9889.ftc2021.auto.AutoModeBase;
import com.team9889.ftc2021.auto.actions.ActionVariables;
import com.team9889.ftc2021.auto.actions.drive.DriveToPole;
import com.team9889.ftc2021.auto.actions.drive.PurePursuit;
import com.team9889.ftc2021.auto.actions.lift.Grab;
import com.team9889.ftc2021.auto.actions.lift.Score;
import com.team9889.ftc2021.auto.actions.utl.ParallelAction;
import com.team9889.ftc2021.auto.actions.utl.Wait;
import com.team9889.ftc2021.subsystems.Lift;
import com.team9889.lib.CruiseLib;
import com.team9889.lib.Pose;
import com.team9889.lib.Pose2d;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by edm11 on 2/5/2023.
 */

@Autonomous
@Config
@Disabled
public class GrabOffStack extends AutoModeBase {
    public static int conesOnStack = 5;

    @Override
    public void initialize() {

    }

    @Override
    public void run(StartPosition startPosition) {
        ArrayList<Pose> path = new ArrayList<>();
        Robot.getMecanumDrive().position = new Pose2d(0, 0, 0);
        Robot.getMecanumDrive().angleOffset = 0;

        Robot.getLift().wantedV4BPosition = Lift.V4BPositions.UP;
        runAction(new Wait(1000));

        for (int i = 0; i < 5 && opModeIsActive(); i++) {
            path.add(new Pose(0, 24, 0, 0.5, 8));
            runAction(new ParallelAction(Arrays.asList(new PurePursuit(path, 0, 1200, true),
                    new Grab(CruiseLib.limitValue(3.5 - (i * (13.0 / 8.0)), 10, 0)))));
            path.clear();

            path.add(new Pose(0, 0, 180, 0.8, 8));
            runAction(new PurePursuit(path, 0, 1000));
            path.clear();

            ActionVariables.doneDriving = true;
            runAction(new Score(Lift.LiftPositions.LOW, true));
            runAction(new Wait(1000));
        }
    }

    @Override
    public StartPosition side() {
        return null;
    }
}

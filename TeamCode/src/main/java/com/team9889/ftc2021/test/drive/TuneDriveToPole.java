package com.team9889.ftc2021.test.drive;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.team9889.ftc2021.auto.AutoModeBase;
import com.team9889.ftc2021.auto.actions.drive.DriveToPole;
import com.team9889.ftc2021.auto.actions.drive.PurePursuit;
import com.team9889.ftc2021.auto.actions.utl.Wait;
import com.team9889.ftc2021.subsystems.Lift;
import com.team9889.lib.Pose;
import com.team9889.lib.Pose2d;

import java.util.ArrayList;

/**
 * Created by Eric on 5/25/2022.
 */

@Autonomous
@Config
public class TuneDriveToPole extends AutoModeBase{
    public static double speed = 1, radius = 8;

    @Override
    public void run(StartPosition startPosition) {
        waitForStart(true);

        Robot.getMecanumDrive().position = new Pose2d(0, 0, 0);

        Robot.getLift().wantedLiftPosition = Lift.LiftPositions.HIGH;
        Robot.getLift().wantedScoreState = Lift.ScoreStates.HOVER_LEFT;
        runAction(new Wait(1000));

        runAction(new DriveToPole(5000));
    }

    @Override
    public StartPosition side() {
        return null;
    }

    @Override
    public void initialize() {

    }
}

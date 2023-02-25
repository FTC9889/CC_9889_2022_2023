package com.team9889.ftc2021.test.drive;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.team9889.ftc2021.auto.AutoModeBase;
import com.team9889.ftc2021.auto.actions.drive.PurePursuit;
import com.team9889.ftc2021.auto.actions.drive.Turn;
import com.team9889.ftc2021.auto.actions.utl.RobotUpdate;
import com.team9889.ftc2021.auto.actions.utl.Wait;
import com.team9889.lib.Pose;
import com.team9889.lib.Pose2d;

import java.util.ArrayList;

/**
 * Created by Eric on 5/25/2022.
 */

@Autonomous
@Config
public class TunePurePursuit extends AutoModeBase{
    public static double speed = 1, radius = 8;

    @Override
    public void run(AutoModeBase.StartPosition startPosition) {
        ArrayList<Pose> path1 = new ArrayList<>(), path2 = new ArrayList<>();
        waitForStart(true);

        Robot.getMecanumDrive().position = new Pose2d(0, 0, -Math.PI / 2);

        while (opModeIsActive()) {
            path1.add(new Pose(0, 48, 90, speed, radius));
            runAction(new PurePursuit(path1, -90, 4000));
            path1.clear();

            runAction(new Wait(1000));

            path1.add(new Pose(0.1, 0, -90, speed, radius));
            runAction(new PurePursuit(path1, -90, 4000));
            path1.clear();

            runAction(new Wait(1000));
        }
    }

    @Override
    public StartPosition side() {
        return null;
    }

    @Override
    public void initialize() {

    }
}

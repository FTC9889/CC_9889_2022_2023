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


//        while (opModeIsActive()) {
//            path1.add(new Pose(0.1, 40, 0, speed, radius, 1));
//            path2.add(new Pose(0, 0, 180, speed, radius, 1));
//
//            runAction(new PurePursuit(path1));
//
//            runAction(new Wait(1000));
//
//            runAction(new PurePursuit(path2));
//        }

//        while (opModeIsActive()) {
//            runAction(new Turn(180));
////            runAction(new Wait(500));
//            runAction(new Turn(0));
////            runAction(new Wait(500));
//        }

        while (opModeIsActive()) {
            path1.add(new Pose(0.1, 20, 0, speed, radius));
            runAction(new PurePursuit(path1));
            path1.clear();
            runAction(new Turn(90));

            path1.add(new Pose(-20, 20, 0, speed, radius));
            runAction(new PurePursuit(path1));
            path1.clear();
            runAction(new Turn(180));

            path1.add(new Pose(-20, 0, 0, speed, radius));
            runAction(new PurePursuit(path1));
            path1.clear();
            runAction(new Turn(-90));

            path1.add(new Pose(0.1, 0, 0, speed, radius));
            runAction(new PurePursuit(path1));
            path1.clear();
            runAction(new Turn(0));
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

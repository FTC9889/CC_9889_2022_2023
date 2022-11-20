package com.team9889.ftc2021.test.drive;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.team9889.ftc2021.auto.AutoModeBase;
import com.team9889.ftc2021.auto.actions.drive.PurePursuit;
import com.team9889.ftc2021.auto.actions.utl.RobotUpdate;
import com.team9889.ftc2021.auto.actions.utl.Wait;
import com.team9889.lib.Pose;

import java.util.ArrayList;

/**
 * Created by Eric on 5/25/2022.
 */

@Autonomous
public class TunePurePursuit extends AutoModeBase{
    @Override
    public void run(AutoModeBase.StartPosition startPosition) {
        ArrayList<Pose> path1 = new ArrayList<>(), path2 = new ArrayList<>();
        waitForStart(true);

        boolean reverse = false;

//        Robot.getMecanumDrive().position.setHeading(Math.PI);

        path1.add(new Pose(0.1, 27, 0, .4, 8, 1, reverse));
        path1.add(new Pose(-24, 27, 0, .4, 8, 1, reverse));
        path1.add(new Pose(-24.1, 51, 0, .4, 8, 1, reverse));
        path1.add(new Pose(24, 51, 0, .4, 8, 1, reverse));
        path1.add(new Pose(24.1, 27, 0, .4, 8, 1, reverse));
        path1.add(new Pose(0, 27, 0, .4, 8, 1, reverse));

        path2.add(new Pose(24.1, 27, 0, .4, 8, 1, !reverse));
        path2.add(new Pose(24, 51, 0, .4, 8, 1, !reverse));
        path2.add(new Pose(-24.1, 51, 0, .4, 8, 1, !reverse));
        path2.add(new Pose(-24, 27, 0, .4, 8, 1, !reverse));
        path2.add(new Pose(0.1, 27, 0, .4, 8, 1, !reverse));


        runAction(new PurePursuit(path1));

        runAction(new Wait(1000));

        runAction(new PurePursuit(path2));

//            runAction(new PurePursuit(path2));
//
//            runAction(new Wait(1000));
    }

    @Override
    public StartPosition side() {
        return null;
    }

    @Override
    public void initialize() {

    }
}

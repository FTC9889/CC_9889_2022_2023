package com.team9889.ftc2021.test.drive;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.team9889.ftc2021.auto.AutoModeBase;
import com.team9889.ftc2021.auto.actions.drive.PurePursuit;
import com.team9889.ftc2021.subsystems.Lift;
import com.team9889.ftc2021.subsystems.Robot;
import com.team9889.lib.Pose;
import com.team9889.lib.Pose2d;

import java.util.ArrayList;

/**
 * Created by edm11 on 1/14/2023.
 */

@Autonomous
public class TuneOdoError extends AutoModeBase {
    @Override
    public void initialize() {
    }

    @Override
    public void run(StartPosition startPosition) {
        Robot.getLift().wantedV4BPosition = Lift.V4BPositions.UP;

        ArrayList<Pose> path = new ArrayList<>();

        Robot.getMecanumDrive().position = new Pose2d(0, 0, 0);
        Robot.getMecanumDrive().angleOffset = 0;

        path.add(new Pose(0, 300, 0, 1, 8));
        runAction(new PurePursuit(path, new Pose(1, 1, 2)));
        path.clear();

        while (opModeIsActive()) {
            Pose2d position = Robot.getMecanumDrive().position;
            telemetry.addData("Position", position.getX() + ", " + position.getY() + ", " + position.getHeading());
            telemetry.update();
            Robot.update();
        }
    }

    @Override
    public StartPosition side() {
        return null;
    }
}

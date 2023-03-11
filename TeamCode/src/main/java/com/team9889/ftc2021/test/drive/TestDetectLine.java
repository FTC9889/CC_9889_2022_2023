package com.team9889.ftc2021.test.drive;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.team9889.ftc2021.auto.AutoModeBase;
import com.team9889.ftc2021.auto.actions.drive.DetectLine;
import com.team9889.lib.Pose2d;

/**
 * Created by edm11 on 2/25/2023.
 */

@TeleOp
@Disabled
public class TestDetectLine extends AutoModeBase {
    @Override
    public void initialize() {

    }

    @Override
    public void run(StartPosition startPosition) {
        Robot.getMecanumDrive().position = new Pose2d(60, 0, Math.PI / 2);

        runAction(new DetectLine(true, false));
    }

    @Override
    public StartPosition side() {
        return null;
    }
}

package com.team9889.ftc2021.test.drive;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.team9889.ftc2021.auto.AutoModeBase;
import com.team9889.ftc2021.auto.actions.utl.Wait;
import com.team9889.ftc2021.subsystems.Robot;
import com.team9889.lib.Pose2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * Created by edm11 on 10/22/2022.
 */

@Autonomous
@Config
@Disabled
public class AngleTest extends AutoModeBase {
    public static int time = 5000, count = 10;

    boolean dec = false;

    @Override
    public void initialize() {

    }

    @Override
    public void run(StartPosition startPosition) {
        waitForStart(true);


        Robot.getMecanumDrive().setPower(0, 0, .6);
        Robot.update();

        while (count != 0) {
            if (Math.abs(Robot.getMecanumDrive().getAngle().getTheda(AngleUnit.DEGREES)) < 5) {
                if (dec) {
                    count -= 1;
                    dec = false;
                }
            } else {
                dec = true;
            }

            Robot.outputToTelemetry(telemetry);
            telemetry.update();

            Robot.update();
        }

        Robot.getMecanumDrive().setPower(0, 0, 0);
        Robot.update();

        while (opModeIsActive()) {
            Pose2d position = Robot.getMecanumDrive().position;
            telemetry.addData("Position", position.getX() + ", " + position.getY() + ", " + position.getHeading());
            telemetry.update();
        }
    }

    @Override
    public StartPosition side() {
        return null;
    }
}

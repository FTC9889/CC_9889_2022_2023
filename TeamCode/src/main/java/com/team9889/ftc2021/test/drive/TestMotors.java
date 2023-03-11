package com.team9889.ftc2021.test.drive;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.team9889.ftc2021.DriverStation;
import com.team9889.ftc2021.Team9889Linear;

/**
 * Created by Eric on 12/5/2020.
 */
@TeleOp(group = "Test")
@Disabled
public class TestMotors extends Team9889Linear {

    @Override
    public void runOpMode() {
        DriverStation driverStation = new DriverStation(gamepad1, gamepad2);
        waitForStart(false);

        while (opModeIsActive()) {
            if (gamepad1.a) {
                Robot.fLDrive.setPower(1);
            } else {
                Robot.fLDrive.setPower(0);
            }

            if (gamepad1.b) {
                Robot.fRDrive.setPower(1);
            } else {
                Robot.fRDrive.setPower(0);
            }

            if (gamepad1.y) {
                Robot.bLDrive.setPower(1);
            } else {
                Robot.bLDrive.setPower(0);
            }

            if (gamepad1.x) {
                Robot.bRDrive.setPower(1);
            } else {
                Robot.bRDrive.setPower(0);
            }

            telemetry.addData("Left Front", Robot.fLDrive.getPosition());
            telemetry.addData("Right Front", Robot.fRDrive.getPosition());
            telemetry.addData("Left Back", Robot.bLDrive.getPosition());
            telemetry.addData("Right Back", Robot.bRDrive.getPosition());
            telemetry.update();

//            Robot.update();
        }
    }

    @Override
    public void initialize() {

    }
}

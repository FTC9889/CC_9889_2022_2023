package com.team9889.ftc2021.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.team9889.ftc2021.Team9889Linear;
import com.team9889.ftc2021.subsystems.Robot;

import java.util.Arrays;

/**
 * Created by edm11 on 3/11/2023.
 */

@TeleOp(group = "Test")
public class TestSensors extends Team9889Linear {
    @Override
    public void initialize() {

    }

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart(false);

        Robot.camera.setPipeline(Robot.getCamera().scanForPole);

        while (opModeIsActive()) {
            // Camera
            telemetry.addLine("Camera");
            telemetry.addData("Pole Camera", Robot.getCamera().scanForPole.getPoint());
            telemetry.addLine();
            telemetry.addLine();

            telemetry.addLine("Color Sensors");
            telemetry.addData("Left Color", leftDetect() + ", blue: " + Robot.leftColor.blue()
                    + ", red: " + Robot.leftColor.red());
            telemetry.addData("Center Color", centerDetect() + ", blue: " + Robot.centerColor.blue()
                    + ", red: " + Robot.centerColor.red());
            telemetry.addData("Right Color", rightDetect() + ", blue: " + Robot.rightColor.blue()
                    + ", red: " + Robot.rightColor.red());
            telemetry.addLine();
            telemetry.addLine();

//            telemetry.addLine("Light Sensors");
//            telemetry.addData("Left Light", leftDetect() + ", " + Robot.leftLight.getVoltage());
//            telemetry.addData("Center Light", centerDetect() + ", " + Robot.centerLight.getVoltage());
//            telemetry.addData("Right Light", rightDetect() + ", " + Robot.rightLight.getVoltage());
//            telemetry.addLine();
//            telemetry.addLine();

            telemetry.addLine("Odometry");
            telemetry.addData("Odometry 1", Robot.bRDrive.getPosition());
            telemetry.addData("Odometry 2", Robot.bLDrive.getPosition());
            telemetry.addData("Odometry 3", Robot.fRDrive.getPosition());
            telemetry.addData("Position", Arrays.toString(Robot.getMecanumDrive().position.getArray()));
            telemetry.addLine();
            telemetry.addLine();

            telemetry.addLine("Lift");
            telemetry.addData("Lift Height", Robot.getLift().getLiftPosition());
            telemetry.addData("Lift Down", Robot.getLift().liftDown);

            telemetry.update();
            Robot.update();
        }
    }


    boolean leftDetect() {
        return Robot.leftColor.blue() > 60 || Robot.leftColor.red() > 60;
    }

    boolean centerDetect() {
        return Robot.centerColor.blue() > 400 || Robot.centerColor.red() > 240;
    }

    boolean rightDetect() {
        return Robot.rightColor.blue() > 180 || Robot.rightColor.red() > 180;
    }
}

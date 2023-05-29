package com.team9889.ftc2021.test;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.team9889.ftc2021.Team9889Linear;
import com.team9889.ftc2021.auto.actions.drive.DetectLine;
import com.team9889.ftc2021.subsystems.Robot;
import com.team9889.lib.control.math.cartesian.Vector2d;

import java.util.Arrays;

/**
 * Created by edm11 on 3/11/2023.
 */

@TeleOp(group = "Test")
public class TestSensors extends Team9889Linear {
    Vector2d leftToRobot = new Vector2d(-38 / 25.4, 104 / 25.4), centerToRobot = new Vector2d(-8 / 25.4, 104 / 25.4), rightToRobot = new Vector2d(42 / 25.4, 104 / 25.4);

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

            double offset = 0;
            double heading = Robot.getMecanumDrive().position.getHeading();
            if (leftDetect()) {
                offset = (12 - (Math.sin(heading) * leftToRobot.getX())) + (Math.cos(heading) * leftToRobot.getY());
            } else if (rightDetect()) {
                offset = (12 - (Math.sin(heading) * rightToRobot.getX())) + (Math.cos(heading) * rightToRobot.getY());
            }
            telemetry.addData("Offset", offset);

//            double heading = Robot.getInstance().getMecanumDrive().position.getHeading();
            heading *= -1;
//            Robot.getInstance().color = "black";
//            if (rightDetect() && centerDetect()) {
//                Robot.getInstance().getMecanumDrive().position.setY((12 - (Math.sin(heading) * (rightToRobot.getX() + centerToRobot.getX()) / 2))
//                        + (Math.cos(heading) * (rightToRobot.getY() + centerToRobot.getY()) / 2));
//                Robot.getInstance().color = "red";
//            } else if (leftDetect() && centerDetect()) {
//                Robot.getInstance().getMecanumDrive().position.setY((12 - (Math.sin(heading) * (leftToRobot.getX() + centerToRobot.getX()) / 2))
//                        + (Math.cos(heading) * (leftToRobot.getY() + centerToRobot.getY()) / 2));
//                Robot.getInstance().color = "red";
//            } else if (rightDetect()) {
//                Robot.getInstance().getMecanumDrive().position.setY((12 - (Math.sin(heading) * rightToRobot.getX()))
//                        + (Math.cos(heading) * rightToRobot.getY()));
//                Robot.getInstance().color = "red";
//            } else if (centerDetect()) {
//                Robot.getInstance().getMecanumDrive().position.setY((12 - (Math.sin(heading) * centerToRobot.getX()))
//                        + (Math.cos(heading) * centerToRobot.getY()));
//                Robot.getInstance().color = "red";
//            } else if (leftDetect()) {
//                Robot.getInstance().getMecanumDrive().position.setY((12 - (Math.sin(heading) * leftToRobot.getX()))
//                        + (Math.cos(heading) * leftToRobot.getY()));
//                Robot.getInstance().color = "red";
//            }

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


    boolean farLeftDetect() {
        return Robot.farLeftColor.blue() > 60 || Robot.farLeftColor.red() > 60;
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

    boolean farRightDetect() {
        return Robot.farRightColor.blue() > 180 || Robot.farRightColor.red() > 180;
    }
}

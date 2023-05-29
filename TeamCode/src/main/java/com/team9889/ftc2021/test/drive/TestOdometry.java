package com.team9889.ftc2021.test.drive;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.team9889.ftc2021.DriverStation;
import com.team9889.ftc2021.Team9889Linear;
import com.team9889.ftc2021.subsystems.Robot;
import com.team9889.lib.Pose2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * Created by edm11 on 5/27/2023.
 */

@TeleOp
public class TestOdometry extends Team9889Linear {
    @Override
    public void initialize() {

    }

    @Override
    public void runOpMode() throws InterruptedException {
        DriverStation driverStation = new DriverStation(gamepad1, gamepad2);
        Robot.driverStation = driverStation;
        waitForStart(false);

        while(opModeIsActive()) {
            Robot.getMecanumDrive().xSpeed = gamepad1.left_stick_x;
            Robot.getMecanumDrive().ySpeed = -gamepad1.left_stick_y;
            Robot.getMecanumDrive().turnSpeed = gamepad1.right_stick_x * 0.85;



            TelemetryPacket packet = new TelemetryPacket();
            Pose2d pose = Robot.getMecanumDrive().position;
            packet.fieldOverlay()
                    .setFill("green")
                    .fillRect(pose.getX() - 6.5, pose.getY() - 6.5, 13, 13)
                    .setFill("black")
                    .strokeLine(pose.getX(), pose.getY(),
                            pose.getX() + (5 * Math.cos(pose.getHeading())),
                            pose.getY() + (5 * Math.sin(pose.getHeading())));
            packet.put("Odometry Angle", Math.toDegrees(pose.getHeading()));

            pose = Robot.getMecanumDrive().position2WheelLeft;
            packet.fieldOverlay()
                    .setFill("red")
                    .fillRect(pose.getX() - 6.5, pose.getY() - 6.5, 13, 13)
                    .setFill("black")
                    .strokeLine(pose.getX(), pose.getY(),
                            pose.getX() + (5 * Math.cos(pose.getHeading())),
                            pose.getY() + (5 * Math.sin(pose.getHeading())));

//            pose = Robot.getMecanumDrive().position2WheelRight;
//            packet.fieldOverlay()
//                    .setFill("blue")
//                    .fillRect(pose.getX() - 6.5, pose.getY() - 6.5, 13, 13)
//                    .setFill("black")
//                    .strokeLine(pose.getX(), pose.getY(),
//                            pose.getX() + (5 * Math.cos(pose.getHeading())),
//                            pose.getY() + (5 * Math.sin(pose.getHeading())));
//
//            pose = Robot.getMecanumDrive().position2WheelArc;
//            packet.fieldOverlay()
//                    .setFill("yellow")
//                    .fillRect(pose.getX() - 6.5, pose.getY() - 6.5, 13, 13)
//                    .setFill("black")
//                    .strokeLine(pose.getX(), pose.getY(),
//                            pose.getX() + (5 * Math.cos(pose.getHeading())),
//                            pose.getY() + (5 * Math.sin(pose.getHeading())));

            packet.put("Gyro Angle", Robot.getMecanumDrive().getAngle().getTheda(AngleUnit.DEGREES));
            FtcDashboard.getInstance().sendTelemetryPacket(packet);


            Robot.update();
        }
    }
}

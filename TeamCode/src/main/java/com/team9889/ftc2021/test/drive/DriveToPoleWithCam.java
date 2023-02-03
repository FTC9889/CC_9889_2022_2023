package com.team9889.ftc2021.test.drive;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.team9889.ftc2021.Team9889Linear;
import com.team9889.ftc2021.subsystems.Lift;
import com.team9889.ftc2021.subsystems.Robot;

/**
 * Created by edm11 on 1/31/2023.
 */

@TeleOp
@Config
public class DriveToPoleWithCam extends Team9889Linear {
    public static double xDivider = 270, yDivider = 1000;

    @Override
    public void initialize() {

    }

    @Override
    public void runOpMode() {
        waitForStart(false);

        while (opModeIsActive()) {
            Robot.getLift().wantedScoreState = Lift.ScoreStates.HOLDING;

            if (gamepad1.a) {
                double x = (Robot.getCamera().scanForPole.getPoint().x - 135);
                double xSpeed = ((-1E-07 * Math.pow(x, 3)) + (2E-19 * Math.pow(x, 2)) + (0.006 * x)) * 1.5 + 1E-14;
                double ySpeed = 0.18 * Math.log(270 - Robot.getCamera().scanForPole.width) - 0.3291;

                if (Robot.getCamera().scanForPole.width > 60) {
                    ySpeed = 0;
                }

                Robot.getMecanumDrive().setPower(-xSpeed, -ySpeed, 0);
            } else {
                Robot.getMecanumDrive().setPower(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
            }

            Robot.outputToTelemetry(telemetry);
            telemetry.update();
            Robot.update();
        }
    }
}

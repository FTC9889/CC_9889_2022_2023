package com.team9889.ftc2021.test.drive;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.team9889.ftc2021.auto.AutoModeBase;
import com.team9889.ftc2021.auto.actions.utl.Wait;
import com.team9889.ftc2021.subsystems.Robot;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * Created by edm11 on 10/22/2022.
 */

@Autonomous
@Config
public class AngleTest extends AutoModeBase {
    public static int time = 5000, count = 10;

    boolean dec = false;

    @Override
    public void initialize() {

    }

    @Override
    public void run(StartPosition startPosition) {
        waitForStart(true);


        Robot.getMecanumDrive().setPower(0, 0, .3);
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
        runAction(new Wait(30000));
    }

    @Override
    public StartPosition side() {
        return null;
    }
}

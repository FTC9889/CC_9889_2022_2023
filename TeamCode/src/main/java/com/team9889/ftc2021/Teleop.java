package com.team9889.ftc2021;


import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.ftc2021.subsystems.Lift;
import com.team9889.lib.CruiseLib;
import com.team9889.lib.Pose2d;

import java.nio.channels.GatheringByteChannel;
import java.util.Arrays;

/**
 * Created by MannoMation on 1/14/2019.
 */

@TeleOp
@Config
public class Teleop extends Team9889Linear {
    ElapsedTime timer = new ElapsedTime();
    boolean left = false;

    @Override
    public void runOpMode() {
        DriverStation driverStation = new DriverStation(gamepad1, gamepad2);
        Robot.driverStation = driverStation;

        waitForStart(false);
        Robot.getLift().wantedLiftPosition = Lift.LiftPositions.DOWN;

        timer.reset();

        while (opModeIsActive()) {
            /* Drive */
//            if (Math.abs(driverStation.getX()) > 0.05)
//                Robot.getMecanumDrive().xSpeed = (0.0774 * Math.exp(2.5584 * driverStation.getX())) * CruiseLib.getSign(driverStation.getX());
//            if (Math.abs(driverStation.getY()) > 0.05)
//                Robot.getMecanumDrive().ySpeed = 0.0774 * Math.exp(2.5584 * driverStation.getY());
            Robot.getMecanumDrive().xSpeed = driverStation.getX();
            Robot.getMecanumDrive().ySpeed = driverStation.getY();
            Robot.getMecanumDrive().turnSpeed = driverStation.getSteer();


            /* Lift */
            if (Math.abs(driverStation.getLiftPower()) > 0.1) {
                Robot.getLift().wantedLiftPosition = Lift.LiftPositions.NULL;
//                Robot.getLift().wantedScoreState = Lift.ScoreStates.NULL;
                Robot.getLift().setLiftPower(driverStation.getLiftPower());
            } else if (driverStation.getLow()) {
                Robot.getLift().wantedLiftPosition = Lift.LiftPositions.LOW;
                Robot.getLift().wantedScoreState = Lift.ScoreStates.HOLDING;
            } else if (driverStation.getMedium()) {
                Robot.getLift().wantedLiftPosition = Lift.LiftPositions.MEDIUM;
                Robot.getLift().wantedScoreState = Lift.ScoreStates.HOLDING;
            } else if (driverStation.getHigh()) {
                Robot.getLift().wantedLiftPosition = Lift.LiftPositions.HIGH;
                Robot.getLift().wantedScoreState = Lift.ScoreStates.HOLDING;
            } else if (driverStation.getDown()) {
                Robot.getLift().wantedLiftPosition = Lift.LiftPositions.DOWN;
                Robot.getLift().wantedScoreState = Lift.ScoreStates.HOLDING;
            } else if (Robot.getLift().wantedLiftPosition == Lift.LiftPositions.NULL){
                Robot.getLift().setLiftPower(0.1);
            }

            if (driverStation.getV4BLeft()) {
                Robot.getLift().wantedV4BPosition = Lift.V4BPositions.LEFT_DOWN;
                Robot.getLift().wantedScoreState = Lift.ScoreStates.NULL;
            } else if (driverStation.getV4BUp()) {
                Robot.getLift().wantedV4BPosition = Lift.V4BPositions.UP;
                Robot.getLift().wantedScoreState = Lift.ScoreStates.NULL;
                Robot.getLift().wantedPickupStates = Lift.PickupStates.NULL;
                driverStation.grabberClosed = true;
            } else if (driverStation.getV4BRight()) {
                Robot.getLift().wantedV4BPosition = Lift.V4BPositions.RIGHT_DOWN;
                Robot.getLift().wantedScoreState = Lift.ScoreStates.NULL;
            }

            if (driverStation.getGrabber()) {
                Robot.getLift().closeGrabber();
            } else {
                Robot.getLift().openGrabber();
            }

            int getScore = driverStation.getScore();
            if (getScore > 0) {
                left = false;
            } else if (getScore < 0) {
                left = true;
            }

            if (Robot.getLift().wantedLiftPosition == Lift.LiftPositions.DOWN) {
                if (getScore != 0) {
                    if (left) {
                        if (Robot.getLift().wantedPickupStates == Lift.PickupStates.HOVER_LEFT) {
                            Robot.getLift().wantedPickupStates = Lift.PickupStates.GRAB_LEFT;
                        } else {
                            Robot.getLift().wantedPickupStates = Lift.PickupStates.HOVER_LEFT;
                            driverStation.grabberClosed = false;
                        }
                    } else {
                        if (Robot.getLift().wantedPickupStates == Lift.PickupStates.HOVER_RIGHT) {
                            Robot.getLift().wantedPickupStates = Lift.PickupStates.GRAB_RIGHT;
                        } else {
                            Robot.getLift().wantedPickupStates = Lift.PickupStates.HOVER_RIGHT;
                            driverStation.grabberClosed = false;
                        }
                    }
                }

                Robot.getLift().wantedScoreState = Lift.ScoreStates.NULL;
            } else {
                if (Robot.getLift().wantedScoreState != Lift.ScoreStates.NULL && getScore != 0) {
                    if (left) {
                        if (Robot.getLift().wantedScoreState == Lift.ScoreStates.HOLDING ||
                                Robot.getLift().wantedScoreState == Lift.ScoreStates.HOVER_RIGHT) {
                            Robot.getLift().wantedScoreState = Lift.ScoreStates.HOVER_LEFT;
                        } else if (Robot.getLift().wantedScoreState == Lift.ScoreStates.HOVER_LEFT) {
                            Robot.getLift().wantedScoreState = Lift.ScoreStates.PLACE_LEFT;
                        }
                    } else {
                        if (Robot.getLift().wantedScoreState == Lift.ScoreStates.HOLDING ||
                                Robot.getLift().wantedScoreState == Lift.ScoreStates.HOVER_LEFT) {
                            Robot.getLift().wantedScoreState = Lift.ScoreStates.HOVER_RIGHT;
                        } else if (Robot.getLift().wantedScoreState == Lift.ScoreStates.HOVER_RIGHT) {
                            Robot.getLift().wantedScoreState = Lift.ScoreStates.PLACE_RIGHT;
                        }
                    }
                }

                Robot.getLift().wantedPickupStates = Lift.PickupStates.NULL;
            }

            /* Telemetry */
            telemetry.addData("Position", Arrays.toString(Robot.getMecanumDrive().position.getArray()));
            telemetry.addData("Score State", Robot.getLift().wantedScoreState.toString());
            telemetry.addData("Pickup State", Robot.getLift().wantedPickupStates.toString());
            telemetry.addData("Left", left);

//            TelemetryPacket packet = new TelemetryPacket();
//            packet.fieldOverlay().setFill("black").fillRect(Robot.getMecanumDrive().position.getY(),
//                    Robot.getMecanumDrive().position.getX(), 13, 13);

//            Pose2d position = Robot.getMecanumDrive().position;
//            double x = position.getX();
//            double y = position.getY();
//            packet.fieldOverlay().setFill("green").fillCircle(y + 7.5 + 5 * Math.cos(position.getHeading()),
//                    x + 7.5 - 5 * Math.sin(position.getHeading()), 2);
//            dashboard.sendTelemetryPacket(packet);

            Robot.outputToTelemetry(telemetry);
            telemetry.update();

            Robot.update();
        }
    }

    @Override
    public void initialize() {

    }
}

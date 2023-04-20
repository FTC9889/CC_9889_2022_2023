package com.team9889.ftc2021;


import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.robocol.RobocolParsableBase;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.ftc2021.subsystems.Lift;
import com.team9889.ftc2021.subsystems.Robot;
import com.team9889.lib.CruiseLib;
import com.team9889.lib.Pose2d;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.nio.channels.GatheringByteChannel;
import java.util.Arrays;

/**
 * Created by MannoMation on 1/14/2019.
 */

@TeleOp
@Config
public class Teleop extends Team9889Linear {
    public static double position = 0, led = 0.5;
    public static boolean LEDOn = false;

    ElapsedTime timer = new ElapsedTime();
    ElapsedTime pickupStackTimer = new ElapsedTime();
    boolean left = false, leftGround = false, rightGround = false, stack = false, first = true,
            autoDone = false;

    boolean pickupStack = false, high = false, releasedDpad = true;
    int stage = 0;

    @Override
    public void runOpMode() {
        DriverStation driverStation = new DriverStation(gamepad1, gamepad2);
        Robot.driverStation = driverStation;

        waitForStart(false);
        Robot.getLift().wantedLiftPosition = Lift.LiftPositions.DOWN;
        Robot.getLift().setV4BAngle(0);

        timer.reset();

        while (opModeIsActive()) {
            /* Drive */
//            if (Math.abs(driverStation.getX()) > 0.05)
//                Robot.getMecanumDrive().xSpeed = (0.0774 * Math.exp(2.5584 * driverStation.getX())) * CruiseLib.getSign(driverStation.getX());
//            if (Math.abs(driverStation.getY()) > 0.05)
//                Robot.getMecanumDrive().ySpeed = 0.0774 * Math.exp(2.5584 * driverStation.getY());
            Robot.getMecanumDrive().xSpeed = driverStation.getX();
            Robot.getMecanumDrive().ySpeed = driverStation.getY();
            Robot.getMecanumDrive().turnSpeed = driverStation.getSteer() * 0.85;


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
                driverStation.grabberClosed = true;
            } else if (driverStation.getV4BRight()) {
                Robot.getLift().wantedV4BPosition = Lift.V4BPositions.RIGHT_DOWN;
                Robot.getLift().wantedScoreState = Lift.ScoreStates.NULL;
            }

            if (gamepad1.right_trigger < 0.3 && gamepad1.left_trigger < 0.3 && !pickupStack) {
                if (driverStation.getGrabber()) {
                    Robot.getLift().closeGrabber();
                } else {
                    Robot.getLift().openGrabber();
                }
            }

            if (pickupStack) {
                if (stage == 0) {
                    Robot.getMecanumDrive().setBumpersDown();

                    Robot.getLift().wantedLiftPosition = Lift.LiftPositions.NULL;
                    Robot.getLift().wantedScoreState = Lift.ScoreStates.P_HOVER_RIGHT;

                    Robot.getLift().setLiftPosition((high ? 3.5 : 1));

                    if (gamepad1.right_bumper) {
                        stage = 1;
                        pickupStackTimer.reset();
                    }
                } else if (stage == 1) {
                    if (Robot.getLift().wantedScoreState == Lift.ScoreStates.HOLDING) {
                        Robot.getLift().setLiftPosition((high ? 3.5 : 1) + 5);
                    } else {
                        Robot.getLift().setLiftPosition((high ? 3.5 : 1));

                        Robot.getLift().wantedScoreState = Lift.ScoreStates.GRAB_RIGHT;
                    }

                     if (pickupStackTimer.milliseconds() > 500) {
                        Robot.getMecanumDrive().setBumpersUp();
                        Robot.getLift().wantedLiftPosition = Lift.LiftPositions.DOWN;

                        pickupStack = false;
                        stage = 0;
                    }
                }
            } else {
                int getGround = driverStation.getGround();
                int getRightGround = driverStation.getRightGround();
                if (getGround == 1 || leftGround) {
                    if (Robot.getLift().wantedScoreState != Lift.ScoreStates.GROUND_LEFT && getGround == 1) {
                        Robot.getLift().wantedScoreState = Lift.ScoreStates.GROUND_LEFT;
                        leftGround = true;
                    } else if (getGround == 1) {
                        Robot.getLift().wantedScoreState = Lift.ScoreStates.DROP;
                    }

                    if (Robot.getLift().wantedScoreState == Lift.ScoreStates.NULL ||
                            Robot.getLift().wantedScoreState == Lift.ScoreStates.LOWER) {
                        leftGround = false;
                    }
                } else if (getRightGround == 1 || rightGround) {
                    if (Robot.getLift().wantedScoreState != Lift.ScoreStates.GROUND_RIGHT &&
                            getRightGround == 1) {
                        Robot.getLift().wantedScoreState = Lift.ScoreStates.GROUND_RIGHT;
                        rightGround = true;
                    } else if (getRightGround == 1) {
                        Robot.getLift().wantedScoreState = Lift.ScoreStates.DROP;
                    }

                    if (Robot.getLift().wantedScoreState == Lift.ScoreStates.NULL ||
                            Robot.getLift().wantedScoreState == Lift.ScoreStates.LOWER) {
                        rightGround = false;
                    }
                } else {
                    int getScore = driverStation.getScore();
                    if (getScore > 0) {
                        left = false;
                    } else if (getScore < 0) {
                        left = true;
                    }

                    if (Robot.getLift().wantedLiftPosition == Lift.LiftPositions.DOWN) {
                        if (getScore != 0) {
                            if (left) {
                                if (Robot.getLift().wantedScoreState == Lift.ScoreStates.P_HOVER_LEFT) {
                                    Robot.getLift().wantedScoreState = Lift.ScoreStates.GRAB_LEFT;
                                } else {
                                    Robot.getLift().wantedScoreState = Lift.ScoreStates.P_HOVER_LEFT;
                                    driverStation.grabberClosed = false;
                                }
                            } else {
                                if (Robot.getLift().wantedScoreState == Lift.ScoreStates.P_HOVER_RIGHT) {
                                    Robot.getLift().wantedScoreState = Lift.ScoreStates.GRAB_RIGHT;
                                } else {
                                    Robot.getLift().wantedScoreState = Lift.ScoreStates.P_HOVER_RIGHT;
                                    driverStation.grabberClosed = false;
                                }
                            }
                        }
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
                    }
                }


                if (driverStation.getBumper()) {
                    Robot.getMecanumDrive().setBumpersDown();
                } else {
                    Robot.getMecanumDrive().setBumpersUp();
                }
            }

            if (releasedDpad) {
                if (gamepad1.dpad_up) {
                    pickupStack = true;
                    high = true;

                    releasedDpad = false;
                } else if (gamepad1.dpad_down) {
                    pickupStack = true;
                    high = false;

                    releasedDpad = false;
                }
            } else if (!gamepad1.dpad_up && !gamepad1.dpad_down) {
                releasedDpad = true;
            }

            if (first) {
                Robot.getLift().wantedScoreState = Lift.ScoreStates.HOLDING;
                first = false;
            } else if (autoDone && gamepad1.right_trigger < 0.15 && gamepad1.left_trigger < 0.15) {
                autoDone = false;
            }


            if (driverStation.getBeacon()) {
                if (Robot.getLift().beaconStage == 0) {
                    Robot.getLift().beaconStage = 1;
                } else {
                    Robot.getLift().beaconStage = 0;
                }
            }


            if (LEDOn) {
                Robot.leds.setPower(1);
            } else {
                Robot.leds.setPower(0);
            }


            /* Telemetry */
            telemetry.addData("Beacon Active", Robot.getLift().beaconStage != 0);
            telemetry.addData("Score State", Robot.getLift().wantedScoreState.toString());
            telemetry.addData("Grabber Open", Robot.getLift().grabberOpen);

            Robot.outputToTelemetry(telemetry);
            telemetry.update();

            Robot.update();
        }
    }

    @Override
    public void initialize() {

    }
}

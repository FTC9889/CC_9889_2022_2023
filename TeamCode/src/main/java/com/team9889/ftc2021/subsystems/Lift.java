package com.team9889.ftc2021.subsystems;

import android.util.Log;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.lib.CruiseLib;
import com.team9889.lib.control.controllers.PID;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * Created by edm11 on 11/5/2022.
 */

@Config
public class Lift extends Subsystem{
    public static PID pid = new PID(0.4, 0, 6);

    public int autoConeCount = 0;

    public static double high = 26;

    double maxSpeed = 0;

    public boolean liftDown = true, auto, grabberOpen = false;
    public int beaconStage = 0;
    public boolean liftInAutoPos = false;
    double beaconHeight = 0;

    double lastAngle = 10000;

    public enum V4BPositions {
        LEFT_DOWN, LEFT_GROUND, LEFT_DROP, LEFT, LEFT_UP, UP, RIGHT_UP, RIGHT, RIGHT_DROP, RIGHT_GROUND, RIGHT_DOWN, INIT, NULL
    }
    public V4BPositions wantedV4BPosition = V4BPositions.NULL;
    public V4BPositions currentV4BPosition = V4BPositions.NULL;

    public enum LiftPositions {
        DOWN, LOW, MEDIUM, HIGH, NULL
    }
    public LiftPositions wantedLiftPosition = LiftPositions.NULL;
    public LiftPositions currentLiftPosition = LiftPositions.NULL;

    public enum ScoreStates {
        HOLDING, P_HOVER_RIGHT, GRAB_RIGHT, HOVER_RIGHT, PLACE_RIGHT, P_HOVER_LEFT, GRAB_LEFT, HOVER_LEFT, PLACE_LEFT, GROUND_LEFT, GROUND_RIGHT, DROP, RETRACT, LOWER, NULL
    }
    public ScoreStates wantedScoreState = ScoreStates.NULL;
    public ScoreStates currentScoreState = ScoreStates.NULL;


    ElapsedTime scoreTimer = new ElapsedTime(), grabTimer = new ElapsedTime();

    @Override
    public void init(boolean auto) {
        this.auto = auto;
//        if (auto) {
//            wantedV4BPosition = Lift.V4BPositions.UP;
//        }

        wantedV4BPosition = V4BPositions.NULL;
        wantedLiftPosition = LiftPositions.NULL;
        wantedScoreState = ScoreStates.NULL;
        closeGrabber();

        autoConeCount = 0;
    }

    @Override
    public void outputToTelemetry(Telemetry telemetry) {
        telemetry.addData("V4B Pot", getV4BAngle());
//        telemetry.addData("Lift Height", getLiftPosition());
        telemetry.addData("Lift Down", liftDown);
        telemetry.addData("Lift State", wantedLiftPosition);
    }

    @Override
    public void update() {
        if (!Robot.getInstance().liftLimit.isPressed()) {
            liftDown = true;
            Robot.getInstance().leftLift.resetEncoder();
            Robot.getInstance().rightLift.resetEncoder();
        } else {
            liftDown = false;
        }


        switch (wantedV4BPosition) {
            case LEFT_DOWN:
                if (setV4BAngle(-135)) {
                    currentV4BPosition = V4BPositions.LEFT_DOWN;
                }
                break;

            case LEFT_GROUND:
                if (setV4BAngle((auto ? -100 : -120))) {
                    currentV4BPosition = V4BPositions.LEFT_DOWN;
                }
                break;

            case LEFT:
                if (setV4BAngle((auto ? -65 : -75))) {
                    currentV4BPosition = V4BPositions.LEFT;
                }
                break;

            case LEFT_DROP:
                if (setV4BAngle(-90)) {
                    currentV4BPosition = V4BPositions.LEFT_DROP;
                }
                break;

            case LEFT_UP:
                if (wantedLiftPosition == LiftPositions.HIGH || auto) {
                    if (setV4BAngle((auto ? -42 : -50), .15)) {
                        currentV4BPosition = V4BPositions.LEFT_UP;
                    }
                } else {
                    if (setV4BAngle(-50)) {
                        currentV4BPosition = V4BPositions.LEFT_UP;
                    }
                }
                break;

            case UP:
                if (setV4BAngle(0)) {
                    currentV4BPosition = V4BPositions.UP;
                }
                break;

            case RIGHT_UP:
                if (wantedLiftPosition == LiftPositions.HIGH || auto) {
                    if (setV4BAngle(50, .15)) {
                        currentV4BPosition = V4BPositions.RIGHT_UP;
                    }
                } else {
                    if (setV4BAngle(50)) {
                        currentV4BPosition = V4BPositions.RIGHT_UP;
                    }
                }
                break;

            case RIGHT:
                if (setV4BAngle((auto ? 70 : 80))) {
                    currentV4BPosition = V4BPositions.RIGHT;
                }
                break;

            case RIGHT_DROP:
                if (setV4BAngle(90)) {
                    currentV4BPosition = V4BPositions.RIGHT_DROP;
                }
                break;

            case RIGHT_GROUND:
                if (setV4BAngle(107)) {
                    currentV4BPosition = V4BPositions.LEFT_DOWN;
                }
                break;

            case RIGHT_DOWN:
                if (setV4BAngle(130)) {
                    currentV4BPosition = V4BPositions.RIGHT_DOWN;
                }
                break;

            case INIT:
                if (setV4BAngle(-40)) {
                    currentV4BPosition = V4BPositions.INIT;
                }
                break;

            case NULL:
                break;
        }


        // CHANGE ONLY TELEOP HEIGHTS
        switch (wantedLiftPosition) {
            case DOWN:
                maxSpeed = 0;
                setLiftPower(-0.2);
                if (liftDown) {
                    currentLiftPosition = LiftPositions.DOWN;
                }
                break;

            case LOW:
                if (setLiftPosition(7 - (beaconStage * 2))) {
                    currentLiftPosition = LiftPositions.LOW;
                }
                break;

            case MEDIUM:
                if (setLiftPosition((auto ? 16.5 : 16.5) - (beaconStage * 2))) {
                    currentLiftPosition = LiftPositions.MEDIUM;
                }
                break;

            case HIGH:
                if (setLiftPosition(high - (beaconStage))) {
                    currentLiftPosition = LiftPositions.HIGH;
                }
                break;

            case NULL:
                break;
        }


        switch (wantedScoreState) {
            case HOLDING:
                wantedV4BPosition = V4BPositions.UP;
                break;

            case P_HOVER_RIGHT:
                wantedV4BPosition = V4BPositions.RIGHT;
                openGrabber();
                grabTimer.reset();
                break;

            case GRAB_RIGHT:
                wantedV4BPosition = V4BPositions.RIGHT_DOWN;

                if (grabTimer.milliseconds() > (auto ? 150 : 120) && grabTimer.milliseconds() < 350) {
                    Robot.getInstance().driverStation.grabberClosed = true;
                    closeGrabber();
                } else if (auto ? (liftInAutoPos) : (grabTimer.milliseconds() > 350)) {
                    wantedScoreState = ScoreStates.HOLDING;
                }
                break;

            case HOVER_RIGHT:
                wantedV4BPosition = V4BPositions.RIGHT_UP;
                scoreTimer.reset();
                break;

            case PLACE_RIGHT:
                wantedV4BPosition = V4BPositions.RIGHT_DROP;

                if (scoreTimer.milliseconds() > 200) {
                    wantedScoreState = ScoreStates.DROP;
                    beaconHeight = getLiftPosition();

                    scoreTimer.reset();
                }
                break;

            case P_HOVER_LEFT:
                wantedV4BPosition = V4BPositions.LEFT;
                openGrabber();
                grabTimer.reset();
                break;

            case GRAB_LEFT:
                wantedV4BPosition = V4BPositions.LEFT_DOWN;

                if (grabTimer.milliseconds() > 120 && grabTimer.milliseconds() < 300) {
                    Robot.getInstance().driverStation.grabberClosed = true;
                    closeGrabber();
                } else if (grabTimer.milliseconds() > 300) {
                    wantedScoreState = ScoreStates.HOLDING;
                }
                break;

            case HOVER_LEFT:
                wantedV4BPosition = V4BPositions.LEFT_UP;
                scoreTimer.reset();
                break;

            case PLACE_LEFT:
                wantedV4BPosition = V4BPositions.LEFT_DROP;

                if (scoreTimer.milliseconds() > 120) {
                    wantedScoreState = ScoreStates.DROP;
                    beaconHeight = getLiftPosition();

                    scoreTimer.reset();
                }
                break;

            case GROUND_LEFT:
                wantedV4BPosition = V4BPositions.RIGHT_GROUND;
                scoreTimer.reset();
                break;

            case GROUND_RIGHT:
                wantedV4BPosition = V4BPositions.LEFT_GROUND;
                scoreTimer.reset();
                break;

            case DROP:
                    Robot.getInstance().driverStation.grabberClosed = false;
                    depositGrabber();

                    if (scoreTimer.milliseconds() > 250) {
                        wantedScoreState = ScoreStates.RETRACT;
                        scoreTimer.reset();
                    }
                break;

            case RETRACT:
                wantedV4BPosition = V4BPositions.UP;

                if (scoreTimer.milliseconds() > 100) {
                    Robot.getInstance().driverStation.grabberClosed = true;
                    closeGrabber();
                }

                if (scoreTimer.milliseconds() > 400) {
                    beaconStage = 0;

                    wantedScoreState = ScoreStates.LOWER;

                    closeGrabber();
                    scoreTimer.reset();
                }
                break;

            case LOWER:
                wantedLiftPosition = LiftPositions.DOWN;
                break;

            case NULL:
                break;
        }
        currentScoreState = wantedScoreState;
    }

    @Override
    public void stop() {
        closeGrabber();

//        Robot.getInstance().leftV4B.getController().pwmDisable();
//        Robot.getInstance().rightV4B.getController().pwmDisable();
        Robot.getInstance().grabber.getController().pwmDisable();
    }

    public boolean setLiftPosition(double position) {
//        if (auto) {
//            maxSpeed += 0.01;
//        } else {
            maxSpeed = 1;
//        }

        setLiftPower(CruiseLib.limitValue(pid.update(getLiftPosition(), position), 0, -0.3, 0, maxSpeed));
        return Math.abs(getLiftPosition() - position) < 2;
    }

    public void setLiftPower(double power) {
        if (power < 0 && liftDown) {
            Robot.getInstance().leftLift.setPower(0);
            Robot.getInstance().rightLift.setPower(0);
        } else {
            Robot.getInstance().leftLift.setPower(power);
            Robot.getInstance().rightLift.setPower(power);
        }
    }

    public double getLiftPosition() {
        return ((-Robot.getInstance().rightLift.getPosition()) / 145.1) * (2 * Math.PI);
    }


    public boolean setV4BAngle(double angle) {
//        angle -= 13;
        angle = angle - 4;
        double adjustedAngle = (angle / 270) + 0.5;
        Robot.getInstance().leftV4B.setPosition(adjustedAngle);
        Robot.getInstance().rightV4B.setPosition(adjustedAngle);
        lastAngle = angle;

        return Math.abs(getV4BAngle() - angle) < 5;
    }

    public boolean setV4BAngle(double angle, double time) {
//        angle -= 13;
        if (lastAngle != angle) {
            angle = angle - 4;
            double adjustedAngle = (angle / 270) + 0.5;

            if (auto) {
                Robot.getInstance().leftV4B.setPosition(adjustedAngle);
                Robot.getInstance().rightV4B.setPosition(adjustedAngle);
            } else {
                Robot.getInstance().leftV4B.setPosition(adjustedAngle, time);
                Robot.getInstance().rightV4B.setPosition(adjustedAngle, time);
            }

            lastAngle = angle;
        }
        return Math.abs(getV4BAngle() - angle) < 5;
    }

    public double getV4BAngle() {
        // Math from REV Robotics documents
//        double angle = Robot.getInstance().v4bPot.getVoltage() * 81.4;
//        double adjustedAngle = ((445.5 * (angle - 270)) / (Math.pow(angle, 2) - (270 * angle) - 36450)) * 81.1;
//        return adjustedAngle - 133;
        return 0;
    }

    public void setGrabber(double pos) {
//        if (getV4BAngle() > -30 && getV4BAngle() < 15) {
//            Robot.getInstance().grabber.setPosition(0.37);
//        } else {
            Robot.getInstance().grabber.setPosition(pos);
//        }
    }


    public void closeGrabber() {
        setGrabber(0.38);
//        Log.v("Open Grabber", "true");
        grabberOpen = false;
    }

    public void openGrabber() {
        double position = 0.58;
        if (auto) {
            position = (autoConeCount != 5 ? 0.64 : 0.59);
        }

        setGrabber(position);
        grabberOpen = true;
    }

    public void depositGrabber() {
        if (auto) {
            double position = 0.5;

            setGrabber(position);
            grabberOpen = true;
        }
    }
}

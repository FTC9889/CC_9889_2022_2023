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
    public static PID pid = new PID(0.4, 0, 0);

    public boolean liftDown = true;

    public enum V4BPositions {
        LEFT_DOWN, LEFT, LEFT_UP, UP, RIGHT_UP, RIGHT, RIGHT_DOWN, NULL
    }
    public V4BPositions wantedV4BPosition = V4BPositions.NULL;
    public V4BPositions currentV4BPosition = V4BPositions.NULL;

    public enum LiftPositions {
        DOWN, LOW, MEDIUM, HIGH, NULL
    }
    public LiftPositions wantedLiftPosition = LiftPositions.NULL;
    public LiftPositions currentLiftPosition = LiftPositions.NULL;

    public enum ScoreStates {
        HOLDING, HOVER_RIGHT, PLACE_RIGHT, HOVER_LEFT, PLACE_LEFT, DROP, RETRACT, LOWER, NULL
    }
    public ScoreStates wantedScoreState = ScoreStates.NULL;
    public ScoreStates currentScoreState = ScoreStates.NULL;

    public enum PickupStates {
        HOVER_RIGHT, GRAB_RIGHT, HOVER_LEFT, GRAB_LEFT, UP, NULL
    }
    public PickupStates wantedPickupStates = PickupStates.NULL;
    public PickupStates currentPickupStates = PickupStates.NULL;


    ElapsedTime scoreTimer = new ElapsedTime(), grabTimer = new ElapsedTime();

    @Override
    public void init(boolean auto) {
//        if (auto) {
//            wantedV4BPosition = Lift.V4BPositions.UP;
//        }

        wantedV4BPosition = V4BPositions.NULL;
        wantedLiftPosition = LiftPositions.NULL;
        wantedScoreState = ScoreStates.NULL;
        wantedPickupStates = PickupStates.NULL;
        closeGrabber();
    }

    @Override
    public void outputToTelemetry(Telemetry telemetry) {
//        telemetry.addData("V4B Pot", getV4BAngle());
//        telemetry.addData("Lift Height", getLiftPosition());
        telemetry.addData("Lift Down", liftDown);
        telemetry.addData("Lift State", wantedLiftPosition);
    }

    @Override
    public void update() {
        if (Robot.getInstance().liftLimit.isPressed()) {
            liftDown = true;
            Robot.getInstance().leftLift.resetEncoder();
            Robot.getInstance().rightLift.resetEncoder();
        } else {
            liftDown = false;
        }


        switch (wantedV4BPosition) {
            case LEFT_DOWN:
                if (setV4BAngle(-117)) {
                    currentV4BPosition = V4BPositions.LEFT_DOWN;
                }
                break;

            case LEFT:
                if (setV4BAngle(-60)) {
                    currentV4BPosition = V4BPositions.LEFT;
                }
                break;

            case LEFT_UP:
                if (setV4BAngle(-40)) {
                    currentV4BPosition = V4BPositions.LEFT;
                }
                break;

            case UP:
                if (setV4BAngle(10)) {
                    currentV4BPosition = V4BPositions.UP;
                }
                break;

            case RIGHT_UP:
                if (setV4BAngle(60)) {
                    currentV4BPosition = V4BPositions.RIGHT_UP;
                }
                break;

            case RIGHT:
                if (setV4BAngle(80)) {
                    currentV4BPosition = V4BPositions.RIGHT;
                }
                break;

            case RIGHT_DOWN:
                if (setV4BAngle(117)) {
                    currentV4BPosition = V4BPositions.RIGHT_DOWN;
                }
                break;

            case NULL:
                break;
        }


        switch (wantedLiftPosition) {
            case DOWN:
                setLiftPower(-0.2);
                if (liftDown) {
                    currentLiftPosition = LiftPositions.DOWN;
                }
                break;

            case LOW:
                if (setLiftPosition(6)) {
                    currentLiftPosition = LiftPositions.LOW;
                }
                break;

            case MEDIUM:
                if (setLiftPosition(17)) {
                    currentLiftPosition = LiftPositions.MEDIUM;
                }
                break;

            case HIGH:
                if (setLiftPosition(28)) {
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

            case HOVER_RIGHT:
                wantedV4BPosition = V4BPositions.RIGHT_UP;
                scoreTimer.reset();
                break;

            case PLACE_RIGHT:
                wantedV4BPosition = V4BPositions.RIGHT;

                if (scoreTimer.milliseconds() > 100) {
                    wantedScoreState = ScoreStates.DROP;
                    scoreTimer.reset();
                }
                break;

            case HOVER_LEFT:
                wantedV4BPosition = V4BPositions.LEFT_UP;
                scoreTimer.reset();
                break;

            case PLACE_LEFT:
                wantedV4BPosition = V4BPositions.LEFT;

                if (scoreTimer.milliseconds() > 100) {
                    wantedScoreState = ScoreStates.DROP;
                    scoreTimer.reset();
                }
                break;

            case DROP:
                Robot.getInstance().driverStation.grabberClosed = false;
                openGrabber();

                if (scoreTimer.milliseconds() > 200) {
                    wantedScoreState = ScoreStates.RETRACT;
                    scoreTimer.reset();
                }
                break;

            case RETRACT:
                Robot.getInstance().driverStation.grabberClosed = true;
                closeGrabber();
                wantedV4BPosition = V4BPositions.UP;

                if (scoreTimer.milliseconds() > 400) {
                    wantedScoreState = ScoreStates.LOWER;
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


        switch (wantedPickupStates) {
            case HOVER_LEFT:
                wantedV4BPosition = V4BPositions.LEFT;
                openGrabber();
                grabTimer.reset();
                break;

            case GRAB_LEFT:
                wantedV4BPosition = V4BPositions.LEFT_DOWN;

                if (grabTimer.milliseconds() > 200 && grabTimer.milliseconds() < 550) {
                    Robot.getInstance().driverStation.grabberClosed = true;
                    closeGrabber();
                } else if (grabTimer.milliseconds() > 550) {
                    wantedPickupStates = PickupStates.UP;
                }
                break;

            case HOVER_RIGHT:
                wantedV4BPosition = V4BPositions.RIGHT;
                openGrabber();
                grabTimer.reset();
                break;

            case GRAB_RIGHT:
                wantedV4BPosition = V4BPositions.RIGHT_DOWN;

                if (grabTimer.milliseconds() > 200 && grabTimer.milliseconds() < 550) {
                    Robot.getInstance().driverStation.grabberClosed = true;
                    closeGrabber();
                } else if (grabTimer.milliseconds() > 550) {
                    wantedPickupStates = PickupStates.UP;
                }
                break;

            case UP:
                wantedV4BPosition = V4BPositions.UP;
                break;

            case NULL:
                break;
        }
    }

    @Override
    public void stop() {

    }

    public boolean setLiftPosition(double position) {
        setLiftPower(CruiseLib.limitValue(pid.update(getLiftPosition(), position), -0.1, 0, 0, 1));
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
        double adjustedAngle = (angle / 270) + 0.5;
        Robot.getInstance().leftV4B.setPosition(adjustedAngle);
        Robot.getInstance().rightV4B.setPosition(adjustedAngle);

        return Math.abs(getV4BAngle() - angle) < 5;
    }

    public double getV4BAngle() {
        // Math from REV Robotics documents
        double angle = Robot.getInstance().v4bPot.getVoltage() * 81.4;
        double adjustedAngle = ((445.5 * (angle - 270)) / (Math.pow(angle, 2) - (270 * angle) - 36450)) * 81.1;
        return adjustedAngle - 133;
    }


    public void closeGrabber() {
        Robot.getInstance().grabber.setPosition(0.37);
    }

    public void openGrabber() {
        Robot.getInstance().grabber.setPosition(0.58);
    }
}

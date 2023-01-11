package com.team9889.ftc2021;

import com.qualcomm.robotcore.hardware.Gamepad;

/**
 * Created by MannoMation on 12/14/2018.
 */
public class DriverStation {
    boolean gamepad2Lift = false;

    private Gamepad gamepad1;
    private Gamepad gamepad2;

    public DriverStation(Gamepad gamepad1, Gamepad gamepad2){
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;
    }


    // Drive
    double getX(){
        return gamepad1.left_stick_x;
    }

    double getY() {
        return -gamepad1.left_stick_y;
    }

    double getSteer(){
        return gamepad1.right_stick_x;
    }


    // Lift
    double getLiftPower() {
        return gamepad2.right_trigger - gamepad2.left_trigger/3;
    }

    boolean getLow() {
        return gamepad2.a;
    }

    boolean getMedium() {
        return gamepad2.b;
    }

    boolean getHigh() {
        return gamepad2.y;
    }

    boolean getDown() {
        return gamepad2.x;
    }

    // V4B
    boolean getV4BLeft() {
        return gamepad2.dpad_left;
    }

    boolean getV4BUp() {
        return gamepad2.dpad_up;
    }

    boolean getV4BRight() {
        return gamepad2.dpad_right;
    }

    // Grabber
    private boolean grabberToggle = true;
    public boolean grabberClosed = true;
    boolean getGrabber() {
        if (gamepad2.right_bumper && grabberToggle) {
            grabberClosed = !grabberClosed;
            grabberToggle = false;
        } else if (!gamepad2.right_bumper)
            grabberToggle = true;

        return grabberClosed;
    }

    private boolean groundReleased = true;
    int getGround() {
        if (gamepad1.a && groundReleased) {
            groundReleased = false;
            return 1;
        } else if (!gamepad1.a) {
            groundReleased = true;
        }

        return 0;
    }

    private boolean rightGroundReleased = true;
    int getRightGround() {
        if (gamepad1.y && rightGroundReleased) {
            rightGroundReleased = false;
            return 1;
        } else if (!gamepad1.y) {
            rightGroundReleased = true;
        }

        return 0;
    }

    private boolean scoreReleased = true;
    int getScore() {
        if (gamepad1.right_bumper && scoreReleased) {
            scoreReleased = false;
            return 1;
        } else if (gamepad1.left_bumper && scoreReleased) {
            scoreReleased = false;
            return -1;
        } else if (!gamepad1.left_bumper && !gamepad1.right_bumper) {
            scoreReleased = true;
        }

        return 0;
    }
}

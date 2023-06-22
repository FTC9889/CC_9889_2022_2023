package com.team9889.ftc2021;

import android.util.Log;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.ReadWriteFile;
import com.team9889.ftc2021.auto.AutoSettings;
import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.subsystems.Lift;
import com.team9889.ftc2021.subsystems.Robot;
import com.team9889.lib.CruiseLib;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.File;

/**
 * Created by joshua9889 on 3/28/2018.
 *
 * This class extends LinearOpMode and makes it
 * easier to make code for the robot and not copy
 * and pasting init code.
 */

public abstract class Team9889Linear extends LinearOpMode {
    // Robot Object
    protected Robot Robot = com.team9889.ftc2021.subsystems.Robot.getInstance();

    // Match Timer
    protected ElapsedTime matchTime = new ElapsedTime();

    // Dashboard
    public FtcDashboard dashboard = FtcDashboard.getInstance();

    public int signal = 3;

    boolean left = false;
    public AutoSettings autoSettings = new AutoSettings();
    public boolean custom = false;

    public abstract void initialize();

    public void waitForStart(boolean autonomous, boolean left) {
        this.left = left;
        waitForStart(autonomous);
    }

    public void waitForStart(boolean autonomous) {
        Telemetry dashTelemetry = new MultipleTelemetry(this.telemetry, FtcDashboard.getInstance().getTelemetry());
        Robot.init(hardwareMap, autonomous);
        Robot.telemetry = telemetry;
        Robot.update();

        initialize();

        if (autonomous) {
            Robot.getCamera().update();
        } else {
            for (int i = 0; i < Robot.lines.length; i++) {
                Log.i("File", Robot.lines[i]);
            }

            if (Robot.lines.length >= 2) {
                String[] filePoses = Robot.lines[0].split(",");
                if (filePoses.length >= 3) {
//                    Robot.rr.setPoseEstimate(new Pose2d(parseDouble(filePoses[0]), parseDouble(filePoses[1]), parseDouble(filePoses[2])));
                }

                Robot.isRed = Robot.lines[1].contains("Red");
            }
        }

        telemetry.setMsTransmissionInterval(autonomous ? 200:250);
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.HTML);

//        telemetry = dashboard.getTelemetry();
//        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        if(autonomous){
            // Autonomous Init Loop code
            while(isInInitLoop()){
                initTelemetryUpdate();

                signal = Robot.getCamera().scanForSignal.getSignal();

                Robot.update();
            }
        } else {
            // Teleop Init Loop code
            while(isInInitLoop()){
                telemetry.addData("Waiting for Start","");
                Robot.getMecanumDrive().outputToTelemetry(telemetry);
                telemetry.update();

                FtcDashboard.getInstance().startCameraStream(Robot.camera, 15);
            }
        }

        matchTime.reset();
    }

    /**
     * Used to stop everything (Robot and OpMode).
     */
    protected void finalAction(){
//        Robot.writer.write((int)Robot.rr.getPoseEstimate().getX() + "," +
//                (int)Robot.rr.getPoseEstimate().getY() + "," + (int)Robot.rr.getPoseEstimate().getHeading());
        if (Robot.isRed) {
            Robot.writer.write("Red");
        } else {
            Robot.writer.write("Blue");
        }

        Robot.writer.close();

        Robot.stop();
        requestOpModeStop();
    }

    /**
     * @return Is the robot waiting for start
     */
    private synchronized boolean isInInitLoop(){
        return !isStarted() && !isStopRequested();
    }

    public void runAction(Action action){
        if(opModeIsActive() && !isStopRequested())
            action.start();

        while (!action.isFinished() && opModeIsActive() && !isStopRequested()) {
            action.update();
        }

        if(opModeIsActive() && !isStopRequested()) {
            action.done();
        }
    }

    public void ThreadAction(final Action action){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                runAction(action);
            }
        };

        if(opModeIsActive() && !isStopRequested())
            new Thread(runnable).start();
    }

    public static void writeLastKnownPosition(String values, String fileName) {
        File file = AppUtil.getInstance().getSettingsFile(fileName + ".txt");
        ReadWriteFile.writeFile(file, values);
    }

    public static String readLastKnownPosition(String fileName) {
        File file = AppUtil.getInstance().getSettingsFile(fileName + ".txt");
        String contents = ReadWriteFile.readFile(file);
        return contents;
    }




    //0 = normal, 1 = custom auto
    boolean normalMode = true;
    int cursor = 0;
    boolean buttonPressed = false;
    boolean resetButton = true;
    private void initTelemetryUpdate() {
        resetButton = true;
        if (gamepad1.back && custom) {
            if (!buttonPressed) {
                normalMode = !normalMode;
            }

            resetButton = false;
        }

        if (normalMode) {
            if (gamepad1.right_bumper) {
                Robot.getLift().wantedV4BPosition = Lift.V4BPositions.INIT;
                Robot.getLift().openGrabber();
                matchTime.reset();
            } else {
                if (matchTime.milliseconds() < 1000) {
                    Robot.getLift().wantedV4BPosition = Lift.V4BPositions.LEFT_DOWN;
                } else {
                    Robot.getLift().wantedV4BPosition = Lift.V4BPositions.INIT;
                }

                if (matchTime.milliseconds() > 300) {
                    Robot.getLift().closeGrabber();
                }
            }

            if (gamepad1.b) {
                Robot.isRed = true;
            } else if (gamepad1.x) {
                Robot.isRed = false;
            }

            telemetry.addData("Waiting for Start","");
            if (Robot.getCamera().scanForSignal.getRGB().val[0] == 0 &&
                    Robot.getCamera().scanForSignal.getRGB().val[1] == 0 &&
                    Robot.getCamera().scanForSignal.getRGB().val[2] == 0) {
                telemetry.addData("⚠️<font size=\"+2\" color=\"red\"> DO NOT RUN: CAMERA NOT INITIALIZED </font>   ⚠️", "");
            }

            if (Robot.isRed) {
                telemetry.addData("Alliance", "<font size=\"+2\" color=\"red\"> RED </font>");
            } else {
                telemetry.addData("Alliance", "<font size=\"+2\" color=\"blue\"> BLUE </font>");
            }

            telemetry.addData("Signal", Robot.getCamera().scanForSignal.getSignal());
            telemetry.addData("RGB", Robot.getCamera().scanForSignal.getRGB());
            telemetry.addData("Average", Robot.getCamera().scanForSignal.average);

            telemetry.addData("Custom", custom);

        } else {
            if (gamepad1.dpad_up) {
                if (!buttonPressed) {
                    cursor = (int) CruiseLib.limitValue(cursor - 1, 8, 0);
                }
                resetButton = false;
            }

            if (gamepad1.dpad_down) {
                if (!buttonPressed) {
                    cursor = (int) CruiseLib.limitValue(cursor + 1, 8, 0);
                }

                resetButton = false;
            }


            if (gamepad1.dpad_left) {
                if (!buttonPressed) {
                    switch (cursor) {
                        case 0:
                            autoSettings.wait -= 100;
                            break;

                        case 1:
                            autoSettings.preloaded = (int) CruiseLib.limitValue(autoSettings.preloaded - 1, 3, 0);
                            break;

                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            autoSettings.cycles[cursor - 2] = (int) CruiseLib.limitValue(autoSettings.cycles[cursor - 2] - 1, 3, 0);
                            break;

                        case 7:
                            autoSettings.waitAfterScore -= 50;
                            break;

                        case 8:
                            autoSettings.left = !autoSettings.left;
                            break;
                    }
                }

                resetButton = false;
            }

            if (gamepad1.dpad_right) {
                if (!buttonPressed) {
                    switch (cursor) {
                        case 0:
                            autoSettings.wait += 100;
                            break;

                        case 1:
                            autoSettings.preloaded = (int) CruiseLib.limitValue(autoSettings.preloaded + 1, 3, 0);
                            break;

                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            autoSettings.cycles[cursor - 2] = (int) CruiseLib.limitValue(autoSettings.cycles[cursor - 2] + 1, 3, 0);
                            break;

                        case 7:
                            autoSettings.waitAfterScore += 50;
                            break;

                        case 8:
                            autoSettings.left = !autoSettings.left;
                            break;
                    }
                }

                resetButton = false;
            }

            if (gamepad1.a) {
                if (!buttonPressed) {
                    autoSettings.left = !autoSettings.left;
                }
                resetButton = false;
            }



            telemetry.addData((cursor == 0 ? "-" : "") + "Wait", autoSettings.wait);

            cycleTelemetry((cursor == 1 ? "-" : "") + "Preloaded", autoSettings.preloaded);

            for (int i = 0; i < 5; i++) {
                cycleTelemetry((cursor == (i + 2) ? "-" : "") + "Cycle " + i, autoSettings.cycles[i]);
            }

            telemetry.addData((cursor == 7 ? "-" : "") + "Wait After Score", autoSettings.waitAfterScore);

            telemetry.addData((cursor == 8 ? "-" : "") + "Side", (autoSettings.left ? "Left" : "Right"));
            Robot.getCamera().scanForSignal.left = autoSettings.left;
        }

        if (resetButton) {
            buttonPressed = false;
        } else {
            buttonPressed = true;
        }

        telemetry.update();
    }

    private void cycleTelemetry(String name, int scoreNum) {
        switch (scoreNum) {
            case 0:
                telemetry.addData(name, "Center High");
                break;

            case 1:
                telemetry.addData(name, "Center High Defensive");
                break;

            case 2:
                telemetry.addData(name, "Medium");
                break;

            case 3:
                telemetry.addData(name, "Safe High");
                break;
        }

    }
}

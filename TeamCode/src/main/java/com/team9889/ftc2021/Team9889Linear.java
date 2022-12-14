package com.team9889.ftc2021;

import android.util.Log;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.ReadWriteFile;
import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.subsystems.Robot;

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

    public int timeToWait = 0, timeToWaitDuck = 0;
    boolean buttonReleased = true, editDetection = false;

    public String angleRead = "";

    public abstract void initialize();

    public void waitForStart(boolean autonomous) {
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

        telemetry.setMsTransmissionInterval(autonomous ? 2000:250);
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.HTML);

//        telemetry = dashboard.getTelemetry();
//        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        if(autonomous){
            // Autonomous Init Loop code
            while(isInInitLoop()){
                telemetry.addData("Waiting for Start","");
                if (Robot.getCamera().scanForSignal.getRGB().val[0] == 0 &&
                        Robot.getCamera().scanForSignal.getRGB().val[1] == 0 &&
                        Robot.getCamera().scanForSignal.getRGB().val[2] == 0) {
                    telemetry.addData("??????<font size=\"+2\" color=\"red\"> DO NOT RUN: CAMERA NOT INITIALIZED </font>   ??????", "");
                }
                if (Robot.isRed) {
                    telemetry.addData("", "\uD83D\uDFE5 Red Auto \uD83D\uDFE5");
                } else {
                    telemetry.addData("", "\uD83D\uDD35 Blue Auto \uD83D\uDD35");
                }

                if (gamepad1.right_bumper) {
                    Robot.getLift().openGrabber();
                } else {
                    Robot.getLift().closeGrabber();
                }

                Robot.outputToTelemetry(telemetry);
                telemetry.update();
                Robot.update();

                signal = Robot.getCamera().scanForSignal.getSignal();
                FtcDashboard.getInstance().startCameraStream(Robot.camera, 0);
            }
        } else {
            FtcDashboard.getInstance().startCameraStream(Robot.camera, 0);

            // Teleop Init Loop code
            while(isInInitLoop()){
                telemetry.addData("Waiting for Start","");
                Robot.getMecanumDrive().outputToTelemetry(telemetry);
                telemetry.update();
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
}

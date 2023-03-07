package com.team9889.ftc2021.auto;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.ftc2021.DriverStation;
import com.team9889.ftc2021.Team9889Linear;
import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.subsystems.Robot;

/**
 * Created by joshua9889 on 8/5/2017.
 */

public abstract class AutoModeBase extends Team9889Linear {

    // Autonomous Settings
    private StartPosition currentAutoRunning = StartPosition.RED;


    // Timer for autonomous
    protected ElapsedTime autoTimer = new ElapsedTime();

    public enum StartPosition {
        RED, BLUE;

        private static String redString = "Red";
        private static String blueString = "Blue";

        private static int RED_Num = 1;
        private static int BLUE_Num = -1;
    }

    // Checks for a saved file to see what auto we are running?
    private void setCurrentAutoRunning(){
        currentAutoRunning = side();
    }

    // Method to implement in the auto to run the autonomous
    public abstract void run(StartPosition startPosition);

    public abstract StartPosition side();

    @Override
    public void runOpMode() throws InterruptedException{
        setCurrentAutoRunning();

        Robot.driverStation = new DriverStation(gamepad1, gamepad2);

        waitForStart(true);

        autoTimer.reset();

        // If the opmode is still running, run auto
        if (opModeIsActive() && !isStopRequested()) {
            signal = 3;
            run(currentAutoRunning);
        }

        // Stop all movement
        finalAction();
    }


    /**
     * Run a single action, once, thread-blocking
     * @param action Action Class wanting to run
     */
    public void runAction(Action action){
        if(opModeIsActive() && !isStopRequested()) {
            action.start();

            while (!action.isFinished() && opModeIsActive() && !isStopRequested()) {
                action.update();

//                Robot.outputToTelemetry(telemetry);
//                telemetry.update();
//                while (Robot.loopTime.milliseconds() < 20) {}
                Robot.update();
            }

            if (opModeIsActive() && !isStopRequested()) {
                action.done();
            }
        }
    }

    public void runAction(Action action, boolean update){
        if(opModeIsActive() && !isStopRequested()) {
            action.start();

            while (!action.isFinished() && opModeIsActive() && !isStopRequested()) {
                action.update();

//                Robot.outputToTelemetry(telemetry);
//                telemetry.update();
//                while (Robot.loopTime.milliseconds() < 20) {}
            }

            if (opModeIsActive() && !isStopRequested()) {
                action.done();
            }
        }
    }

    /**
     * Run a single action, once, in a new thread
     * Caution to make sure that you don't run more one action on the same subsystem
     * @param action Action Class wanting to run
     */
    public void ThreadAction(final Action action){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                runAction(action, false);
            }
        };

        if(opModeIsActive() && !isStopRequested())
            new Thread(runnable).start();
    }
}
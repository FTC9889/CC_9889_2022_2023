package com.team9889.ftc2021.auto.actions.drive;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.auto.actions.ActionVariables;
import com.team9889.ftc2021.subsystems.Robot;
import com.team9889.lib.CruiseLib;
import com.team9889.lib.control.controllers.PID;

/**
 * Created by edm11 on 1/31/2023.
 */

@Config
public class DriveToPole extends Action {
    public static double wantedPoint = 128, wantedY = 76;

    public PID yPID = new PID(0.008, 0, 0), thetaPID = new PID(0.003, 0, 0.002);

    double timeout, theta, lastTime = 0;
    ElapsedTime timer = new ElapsedTime();

    double xSpeed = 0, ySpeed = 0, maxSpeed = 1, thetaSpeed = 0;

    public DriveToPole(double timeout) {
        this.timeout = timeout;
        this.theta = 0;
    }

    public DriveToPole(double timeout, double theta) {
        this.timeout = timeout;
        this.theta = theta;
    }

    @Override
    public void start() {
        ActionVariables.doneDriving = false;
    }

    @Override
    public void update() {
        ySpeed = yPID.update(Robot.getInstance().getCamera().scanForPole.getPoint().y, wantedY);
        thetaSpeed = thetaPID.update(Robot.getInstance().getCamera().scanForPole.getPoint().x, wantedPoint);

        ySpeed = CruiseLib.limitValue(ySpeed, maxSpeed);
//        maxSpeed += (timer.milliseconds() - lastTime) * (1.0 / 500.0);
        lastTime = timer.milliseconds();

        Robot.getInstance().getMecanumDrive().setPower(-xSpeed, -ySpeed, -thetaSpeed);

        TelemetryPacket packet = new TelemetryPacket();
        packet.put("Y Error", yPID.getError());
        packet.put("Theta Error", thetaPID.getError());
//        packet.put("Power", ySpeed);

        FtcDashboard.getInstance().sendTelemetryPacket(packet);
    }

    @Override
    public boolean isFinished() {
        return timer.milliseconds() > timeout ||
                (Math.abs(yPID.getError()) < 6 && Math.abs(thetaPID.getError()) < 12);
    }

    @Override
    public void done() {
        Robot.getInstance().getMecanumDrive().setPower(0, 0, 0);
        ActionVariables.doneDriving = true;
    }
}

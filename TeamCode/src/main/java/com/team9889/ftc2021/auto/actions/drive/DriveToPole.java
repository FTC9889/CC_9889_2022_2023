package com.team9889.ftc2021.auto.actions.drive;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.auto.actions.ActionVariables;
import com.team9889.ftc2021.subsystems.Robot;
import com.team9889.lib.CruiseLib;
import com.team9889.lib.Pose;
import com.team9889.lib.control.controllers.PID;

/**
 * Created by edm11 on 1/31/2023.
 */

@Config
public class DriveToPole extends Action {
    public static double wantedPoint = 135, wantedY = 120;

    public PID yPID = new PID(0.008, 0, 0.025), thetaPID = new PID(0.0023, 0, 0.006);
    public PID yPIDVel = new PID(0.0025, 0, 0.4, 0), thetaPIDVel = new PID(0.0008, 0, 0.03);

    double curYVel = 0, curThetaVel = 0;

    double timeout, theta, lastTime = 0, radius = 8;
    Pose polePos;
    ElapsedTime timer = new ElapsedTime();

    double ySpeed = 0, maxSpeed = .5, thetaSpeed = 0;

    public DriveToPole(double timeout) {
        this.timeout = timeout;
        this.theta = 0;
    }

    public DriveToPole(double timeout, Pose polePos) {
        this.timeout = timeout;
        this.theta = 0;
        this.polePos = polePos;
    }

    public DriveToPole(double timeout, Pose polePos, double radius) {
        this.timeout = timeout;
        this.theta = 0;
        this.polePos = polePos;
        this.radius = radius;
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
        ySpeed = -yPID.update(Robot.getInstance().getCamera().scanForPole.getPoint().y, wantedY);
        thetaSpeed = -thetaPID.update(Robot.getInstance().getCamera().scanForPole.getPoint().x, wantedPoint);

        ySpeed = CruiseLib.limitValue(ySpeed, 0, (isInRadius(radius) ? 0 : -0.3), 0, maxSpeed);
        thetaSpeed = CruiseLib.limitValue(thetaSpeed, 0, -maxSpeed, 0, maxSpeed);

        lastTime = timer.milliseconds();

        ySpeed *= 48;
        thetaSpeed *= 220;

        curYVel += yPIDVel.update(Robot.getInstance().getMecanumDrive().yVel, ySpeed);
        curThetaVel += thetaPIDVel.update(Robot.getInstance().getMecanumDrive().thetaVel, thetaSpeed);

        curYVel = CruiseLib.limitValue(curYVel, 1);
        curThetaVel = CruiseLib.limitValue(curThetaVel, 1);

        Robot.getInstance().getMecanumDrive().setPower(0, curYVel, curThetaVel);

        TelemetryPacket packet = new TelemetryPacket();
        packet.put("Y Error", yPID.getError());
        packet.put("Theta Error", thetaPID.getError());
//        packet.put("Power", ySpeed);

        FtcDashboard.getInstance().sendTelemetryPacket(packet);
    }

    public boolean isInRadius(double radius) {
        if (polePos != null) {
            double xError = polePos.x - Robot.getInstance().getMecanumDrive().position.getX();
            double yError = polePos.y - Robot.getInstance().getMecanumDrive().position.getY();

            return Math.hypot(xError, yError) < radius;
        }

        return false;
    }

    @Override
    public boolean isFinished() {
        return timer.milliseconds() > timeout ||
                ((Math.abs(yPID.getError()) < 4 || isInRadius(radius))
                        && Math.abs(thetaPID.getError()) < 12);
    }

    @Override
    public void done() {
        Robot.getInstance().getMecanumDrive().setPower(0, 0, 0);
        ActionVariables.doneDriving = true;
    }
}

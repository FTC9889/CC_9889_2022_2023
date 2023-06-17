package com.team9889.ftc2021.auto.actions.utl;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.subsystems.Robot;

/**
 * Created by edm11 on 6/10/2023.
 */
public class TelemetryTimer extends Action {
    @Override
    public void start() {

    }

    @Override
    public void update() {
        TelemetryPacket p = new TelemetryPacket();
        p.addLine("Timer: " + Robot.getInstance().robotTimer);
        FtcDashboard.getInstance().sendTelemetryPacket(p);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void done() {

    }
}

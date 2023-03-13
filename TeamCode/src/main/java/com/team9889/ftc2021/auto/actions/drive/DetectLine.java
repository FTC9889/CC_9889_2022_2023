package com.team9889.ftc2021.auto.actions.drive;

import android.graphics.Point;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.team9889.ftc2021.auto.actions.Action;
import com.team9889.ftc2021.auto.actions.ActionVariables;
import com.team9889.ftc2021.subsystems.Robot;
import com.team9889.lib.control.math.cartesian.Vector2d;

/**
 * Created by edm11 on 2/25/2023.
 */
public class DetectLine extends Action {
    boolean red, left;

    public DetectLine(boolean red, boolean left) {
        this.red = red;
        this.left = left;
    }

    @Override
    public void start() {
        ActionVariables.doneDriving = false;
    }

    Vector2d leftToRobot = new Vector2d(0.63, 4), centerToRobot = new Vector2d(0, 4), rightToRobot = new Vector2d(-1.26, 4);

    @Override
    public void update() {
        double heading = Robot.getInstance().getMecanumDrive().position.getHeading();

        if (Math.abs(Robot.getInstance().getMecanumDrive().position.getX()) > 50)
        if (red && left) {
            heading *= -1;

//            if (rightDetect() && centerDetect()) {
//                Robot.getInstance().getMecanumDrive().position.setY((12 - (Math.sin(heading) * (leftToRobot.getX() + centerToRobot.getX()) / 2))
//                        + (Math.cos(heading) * (leftToRobot.getY() + centerToRobot.getY()) / 2));
//            } else if (leftDetect() && centerDetect()) {
//                Robot.getInstance().getMecanumDrive().position.setY((12 - (Math.sin(heading) * (rightToRobot.getX() + centerToRobot.getX()) / 2))
//                        + (Math.cos(heading) * (rightToRobot.getY() + centerToRobot.getY()) / 2));
//            } else if (rightDetect()) {
//                Robot.getInstance().getMecanumDrive().position.setY((12 - (Math.sin(heading) * leftToRobot.getX()))
//                        + (Math.cos(heading) * leftToRobot.getY()));
//            } else if (centerDetect()) {
//                Robot.getInstance().getMecanumDrive().position.setY((12 - (Math.sin(heading) * centerToRobot.getX()))
//                        + (Math.cos(heading) * centerToRobot.getY()));
//            } else if (leftDetect()) {
//                Robot.getInstance().getMecanumDrive().position.setY((12 - (Math.sin(heading) * rightToRobot.getX()))
//                        + (Math.cos(heading) * rightToRobot.getY()));
//            }

            if (rightDetect()) {
                Robot.getInstance().getMecanumDrive().position.setY((12 - (Math.sin(heading) * leftToRobot.getX()))
                        + (Math.cos(heading) * leftToRobot.getY()));
            } else if (leftDetect()) {
                Robot.getInstance().getMecanumDrive().position.setY((12 - (Math.sin(heading) * rightToRobot.getX()))
                        + (Math.cos(heading) * rightToRobot.getY()));
            }
        } else if (red && !left) {
            if (leftDetect()) {
                Robot.getInstance().getMecanumDrive().position.setY((12 - (Math.sin(heading) * leftToRobot.getX()))
                        + (Math.cos(heading) * leftToRobot.getY()));
            } else if (rightDetect()) {
                Robot.getInstance().getMecanumDrive().position.setY((12 - (Math.sin(heading) * rightToRobot.getX()))
                        + (Math.cos(heading) * rightToRobot.getY()));
            }
        }
    }

//    boolean leftDetect() {
//        return Robot.getInstance().leftLight.getVoltage() > 0.02;
//    }
//
//    boolean centerDetect() {
//        return Robot.getInstance().centerLight.getVoltage() > 0.09;
//    }
//
//    boolean rightDetect() {
//        return Robot.getInstance().rightLight.getVoltage() > 0.033;
//    }


    boolean leftDetect() {
        return Robot.getInstance().backColor.blue() > 180 || Robot.getInstance().backColor.red() > 180;
    }

    boolean rightDetect() {
        return Robot.getInstance().frontColor.blue() > 180 || Robot.getInstance().frontColor.red() > 180;
    }

    @Override
    public boolean isFinished() {
        return ActionVariables.doneDriving;
    }

    @Override
    public void done() {

    }
}

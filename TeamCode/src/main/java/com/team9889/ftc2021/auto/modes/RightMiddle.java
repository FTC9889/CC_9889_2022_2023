package com.team9889.ftc2021.auto.modes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.ftc2021.auto.AutoModeBase;
import com.team9889.ftc2021.auto.actions.drive.DetectLine;
import com.team9889.ftc2021.auto.actions.drive.DriveToPole;
import com.team9889.ftc2021.auto.actions.drive.PurePursuit;
import com.team9889.ftc2021.auto.actions.drive.Turn;
import com.team9889.ftc2021.auto.actions.lift.Grab;
import com.team9889.ftc2021.auto.actions.lift.Score;
import com.team9889.ftc2021.auto.actions.lift.SetLift;
import com.team9889.ftc2021.auto.actions.utl.ParallelAction;
import com.team9889.ftc2021.auto.actions.utl.Wait;
import com.team9889.ftc2021.subsystems.Lift;
import com.team9889.lib.CruiseLib;
import com.team9889.lib.Pose;
import com.team9889.lib.Pose2d;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by edm11 on 11/5/2022.
 */

@Autonomous(preselectTeleOp = "Teleop")
public class RightMiddle extends com.team9889.ftc2021.auto.Autonomous {
    ElapsedTime timer = new ElapsedTime();

    @Override
    public void initialize() {
        Robot.getCamera().scanForSignal.left = false;
    }

    @Override
    public void run(StartPosition startPosition) {
        Robot.getMecanumDrive().position = new Pose2d(35, 61, 0);
        Robot.getMecanumDrive().angleOffset = 0;

        Robot.camera.setPipeline(Robot.getCamera().scanForPole);

        timer.reset();
        Robot.getLift().wantedScoreState = Lift.ScoreStates.GROUND_RIGHT;
        Robot.getMecanumDrive().setBumpersUp();

        PreloadedMediumJunction();

        for (int i = 0; i < 5 && timer.milliseconds() < (signal == 1 ? 24000 : 24500) && opModeIsActive(); i++) {
            GrabOffStack(i);

            PlaceOnMediumJunction();
            runAction(new Wait(200));
        }


        //Park
        Park();
        runAction(new Wait(1000));
    }

    @Override
    public StartPosition side() {
        return StartPosition.RIGHT;
    }
}

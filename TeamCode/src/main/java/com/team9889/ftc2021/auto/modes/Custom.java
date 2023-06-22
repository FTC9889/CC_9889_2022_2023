package com.team9889.ftc2021.auto.modes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.team9889.ftc2021.auto.actions.utl.Wait;
import com.team9889.ftc2021.subsystems.Lift;
import com.team9889.lib.Pose2d;

/**
 * Created by edm11 on 11/5/2022.
 */

@Autonomous(preselectTeleOp = "Teleop")
public class Custom extends com.team9889.ftc2021.auto.Autonomous {
    ElapsedTime timer = new ElapsedTime();

    @Override
    public void initialize() {
        custom = true;
    }

    @Override
    public void run(StartPosition startPosition) {
        runAction(new Wait(autoSettings.wait));

        Robot.getMecanumDrive().position = new Pose2d(35 * StartPosition.getNum(side()), 61, 0);
        Robot.getMecanumDrive().angleOffset = 0;

        Robot.camera.setPipeline(Robot.getCamera().scanForPole);



        timer.reset();
        Robot.getLift().wantedScoreState = Lift.ScoreStates.GROUND_RIGHT;
        Robot.getMecanumDrive().setBumpersUp();

        switch (autoSettings.preloaded) {
            case 0:
                PreloadedHighJunction();
                break;

            case 1:
                PreloadedHighJunctionDefensive();
                break;

            case 2:
                PreloadedMediumJunction();
                break;

            case 3:
                PreloadedSafeHighJunction();
                break;
        }

        for (int i = 0; i < 5 && timer.milliseconds() < 24500 && opModeIsActive(); i++) {
            GrabOffStack(i);

            switch (autoSettings.cycles[i]) {
                case 0:
                    PlaceOnHighJunction();
                    break;

                case 1:
                    PlaceOnHighJunctionDefensive();
                    break;

                case 2:
                    PlaceOnMediumJunction();
                    break;

                case 3:
                    PlaceOnSafeHighJunction();
                    break;
            }
            runAction(new Wait(autoSettings.waitAfterScore));
        }


        //Park
        Park();
        runAction(new Wait(1000));
    }

    @Override
    public StartPosition side() {
        return (autoSettings.left ? StartPosition.LEFT : StartPosition.RIGHT);
    }
}

package com.team9889.ftc2021.subsystems;

import android.util.Log;

import com.acmerobotics.dashboard.config.Config;
import com.team9889.ftc2021.Constants;
import com.team9889.ftc2021.auto.AutoModeBase;
import com.team9889.lib.detectors.Blank;
import com.team9889.lib.detectors.ScanForPole;
import com.team9889.lib.detectors.ScanForSignal;
import com.team9889.lib.detectors.ScanForTSEObject;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Point;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.PipelineRecordingParameters;

/**
 * Created by MannoMation on 10/27/2018.
 */

@Config
public class Camera extends Subsystem{
//    ScanForTSE scanForTSE = new ScanForTSE();
    ScanForTSEObject scanForTSE = new ScanForTSEObject();
    public ScanForPole scanForPole = new ScanForPole();
    public ScanForSignal scanForSignal = new ScanForSignal();

    public Blank blank = new Blank();

    public enum CameraStates {
        TSE, NULL
    }
    public CameraStates currentCamState = CameraStates.NULL;
    public CameraStates wantedCamState = CameraStates.NULL;

    OpenCvCamera.AsyncCameraOpenListener cvCam = null;

    enum Pipelines {
        TSE, NULL
    }
    public Pipelines currentPipeline = Pipelines.NULL;

    boolean auto;

    @Override
    public void init(final boolean auto) {
        this.auto = auto;
        Robot.getInstance().camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                Robot.getInstance().camera.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);

                if (auto) {
                    Robot.getInstance().camera.setPipeline(scanForSignal);
                } else {
                    Robot.getInstance().camera.setPipeline(scanForPole);
                }
            }

            @Override
            public void onError(int errorCode) {
                /*
                 * This will be called if the camera could not be opened
                 */
            }
        });
    }

    @Override
    public void outputToTelemetry(Telemetry telemetry) {
//        if (auto) {
            telemetry.addData("Pole Camera", scanForPole.getPoint());
//        }
    }

    @Override
    public void update() {
    }

    @Override
    public void stop() {
        Robot.getInstance().camera.stopRecordingPipeline();

        Robot.getInstance().camera.stopStreaming();
    }
}

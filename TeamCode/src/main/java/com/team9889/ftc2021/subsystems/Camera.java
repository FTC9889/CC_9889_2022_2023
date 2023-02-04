package com.team9889.ftc2021.subsystems;

import android.util.Log;

import com.acmerobotics.dashboard.config.Config;
import com.team9889.ftc2021.Constants;
import com.team9889.lib.detectors.Blank;
import com.team9889.lib.detectors.ScanForPole;
import com.team9889.lib.detectors.ScanForSignal;
import com.team9889.lib.detectors.ScanForTSEObject;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Point;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraRotation;

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

        if (auto) {
            cvCam = new OpenCvCamera.AsyncCameraOpenListener() {
                @Override
                public void onOpened() {
                    Robot.getInstance().camera.startStreaming(320, 240, OpenCvCameraRotation.UPRIGHT);
                    Robot.getInstance().camera.setPipeline(scanForSignal);
                }

                @Override
                public void onError(int errorCode) {

                }
            };

            Robot.getInstance().camera.openCameraDeviceAsync(cvCam);

            startFrontCam();
        }
    }

    @Override
    public void outputToTelemetry(Telemetry telemetry) {
        if (auto) {
            telemetry.addData("Pole Camera", scanForPole.getPoint());
        }
    }

    @Override
    public void update() {
    }

    @Override
    public void stop() {
        if (cvCam != null) {
//            Robot.getInstance().frontCVCam.closeCameraDeviceAsync(cvCam);
        }

        Robot.getInstance().frontCVCam.stopStreaming();
        if (Robot.getInstance().hardwareMap.tryGet(WebcamName.class, Constants.kFrontCam) != null) {
            Robot.getInstance().hardwareMap.remove(Constants.kFrontCam, Robot.getInstance().frontCam);
        }
    }

    public void startFrontCam() {
        Robot.getInstance().frontCVCam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened() {
                Robot.getInstance().frontCVCam.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
                Robot.getInstance().frontCVCam.setPipeline(scanForPole);
            }

            @Override
            public void onError(int errorCode) {
                Log.e("CamError", "Code: " + errorCode);
            }
        });
    }

    public void setScanForTSE() {
//        Robot.getInstance().camera.setPipeline(scanForTSE);
        currentPipeline = Pipelines.TSE;
    }

    public void setTSECamPos() {
        wantedCamState = CameraStates.TSE;
    }

    public void setCamPositions(double x, double y) {
//        Robot.getInstance().camXAxis.setPosition(x);
//        Robot.getInstance().camYAxis.setPosition(y);
    }

    public Point getPosOfTarget () {
        Point posToReturn = new Point();
        switch (currentPipeline) {
            case TSE:
//                if (scanForTSE.getPoint().size() > 0)
//                    posToReturn = scanForTSE.getPoint().get(0);
//                else
//                    posToReturn = new Point(1e10, 1e10);
//                break;

                posToReturn = scanForTSE.getPoint();
        }

        return posToReturn;
    }
}

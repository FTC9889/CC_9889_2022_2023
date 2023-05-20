package com.team9889.ftc2021.test;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.RobotLog;
import com.team9889.lib.detectors.ScanForPole;

import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraCharacteristics;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.FocusControl;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;

import java.util.concurrent.TimeUnit;

@TeleOp
public class CameraTesting extends LinearOpMode {
    public WebcamName webcam;
    public OpenCvWebcam camera;

    @Override
    public void runOpMode() throws InterruptedException {
        webcam = hardwareMap.get(WebcamName.class, "Webcam");
        camera = OpenCvCameraFactory.getInstance().createWebcam(webcam);

        RobotLog.v("FORMATS");
        for (CameraCharacteristics.CameraMode mode:
                webcam.getCameraCharacteristics().getAllCameraModes()) {
            RobotLog.v("MAX FPS: " + mode.fps + " | " + mode.toString());
        }

        camera.setPipeline(new ScanForPole());

        OpenCvCamera.AsyncCameraOpenListener listener = new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {

//                camera.getExposureControl().setExposure(100000, TimeUnit.NANOSECONDS);
//                camera.getFocusControl().setMode(FocusControl.Mode.Fixed);
//                camera.getFocusControl().setFocusLength(10);
//
//                camera.getGainControl().setGain(1);


                camera.startStreaming(640, 480, OpenCvCameraRotation.UPRIGHT);

            }

            @Override
            public void onError(int errorCode) {}
        };

        camera.openCameraDeviceAsync(listener);

        waitForStart();

//        FtcDashboard.getInstance().startCameraStream(camera, 15);
//        webcam.resetDeviceConfigurationForOpMode();

        while (opModeIsActive()) {
            RobotLog.v("FPS: " + String.format("%.2f", camera.getFps())
                    + " frame time ms: " + camera.getTotalFrameTimeMs()
                    + " Pipe time: " + camera.getPipelineTimeMs()
                    + " Overtime: " + camera.getOverheadTimeMs()
                    + " Theor FPS: " + camera.getCurrentPipelineMaxFps());

            idle();
        }

        camera.stopStreaming();
    }
}

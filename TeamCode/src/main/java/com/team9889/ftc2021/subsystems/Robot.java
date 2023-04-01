package com.team9889.ftc2021.subsystems;

import android.util.Log;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;
import com.team9889.ftc2021.Constants;
import com.team9889.ftc2021.DriverStation;
import com.team9889.ftc2021.auto.actions.ActionVariables;
import com.team9889.lib.android.FileReader;
import com.team9889.lib.android.FileWriter;
import com.team9889.lib.hardware.CCServo;
import com.team9889.lib.hardware.Motor;
import com.team9889.lib.hardware.RevIMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.team9889.ftc2021.Team9889Linear.writeLastKnownPosition;


/**
 * Created by Eric on 7/26/2019.
 */

public class Robot {

    public WebcamName webcam;
    public OpenCvCamera camera;

    public Motor fLDrive, fRDrive, bLDrive, bRDrive;
    public RevIMU imu = null;
    public Servo leftBumper, rightBumper;

    public AnalogInput leftLight, centerLight, rightLight;

    public Motor leftLift, rightLift;
//    public Servo leftV4B, rightV4B;
    public CCServo leftV4B, rightV4B;
    public Servo grabber;

    public AnalogInput v4bPot, distance, sideDistance;
    public RevColorSensorV3 centerColor;
    public ColorRangeSensor leftColor, rightColor;
    public TouchSensor liftLimit;

    public Motor leds;

    List<LynxModule> hubs;
    LynxModule.BulkData bulkData;


    public HardwareMap hardwareMap;

    public ActionVariables actionVariables = new ActionVariables();

    public boolean isRed = true, auto;

    public ElapsedTime robotTimer = new ElapsedTime(), loopTime = new ElapsedTime();

    public double result = Double.POSITIVE_INFINITY;

    public Telemetry telemetry;

    public FileWriter writer;
    public FileReader reader;
    public String[] lines;

    private static Robot mInstance = null;

    public static Robot getInstance() {
        if (mInstance == null)
            mInstance = new Robot();

        return mInstance;
    }

    private MecanumDrive mMecanumDrive = new MecanumDrive();
    private Lift mLift = new Lift();
    private Camera mCamera = new Camera();

    public boolean rrCancelable = false;

    public DriverStation driverStation;

    // List of subsystems
    private List<Subsystem> subsystems = Arrays.asList(mMecanumDrive, mLift, mCamera);

    public void init(HardwareMap hardwareMap, boolean auto){
        this.hardwareMap = hardwareMap;

        Date currentData = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd.M.yyyy hh:mm:ss");

        RobotLog.a("Robot Init Started at " + format.format(currentData));
        reader = new FileReader("AutoInfo.txt");
        lines = reader.lines();
        reader.close();

        writer = new FileWriter("AutoInfo.txt");

        //Rev Hubs
        hubs = hardwareMap.getAll(LynxModule.class);

        //Camera
        webcam = hardwareMap.get(WebcamName.class, Constants.kWebcam);
        camera = OpenCvCameraFactory.getInstance().createWebcam(webcam);

        //Drive
        fLDrive = new Motor(hardwareMap, Constants.DriveConstants.kLeftDriveMasterId,
                DcMotorSimple.Direction.REVERSE, DcMotor.ZeroPowerBehavior.BRAKE, true, true);
        bLDrive = new Motor(hardwareMap, Constants.DriveConstants.kLeftDriveSlaveId,
                DcMotorSimple.Direction.REVERSE, DcMotor.ZeroPowerBehavior.BRAKE, true, true);
        fRDrive = new Motor(hardwareMap, Constants.DriveConstants.kRightDriveMasterId,
                DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE, true, true);
        bRDrive = new Motor(hardwareMap, Constants.DriveConstants.kRightDriveSlaveId,
                DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE, true, true);

        leftBumper = hardwareMap.get(Servo.class, Constants.DriveConstants.kLeftBumper);
        leftBumper.setDirection(Servo.Direction.REVERSE);
        rightBumper = hardwareMap.get(Servo.class, Constants.DriveConstants.kRightBumper);

        leftLight = hardwareMap.get(AnalogInput.class, Constants.DriveConstants.kLeftLight);
        centerLight = hardwareMap.get(AnalogInput.class, Constants.DriveConstants.kCenterLight);
        rightLight = hardwareMap.get(AnalogInput.class, Constants.DriveConstants.kRightLight);

        //Lift
        leftLift = new Motor(hardwareMap, Constants.LiftConstants.kLeftLift,
                DcMotorSimple.Direction.REVERSE, DcMotor.ZeroPowerBehavior.BRAKE, true, true);
        rightLift = new Motor(hardwareMap, Constants.LiftConstants.kRightLift,
                DcMotorSimple.Direction.FORWARD, DcMotor.ZeroPowerBehavior.BRAKE, true, true);

//        leftV4B = hardwareMap.get(Servo.class, Constants.LiftConstants.kLeftV4B);
//        leftV4B.setDirection(Servo.Direction.REVERSE);
//        leftV4B.getController().pwmEnable();
//        rightV4B = hardwareMap.get(Servo.class, Constants.LiftConstants.kRightV4B);
//        rightV4B.getController().pwmEnable();

        leftV4B = new CCServo(hardwareMap, Constants.LiftConstants.kLeftV4B, 270, 500, Servo.Direction.REVERSE);
        rightV4B = new CCServo(hardwareMap, Constants.LiftConstants.kRightV4B, 270, 500, Servo.Direction.FORWARD);

        grabber = hardwareMap.get(Servo.class, Constants.LiftConstants.kGrabber);

//        v4bPot = hardwareMap.get(AnalogInput.class, Constants.LiftConstants.kV4BPot);
        distance = hardwareMap.get(AnalogInput.class, Constants.LiftConstants.kDistance);
        sideDistance = hardwareMap.get(AnalogInput.class, Constants.LiftConstants.kSideDistance);
        centerColor = hardwareMap.get(RevColorSensorV3.class, Constants.LiftConstants.kCenterColor);
        leftColor = hardwareMap.get(ColorRangeSensor.class, Constants.LiftConstants.kLeftColor);
        rightColor = hardwareMap.get(ColorRangeSensor.class, Constants.LiftConstants.kRightColor);

        liftLimit = hardwareMap.get(TouchSensor.class, Constants.LiftConstants.kLiftLimit);


        leds = new Motor(hardwareMap, "leds");


        imu = new RevIMU("imu", hardwareMap);

        // Init all subsystems
        for (Subsystem subsystem : subsystems) {
            subsystem.init(auto);
        }

        this.auto = auto;

        for (LynxModule module : hubs) {
            module.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }


        robotTimer.reset();
        loopTime.reset();
    }

    // Update data from Hubs and Apply new data
    public void update() {
        // Update Bulk Data
        try {
            // Update Motors
            fRDrive.update();
            fLDrive.update();
            bRDrive.update();
            bLDrive.update();

            leftLift.update();
            rightLift.update();

            // Update Subsystems
            for (Subsystem subsystem : subsystems)
                subsystem.update();
        } catch (Exception e){
            Log.v("Exception@robot.update", "" + e);
        }

        FtcDashboard.getInstance().startCameraStream(Robot.getInstance().camera, 15);

        leftV4B.update(loopTime.milliseconds());
        rightV4B.update(loopTime.milliseconds());

        Log.v("Loop Time", "" + loopTime.milliseconds());

        if (loopTime.milliseconds() > 30) {
            Log.v("LoopWarning", "" + loopTime.milliseconds());
        }

        loopTime.reset();
    }

    // Output Telemetry for all subsystems
    public void outputToTelemetry(Telemetry telemetry) {
        telemetry.addData("Loop Time", loopTime.milliseconds());

        if (isRed)
            telemetry.addData("<font size=\"+2\" color=\"red\">Side</font>", "");
        else
            telemetry.addData("<font size=\"+2\" color=\"aqua\">Side</font>", "");

        for (Subsystem subsystem : subsystems)
            subsystem.outputToTelemetry(telemetry);


//        TelemetryPacket packet = new TelemetryPacket();
//        packet.fieldOverlay()
//                .setFill("green")
//                .fillRect(rr.getPoseEstimate().getX() - 6.5, rr.getPoseEstimate().getY() - 6.5, 13, 13);
//        FtcDashboard.getInstance().sendTelemetryPacket(packet);
    }

    // Stop all subsystems
    public void stop(){
        for (Subsystem subsystem : subsystems)
            subsystem.stop();

        if (isRed) {
            writeLastKnownPosition("Red", "Side");
        } else {
            writeLastKnownPosition("Blue", "Side");
        }
    }

    public MecanumDrive getMecanumDrive(){
        return mMecanumDrive;
    }

    public Lift getLift(){
        return mLift;
    }

    public Camera getCamera(){
        return mCamera;
    }

    public String color = "black";

}

package com.team9889.ftc2021.test;

import com.outoftheboxrobotics.photoncore.Neutrino.Rev2MSensor.Rev2mDistanceSensorEx;
import com.outoftheboxrobotics.photoncore.PhotonCore;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.MovingStatistics;
import com.qualcomm.robotcore.util.RobotLog;
import com.qualcomm.robotcore.util.RollingAverage;
import com.team9889.ftc2021.Constants;
import com.team9889.lib.CruiseLib;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@TeleOp
public class SensorMultiThreadTesting extends LinearOpMode {
    private DistanceSensor sensorRange;

    public RollingAverage average = new RollingAverage(100);

    BNO055IMU imu;

    // State used for updating telemetry
    Orientation angles;
    Acceleration gravity;

    volatile double angle = 0;

    @Override
    public void runOpMode() {
        PhotonCore.enable();

        // you can use this as a regular DistanceSensor.
        sensorRange = hardwareMap.get(DistanceSensor.class, "sensor_range");

        // you can also cast this to a Rev2mDistanceSensor if you want to use added
        // methods associated with the Rev2mDistanceSensor class.
        Rev2mDistanceSensorEx sensorTimeOfFlight = (Rev2mDistanceSensorEx)sensorRange;

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

        telemetry.addData(">>", "Press start to continue");
        telemetry.update();

        waitForStart();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while(opModeIsActive())
                    angle = imu.getAngularOrientation().firstAngle;
            }
        };

        if(opModeIsActive() && !isStopRequested())
            new Thread(runnable).start();

        ElapsedTime timer = new ElapsedTime();
        sensorTimeOfFlight.setRangingProfile(Rev2mDistanceSensorEx.RANGING_PROFILE.HIGH_SPEED);



        while(opModeIsActive()) {
            timer.reset();
            RobotLog.v("range: " +
                    String.format("%.01f in", sensorRange.getDistance(DistanceUnit.INCH)) +
                    " | " +
                    angle +
                    " | " +
                    PhotonCore.CONTROL_HUB.getBulkData().getAnalogInputVoltage(0));


            average.addNumber((int) (1/(timer.seconds() + 0.0000001)));
            RobotLog.v("Hz: " + average.getAverage());
            timer.reset();
        }
    }
}

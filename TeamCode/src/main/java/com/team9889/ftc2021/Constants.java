package com.team9889.ftc2021;

/**
 * Class to store constants
 * Created by joshua9889 on 4/10/2017.
 */

public class Constants {

    //VuMark Licence Key
    public final static String kVuforiaLicenceKey = "AUUHzRL/////AAABmaGWp2jobkGOsFjHcltHEso2M1NH" +
            "Ko/nBDlUfwXKZBwui9l88f5YAT31+8u5yoy3IEJ1uez7rZrIyLdyR4cCMC+a6I7X/EzkR1F094ZegAsXdg9n" +
            "ht01zju+nxxi1+rabsSepS+TZfAa14/0rRidvX6jqjlpncDf8uGiP75f38cW6W4uFRPrLdufA8jMqODGux9d" +
            "w7VkFMqB+DQuNk8n1pvJP87FFo99kr653cjMwO4TYbztNmUYaQUXjHHNhOFxHufN42r7YcCErvX90n/gIvs4" +
            "wrvffXGyU/xkmSaTJzrgiy8R+ZJx2T0JcEJ0m1UUEoF2BmW4ONAVv/TkG9ESGf6iAmx66vrVm3tk6+YY+1q1";

    public final static String kWebcam = "webcam";

    /*---------------------
    |                     |
    |     Drivetrain!     |
    |                     |
    ---------------------*/

    //Settings for Drive class
    public static class DriveConstants {
        public final static String kLeftDriveMasterId = "lf";
        public final static String kRightDriveMasterId = "rf";
        public final static String kLeftDriveSlaveId = "lb";
        public final static String kRightDriveSlaveId = "rb";

        public final static String kLeftBumper = "left_bumper";
        public final static String kRightBumper = "right_bumper";

        public final static String kLeftLight = "left_light";
        public final static String kCenterLight = "center_light";
        public final static String kRightLight = "right_light";

        public final static double WheelbaseWidth = 14.5;
    }

    public static class OdometryConstants {

//        private static double WheelDiameter = 38.45 / 25.4;  // https://www.rotacaster.com.au/shop/35mm-rotacaster-wheels/index
        private static double WheelDiameter = 35 / 25.4;  // https://www.rotacaster.com.au/shop/35mm-rotacaster-wheels/index
        public final static double ENCODER_TO_DISTANCE_RATIO = ((WheelDiameter * Math.PI) * ((((double) 40) / ((double) 24)))) / 1440; // https://www.rotacaster.com.au/shop/35mm-rotacaster-wheels/index  Step 4
//        0.005007837
    }

    /*---------------------
    |                     |
    |       Lift!         |
    |                     |
    ---------------------*/

    //Settings for Lift
    public static class LiftConstants {
        public final static String kLeftLift = "llift";
        public final static String kRightLift = "rlift";

        public final static String kLeftV4B = "left_vb";
        public final static String kRightV4B = "right_vb";

        public final static String kGrabber = "grabber";

        public final static String kV4BPot = "vb_pot";
        public final static String kDistance = "distance";
        public final static String kSideDistance = "side_distance";
        public final static String kCenterColor = "center_color";
        public final static String kFarLeftColor = "far_left_color";
        public final static String kLeftColor = "left_color";
        public final static String kRightColor = "right_color";
        public final static String kFarRightColor = "far_right_color";
        public final static String kLiftLimit = "lift_limit";
    }
}

package com.team9889.lib;

/**
 * Created by edm11 on 9/6/2022.
 */
public class Pose2d {
    double x, y, heading;

    public Pose2d(double x, double y, double heading) {
        this.x = x;
        this.y = y;
        this.heading = heading;
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getHeading() {
        return heading;
    }

    public void setX(double pos) {
        x = pos;
    }

    public void setY(double pos) {
        y = pos;
    }

    public void setHeading(double pos) {
        heading = pos;
    }

    public void addX(double pos) {
        x += pos;
    }

    public void addY(double pos) {
        y += pos;
    }

    public void addHeading(double pos) {
        heading += pos;
    }

    public double[] getArray() {
        double[] array = new double[3];
        array[0] = x;
        array[1] = y;
        array[2] = Math.toDegrees(heading);

        return array;
    }
}

//package com.team9889.lib.control;
//
//
//import android.util.Log;
//
//import com.team9889.lib.CruiseLib;
//import com.team9889.lib.control.math.cartesian.Pose;
//
//import org.opencv.core.Point;
//
//import java.util.ArrayList;
//
//import static java.lang.Math.*;
//
///**
// * Created by Eric on 8/23/2020.
// */
//public class PurePursuit {
////    public ArrayList<Point> lineIntersectWithCircle (Point startOfLine, Point endOfLine, Point circleOffset, double radius) {
////        Log.i("Start Line : ", "" + startOfLine);
////        Log.i("End Line : ", "" + endOfLine);
////        Log.i("Offset Line : ", "" + circleOffset);
////
////        //calculate the M and X of M * X + B = Y
////        double M = (endOfLine.y - startOfLine.y) / (endOfLine.x - startOfLine.x); // -1
////        double B = startOfLine.y - (M * startOfLine.x); // 24
////
////        //calculate the a, b, and c of the quadratic equation
////        double a = pow(M, 2) + 1; // 2
////        double b = (M * B * 2) + (2 * (-circleOffset.x + (M * -circleOffset.y))); // -44
////        double c = (pow(B, 2) - pow(radius, 2)) + (pow(circleOffset.x, 2) + pow(circleOffset.y, 2)); // 954
////
////        //solve the quadratic equations
////        double quad1 = (-b + sqrt(pow(b, 2) - (4 * a * c))) / (a * 2); // 11.31215366417592251028244160312
////        double quad2 = (-b - sqrt(pow(b, 2) - (4 * a * c))) / (a * 2);
////
////        Log.i("Quad1 : ", "" + quad1);
////        Log.i("Quad2 : ", "" + quad2);
////
////        ArrayList<Point> points = new ArrayList<>();
////
////        //make sure the quadratic equations results are less than the radius
////        if (!Double.isNaN(quad1))
////            points.add(new Point(quad1, M * quad1 + B));
////
////        if (!Double.isNaN(quad2))
////            points.add(new Point(quad2, M * quad2 + B));
////
////        return points;
////    }
//
//    public ArrayList<Point> lineIntersectWithCircle (Point startOfLine, Point endOfLine, Point circleOffset, double radius) {
//        Log.i("Start Line : ", "" + startOfLine);
//        Log.i("End Line : ", "" + endOfLine);
//        Log.i("Offset Line : ", "" + circleOffset);
//
//        //calculate the M and X of M * X + B = Y
//        double M = (endOfLine.y - startOfLine.y) / (endOfLine.x - startOfLine.x); // -1
//        double B = startOfLine.y - (M * startOfLine.x); // 24
//
//        //calculate the a, b, and c of the quadratic equation
//        double a1 = 1; // 1
//        double b1 = -circleOffset.x * 2; // -24
//        double c1 = pow(circleOffset.x, 2); // 144
//
//        double a2 = pow(M, 2); // 1
//        double b2 = (B + -circleOffset.y) * M * 2; // -24
//        double c2 = pow(B + -circleOffset.y, 2); // 144
//
//        double a = a1 + a2; // 2
//        double b = b1 + b2; // -48
//        double c = c1 + c2 - pow(radius, 2); // 272
//
//        //solve the quadratic equations
//        double quad1 = (-b + sqrt(pow(b, 2) - (4 * a * c))) / (a * 2); // 14.8284
//        double quad2 = (-b - sqrt(pow(b, 2) - (4 * a * c))) / (a * 2); // 9.17157
//
//        Log.i("Quad1 : ", "" + quad1);
//        Log.i("Quad2 : ", "" + quad2);
//
//        ArrayList<Point> points = new ArrayList<>();
//
//        //make sure the quadratic equations results are less than the radius
//        if (!Double.isNaN(quad1))
//            points.add(new Point(quad1, M * quad1 + B));
//
//        if (!Double.isNaN(quad2))
//            points.add(new Point(quad2, M * quad2 + B));
//
//        return points;
//    }
//
//
//    public Object[] bestPointToFollow (Point startOfLine, Point endOfLine, Point endOfSecLine, Point robotPos, double radius1, double radius2) {
//        //get the line intersections with the circle
//        ArrayList<Point> lineIntersections1 = lineIntersectWithCircle(startOfLine, endOfLine, robotPos, radius1);
//        ArrayList<Point> lineIntersections2 = lineIntersectWithCircle(endOfLine, endOfSecLine, robotPos, radius2);
//
//        Point bestPoint = null;
//        double bestPointDist = 10000;
//        boolean changedLine = false;
//
//        //make sure the line does intersect with the circle. If it does not intersect, return the last point
//        if (lineIntersections1.size() > 0) {
//            for (int i = 0; i < lineIntersections1.size(); i++) {
//
//                //check if the distance from this point to the end point is less than the current best distance
//                if (sqrt(pow(endOfLine.x - lineIntersections1.get(i).x, 2) +
//                        pow(endOfLine.y - lineIntersections1.get(i).y, 2)) < bestPointDist) {
//
//                    //update the best distance, point, and what line is the best
//                    bestPointDist = sqrt(pow(endOfLine.x - lineIntersections1.get(i).x, 2) +
//                            pow(endOfLine.y - lineIntersections1.get(i).y, 2));
//
//                    bestPoint = lineIntersections1.get(i);
//
//                    bestPoint.x = CruiseLib.limitValue(bestPoint.x, -0, endOfLine.x, 0, endOfLine.x);
//                    bestPoint.y = CruiseLib.limitValue(bestPoint.y, -0, endOfLine.y, 0, endOfLine.y);
//                }
//            }
//        }
//
//        //make sure the line does intersect with the circle
//        if (lineIntersections2.size() > 0) {
//            for (int i = 0; i < lineIntersections2.size(); i++) {
//
//                //check if the current best point is not from the second line or,
//                // if the distance from this point to the end point is less than the current best distance
//                if (!changedLine || sqrt(pow(endOfSecLine.x - lineIntersections2.get(i).x, 2) +
//                        pow(endOfSecLine.y - lineIntersections2.get(i).y, 2)) < bestPointDist) {
//
//                    //update the best distance, point, and what line is the best
//                    bestPointDist = sqrt(pow(endOfSecLine.x - lineIntersections2.get(i).x, 2) +
//                            pow(endOfSecLine.y - lineIntersections2.get(i).y, 2));
//
//                    bestPoint = lineIntersections2.get(i);
//                    changedLine = true;
//
//                    bestPoint.x = CruiseLib.limitValue(bestPoint.x, -0, endOfSecLine.x, 0, endOfSecLine.x);
//                    bestPoint.y = CruiseLib.limitValue(bestPoint.y, -0, endOfSecLine.y, 0, endOfSecLine.y);
//                }
//            }
//        }
//
//        if (bestPoint != null) {
//            if (!changedLine) {
//                if (Math.pow(radius1, 2) - (Math.pow(robotPos.x - endOfLine.x, 2) + Math.pow(robotPos.y - endOfSecLine.y, 2)) >= 0) {
//                    bestPoint = endOfLine;
//                }
//            } else {
//                if (Math.pow(radius2, 2) - (Math.pow(robotPos.x - endOfSecLine.x, 2) + Math.pow(robotPos.y - endOfSecLine.y, 2)) >= 0) {
//                    bestPoint = endOfSecLine;
//                }
//            }
//        }
//
//        Log.i("Point : ", "" + bestPoint);
//
//        return new Object[] {bestPoint, changedLine};
//    }
//}

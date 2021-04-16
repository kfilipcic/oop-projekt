package com.example.sensormeasurementapp;

import android.graphics.Path;
import android.graphics.RectF;

import java.util.Random;

public class Triangle extends GeometryObject {
    private double x1;
    private double x2;
    private double x3;
    private double y1;
    private double y2;
    private double y3;

    private Path path;

    public double[] calcXYPoints(double tgAlphaN, double radius, double xc, double yc, double x1, double y1) {
        double a = 1.0 + Math.pow(tgAlphaN,2);
        double b = -2.0*(xc + (yc*tgAlphaN) + (x1*Math.pow(tgAlphaN,2)) - (y1*tgAlphaN));
        double c = ((tgAlphaN*x1) - y1) * ((tgAlphaN*x1) - y1 + (2*yc)) + Math.pow(xc, 2) + Math.pow(yc, 2) - Math.pow(radius, 2);
        double xn = 0.0;

        double determinant = (b*b) - (4*a*c);
        double sqrt = Math.sqrt(determinant);

        if (determinant > 0) {
            double firstRoot = (-b + sqrt) / (2*a);
            double secondRoot = (-b - sqrt) / (2*a);

            if ((int)Math.round(firstRoot) != (int)x1) {
                xn = firstRoot;
            } else if ((int)Math.round(secondRoot) != (int)x1) {
                xn = secondRoot;
            }
        } else if (determinant == 0) {
            xn = (-b + sqrt) / (2*a);
        }

        double yn = tgAlphaN * (xn - x1) + y1;

        return new double[]{xn, yn};
    }

    public Triangle(int x, int y, int minBound, int maxBound, int minRotationDegree, int maxRotationDegree) {
        super(minRotationDegree, maxRotationDegree);
        setObjectTypeString("triangle");

        Random rnd = new Random();

        int xc, yc;

        do {
            xc = rnd.nextInt(x);
            yc = rnd.nextInt(y);
            setRadius(rnd.nextInt(maxBound - minBound + 1) + minBound);

            x1 = rnd.nextInt((int) ((xc+getRadius())-(xc-getRadius()) + 1)) + (xc-getRadius());
            y1 = Math.sqrt(Math.pow(getRadius(),2) - Math.pow((x1-xc),2)) + yc;

            if (xc-x1 == 0) continue;

            double sideLength = getRadius()*Math.sqrt(3);

            x2 = x1 + sideLength;
            y2 = y1;

            x3 = x1 + sideLength/2;
            y3 = y1 - (getRadius()*1.5);


            /*
            double tgAlpha1 = (yc-y1)/(xc-x1);
            double tgAlpha2 = (tgAlpha1-(Math.sqrt(3.0)/3.0)) / (1.0 + tgAlpha1*(Math.sqrt(3.0)/3.0));
            double tgAlpha3 = (tgAlpha1+(Math.sqrt(3.0)/3.0)) / (1.0 - tgAlpha1*(Math.sqrt(3.0)/3.0));
            double[] xy2 = calcXYPoints(tgAlpha2, getRadius(), xc, yc, x1, y1);
            x2 = xy2[0];
            y2 = xy2[1];
            double[] xy3 = calcXYPoints(tgAlpha3, getRadius(), xc, yc, x1, y1);
            x3 = xy3[0];
            y3 = xy3[1]; */

        } while (xc <= 0 || xc >= x || yc <= 0 || yc >= y || x1 <= 0 || x1 >= x || y1 <= 0 || y1 >= y || x2 <= 0 || x2 >= x || y2 <= 0 || y2 >= y || x3 <= 0 || x3 >= x || y3 <= 0 || y3 >= y);

        setCenterX(xc);
        setCenterY(yc);

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo((int)x1, (int)y1);
        path.lineTo((int)x2, (int)y2);
        path.lineTo((int)x3, (int)y3);
        path.lineTo((int)x1, (int)y1);
        //path.close();
    }

    @Override
    public Boolean isInside(float xTouch, float yTouch) {
        int x = (int)xTouch;
        int y = (int)yTouch;
        Path tempPath = new Path(); // Create temp Path
        tempPath.moveTo(x, y); // Move cursor to point
        int rectSize = 1;
        RectF rectangle = new RectF(x-rectSize, y-rectSize, x+rectSize, y+rectSize); // create rectangle with size 2xp
        tempPath.addRect(rectangle, Path.Direction.CW); // add rect to temp path
        tempPath.op(path, Path.Op.DIFFERENCE); // get difference with our PathToCheck

        if (tempPath.isEmpty()) {
            return true;
        }
        return false;
    }

    public double getX1() {
        return x1;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public double getX2() {
        return x2;
    }

    public void setX2(double x2) {
        this.x2 = x2;
    }

    public double getX3() {
        return x3;
    }

    public void setX3(double x3) {
        this.x3 = x3;
    }

    public double getY1() {
        return y1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public double getY2() {
        return y2;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }

    public double getY3() {
        return y3;
    }

    public void setY3(double y3) {
        this.y3 = y3;
    }
    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

}

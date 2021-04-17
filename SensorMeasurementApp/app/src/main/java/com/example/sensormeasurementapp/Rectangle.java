package com.example.sensormeasurementapp;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.SystemClock;

import java.util.Random;

public class Rectangle extends GeometryObject {
    private int left;
    private int right;
    private int top;
    private int bottom;
    private Path path;

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public Path getPath() {
        return path;
    }

    public void setRect(RectF rect) {
        this.path = path;
    }

    public Rectangle(int x, int y, int minBound, int maxBound, int minRotationDegree, int maxRotationDegree, Boolean isSquare) {
        super(minRotationDegree, maxRotationDegree);
        setObjectTypeString("rectangle");

        Random rnd = new Random();
        right = bottom = 0;
        //minRotationDegree = maxRotationDegree = 0;
        //circleRadius = 100;

        left = rnd.nextInt(x);
        top = rnd.nextInt(x);

        //System.out.println("left: " + left);

        while((right-left) < minBound || (right-left) > maxBound || right >= x || (isSquare && top + (right-left) >= y)) {
            left = rnd.nextInt(x);
            top = rnd.nextInt(x);
            right = rnd.nextInt(x - left) + left;
        }
        if (isSquare) {
            bottom = top + (right-left);
        } else {
            while((bottom-top) < minBound || (bottom-top) > maxBound || bottom >= y) {
                bottom = rnd.nextInt(y - top) + top;
            }
        }

        // Create circumscribed circle
        float squareSideLength = bottom - top;
        setRadius(squareSideLength * (float)Math.sqrt(2));
        setCenterX(left + (squareSideLength/2));
        setCenterY(top + (squareSideLength/2));

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(left, top);
        path.lineTo(right, top);
        path.lineTo(right, bottom);
        path.lineTo(left, bottom);
        path.lineTo(left, top);

        Matrix rectangleMatrix  = new Matrix();
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        rectangleMatrix.postRotate(getRotationValue(), bounds.centerX(), bounds.centerY());
        path.transform(rectangleMatrix);
    }

    @Override
    public Boolean isInside(float xTouch, float yTouch) {
        //return (xTouch > left && xTouch < right && yTouch < bottom && yTouch > top);
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
}

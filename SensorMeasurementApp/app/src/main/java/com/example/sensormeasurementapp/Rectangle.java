package com.example.sensormeasurementapp;

import android.graphics.RectF;

import java.util.Random;

public class Rectangle extends GeometryObject {
    private int left;
    private int right;
    private int top;
    private int bottom;

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


    public Rectangle(int x, int y, int minBound, int maxBound, Boolean isSquare) {
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
            System.out.println("left: " + left + " right: " + right + " top: " + top);
            System.out.println("minBound: " + minBound + " maxBound: " + maxBound);
            System.out.println("stuck in rect while!");
        }
        if (isSquare) {
            bottom = top + (right-left);
        } else {
            while((bottom-top) < minBound || (bottom-top) > maxBound || bottom >= y) {
                System.out.println("stuck in rect while!!");
                bottom = rnd.nextInt(y - top) + top;
            }
        }

        // Create circumscribed circle
        float squareSideLength = bottom - top;
        setRadius(squareSideLength * (float)Math.sqrt(2));
        setCenterX(left + (squareSideLength/2));
        setCenterY(top + (squareSideLength/2));
    }

    @Override
    public Boolean isInside(float xTouch, float yTouch) {
        return (xTouch > left && xTouch < right && yTouch < bottom && yTouch > top);
    }
}

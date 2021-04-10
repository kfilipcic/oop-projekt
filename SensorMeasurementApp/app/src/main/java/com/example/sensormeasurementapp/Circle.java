package com.example.sensormeasurementapp;

import java.util.Random;

public class Circle extends GeometryObject {
    public Circle(int x, int y, int minBound, int maxBound) {
        setObjectTypeString("circle");
        Random rnd = new Random();
        do {
            setCenterX(0 + rnd.nextFloat() * (x-0));
            setCenterY(0 + rnd.nextFloat() * (y-0));
            setRadius(rnd.nextInt(maxBound - minBound + 1) + minBound);
            System.out.println("x: " + x + " y: " + y);
            System.out.println("circleRadius: " + getRadius() + " getCenterX(): " + getCenterX() + " rndCircleY: " + getCenterY());
        } while((getCenterX() + getRadius()) >= x || (getCenterX() - getRadius()) <= 0 || (getCenterY() + getRadius()) >= y || (getCenterY() - getRadius()) <= 0);
    }

    @Override
    public Boolean isInside(float xTouch, float yTouch) {
        float distanceX = xTouch - getCenterX();
        float distanceY = yTouch - getCenterY();
        System.out.println("distanceX: " + distanceX + ", " + "distanceY: " + distanceY + ", " + "xTouch: " + xTouch + ", " + "yTouch: " + yTouch);
        System.out.println(getCenterX() + ", " + getCenterY());
        return Math.sqrt(distanceX*distanceX + distanceY*distanceY) <= getRadius();
    }
}

package com.example.sensormeasurementapp;

import java.util.Random;

public class GeometryObject {
    private float centerX;
    private float centerY;
    private float radius;

    private int rotationValue;

    private String objectTypeString;

    GeometryObject(int minRotationDegree, int maxRotationDegree) {
        setRandomRotationValue(minRotationDegree, maxRotationDegree);
    }

    GeometryObject() {

    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Boolean isInside(float xTouch, float yTouch) {
        return false;
    }

    public String getObjectTypeString() {
        return objectTypeString;
    }

    public void setObjectTypeString(String objectTypeString) {
        this.objectTypeString = objectTypeString;
    }

    public int getRotationValue() {
        return rotationValue;
    }

    public void setRotationValue(int rotationValue) {
        this.rotationValue = rotationValue;
    }

    private void setRandomRotationValue(int minRotationDegree, int maxRotationDegree) {
        Random rnd = new Random();
        this.rotationValue = rnd.nextInt(maxRotationDegree - minRotationDegree + 1) + minRotationDegree;
    }
}

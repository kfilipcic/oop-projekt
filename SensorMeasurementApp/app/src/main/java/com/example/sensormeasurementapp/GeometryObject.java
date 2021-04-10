package com.example.sensormeasurementapp;

public class GeometryObject {
    private float centerX;
    private float centerY;
    private float radius;
    private String objectTypeString;

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
}

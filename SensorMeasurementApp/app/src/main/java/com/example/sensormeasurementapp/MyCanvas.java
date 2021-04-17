package com.example.sensormeasurementapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class MyCanvas extends View {
    private int minBound = 10;
    private int maxBound = 500;
    private int rndLeft = 0;
    private int rndRight = 0;
    private int rndTop = 0;
    private int rndBottom = 0;
    private int minRotationDegree, maxRotationDegree = 0;

    private int tapNum;
    RectF rndRect;
    float rndCircleX, rndCircleY;
    float circleRadius;
    Boolean onCreate;
    Paint paint;
    int objectType;
    GeometryObject geometryObject;

    public MyCanvas(Context context) {
        super(context);
        rndRect = new RectF();
        onCreate = true;
        paint = new Paint();
        objectType = -1;
    }

    public MyCanvas(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        rndRect = new RectF();
        onCreate = true;
        paint = new Paint();
        objectType = -1;
    }

    public void randomizePaintColor() {
        Random rnd = new Random();
        int randomColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        paint.setColor(randomColor);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Random rnd = new Random();


        //rect.set(100, 50, 300, 300);


        //System.out.println(String.valueOf(rndRight-rndLeft) + " " + String.valueOf(rndBottom-rndTop));

        if (onCreate) {
            //generateNewRandomRect(true);
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(3);
            canvas.drawPaint(paint);
            //int randomColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            paint.setColor(getResources().getColor(R.color.object_color));

            onCreate = false;
        }

        //canvas.drawColor(Color.BLACK);

        switch (objectType) {
            // Circle
            case 0:
                canvas.drawCircle(geometryObject.getCenterX(), geometryObject.getCenterY(), geometryObject.getRadius(), paint);
                break;
            // Square
            case 1:
                Rectangle rectObject = (Rectangle) geometryObject;
                canvas.drawPath(rectObject.getPath(), paint);
                break;
            // Triangle
            case 2:
                Triangle triangleObject = (Triangle) geometryObject;
                canvas.drawPath(triangleObject.getPath(), paint);
                break;
        }
    }

    public void setObjectType(int objectType) {
        this.objectType = objectType;
    }

    public int getObjectType() {
        return this.objectType;
    }

    public int getMinRotationDegree() {
        return minRotationDegree;
    }

    public void setMinRotationDegree(int minRotationDegree) {
        this.minRotationDegree = minRotationDegree;
    }

    public int getMaxRotationDegree() {
        return maxRotationDegree;
    }

    public void setMaxRotationDegree(int maxRotationDegree) {
        this.maxRotationDegree = maxRotationDegree;
    }

    public int getMinBound() {
        return minBound;
    }

    public void setMinBound(int minBound) {
        this.minBound = minBound;

    }

    public int getMaxBound() {
        return maxBound;
    }

    public void setMaxBound(int maxBound) {
        this.maxBound = maxBound;
    }

    public Boolean getTargetHit() {
        return isTargetHit;
    }

    public void setTargetHit(Boolean targetHit) {
        isTargetHit = targetHit;
    }

    private Boolean isTargetHit;

    public int getRndLeft() {
        return rndLeft;
    }

    public void setRndLeft(int rndLeft) {
        this.rndLeft = rndLeft;
    }

    public int getRndRight() {
        return rndRight;
    }

    public void setRndRight(int rndRight) {
        this.rndRight = rndRight;
    }

    public int getRndTop() {
        return rndTop;
    }

    public void setRndTop(int rndTop) {
        this.rndTop = rndTop;
    }

    public int getRndBottom() {
        return rndBottom;
    }

    public void setRndBottom(int rndBottom) {
        this.rndBottom = rndBottom;
    }

    public int getScrWidth() {
        return getWidth();
    }

    public int getScrHeight() {
        return getHeight();
    }

    public GeometryObject getGeometryObject() {
        return geometryObject;
    }

    public void setGeometryObject(GeometryObject geometryObject) {
        this.geometryObject = geometryObject;
    }

    public int getTapNum() {
        return tapNum;
    }

    public void setTapNum(int tapNum) {
        this.tapNum = tapNum;
    }
}

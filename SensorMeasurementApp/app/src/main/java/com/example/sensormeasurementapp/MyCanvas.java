package com.example.sensormeasurementapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
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
    RectF rndRect;
    float rndCircleX, rndCircleY;
    float circleRadius;
    Boolean onCreate;
    Paint paint;
    int objectType;


    public MyCanvas(Context context) {
        super(context);
        rndRect = new RectF();
        onCreate = true;
        paint = new Paint();
        objectType = 0;
    }

    public MyCanvas(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        rndRect = new RectF();
        onCreate = true;
        paint = new Paint();
        objectType = 0;
    }

    public void generateNewRandomCircle() {
        Random rnd = new Random();
        int x = getWidth();
        int y = getHeight();


        do {
            rndCircleX = 0 + rnd.nextFloat() * (x-0);
            rndCircleY = 0 + rnd.nextFloat() * (y-0);
            circleRadius = rnd.nextInt(maxBound - minBound + 1) + minBound;
            System.out.println("x: " + x + " y: " + y);
            System.out.println("circleRadius: " + circleRadius + " rndCircleX: " + rndCircleX + " rndCircleY: " + rndCircleY);
        } while((rndCircleX + circleRadius) >= x || (rndCircleX - circleRadius) <= 0 || (rndCircleY + circleRadius) >= y || (rndCircleY - circleRadius) <= 0);
    }

    public void generateNewRandomRect(Boolean isSquare) {
        Random rnd = new Random();
        int x = getWidth();
        int y = getHeight();

        rndRight = rndBottom = 0;
        //minRotationDegree = maxRotationDegree = 0;
        //circleRadius = 100;

        rndLeft = rnd.nextInt(x);
        rndTop = rnd.nextInt(x);

        //System.out.println("rndLeft: " + rndLeft);

        while((rndRight-rndLeft) < minBound || (rndRight-rndLeft) > maxBound || rndRight >= x || (isSquare && rndTop + (rndRight-rndLeft) >= y)) {
            rndLeft = rnd.nextInt(x);
            rndTop = rnd.nextInt(x);
            rndRight = rnd.nextInt(x - rndLeft) + rndLeft;
            System.out.println("rndLeft: " + rndLeft + " rndRight: " + rndRight + " rndTop: " + rndTop);
            System.out.println("minBound: " + minBound + " maxBound: " + maxBound);
            System.out.println("stuck in rect while!");
        }
        if (isSquare) {
            rndBottom = rndTop + (rndRight-rndLeft);
        } else {
            while((rndBottom-rndTop) < minBound || (rndBottom-rndTop) > maxBound || rndBottom >= y) {
                System.out.println("stuck in rect while!!");
                rndBottom = rnd.nextInt(y - rndTop) + rndTop;
            }
        }

        this.rndRect = new RectF(rndLeft, rndTop, rndRight, rndBottom);
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
            System.out.println("!!! CANVAS onCreate");
            generateNewRandomRect(true);
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(3);
            canvas.drawPaint(paint);
            //int randomColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            paint.setColor(getResources().getColor(R.color.object_color));

            onCreate = false;
        }

        //canvas.drawColor(Color.BLACK);

        System.out.println("OBJECT TYPE MyCanvas: " + getObjectType());

        switch (objectType) {
            case 0:
                canvas.drawCircle(rndCircleX, rndCircleY, circleRadius, paint);
                break;
            case 1:
                canvas.save();
                canvas.rotate(rnd.nextInt(maxRotationDegree - minRotationDegree + 1) + minRotationDegree, rndRect.centerX(), rndRect.centerY());
                canvas.drawRect(rndRect, paint);
                canvas.restore();
                break;
        }

        // Circle
        //canvas.drawCircle(x/2, y/2, 100, paint);
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
}

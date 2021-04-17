package com.example.sensormeasurementapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Random;

public class DoubleTapActivity extends TapActivity {
    float doubleTapTime;
    float defaultDoubleTapTime = 5.0f;
    Handler doubleTapTimerHandler = new Handler();
    int doubleTapSuccess = 0;
    Long startDoubleTapTime = null;
    float x1;
    float y1;
    float x2;
    float y2;

    Runnable doubleTapTimer = new Runnable() {
        @Override
        public void run() {
            if (startDoubleTapTime == null || doubleTapSuccess != 0)  {
                doubleTapTimerHandler.removeCallbacks(this);
                return;
            } else {
                long millis = SystemClock.elapsedRealtime() - startDoubleTapTime;

                if (millis > (long)(doubleTapTime * 1000)) {
                    doubleTapSuccess = -1;
                    System.out.println("Double tap fail" + doubleTapTime);
                    startDoubleTapTime = null;
                    //generateObjectAndLog(x1, y1, -1, -1);
                    doubleTapTimerHandler.removeCallbacks(this);
                }
                if (doubleTapSuccess != 0) {
                    if (doubleTapSuccess == 1) {
                        doubleTapTimerHandler.removeCallbacks(this);
                    }
                }
                doubleTapTimerHandler.postDelayed(this, 0);
            }
        }
    };

    private void generateObjectAndLog(float x1, float y1, float x2, float y2) {
        touchCnt++;
        Random rnd = new Random();
        ArrayList<Integer> objectTypesRandom = new ArrayList<Integer>();
        for (int i = 0; i < objectTypes.size(); i++) {
            if (objectTypes.get(i)) {
                objectTypesRandom.add(i);
            }
        }
        if (objectTypesRandom.isEmpty()) {
            myCanvas.setObjectType(-1);
        } else {
            myCanvas.setObjectType(objectTypesRandom.get(rnd.nextInt(objectTypesRandom.size())));
        }
        drawNewObject(x1, y1, x2, y2, myCanvas.getObjectType());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interactionTypeString = "double tap";
        startTapTime = null;

        myCanvas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (myCanvas.getTapNum() >= sessionTapNum) {
                    Toast.makeText(DoubleTapActivity.this, getString(R.string.session_end_toast_msg), Toast.LENGTH_SHORT).show();
                    return true;
                }

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (myCanvas.geometryObject != null) {
                            if (startDoubleTapTime != null) {
                                x2 = event.getX();
                                y2 = event.getY();
                                if (doubleTapSuccess == 0) {
                                    doubleTapSuccess = 1;
                                    System.out.println("Double tap success!" + doubleTapTime);
                                    doubleTapTimerHandler.removeCallbacks(doubleTapTimer,0);
                                    startDoubleTapTime = null;
                                }
                            } else {
                                System.out.println("ACTION DOWN startTapTime: ");
                                startTapTime = SystemClock.elapsedRealtime();
                                x1 = event.getX();
                                y1 = event.getY();
                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if (myCanvas.geometryObject != null) {
                            // First action up in double tap
                            if (startDoubleTapTime == null && startTapTime != null) {
                                if (doubleTapSuccess == 0) {
                                    startDoubleTapTime = SystemClock.elapsedRealtime();
                                    doubleTapTimerHandler.postDelayed(doubleTapTimer, 0);
                                } else {
                                    generateObjectAndLog(x1, y1, x2, y2);
                                    tapTime = SystemClock.elapsedRealtime() - startTapTime;
                                    startTapTime = null;
                                    startDoubleTapTime = null;
                                    doubleTapSuccess = 0;
                                }
                            }
                        } else {
                            System.out.println("GEOM OBJ IS NULL");
                            generateObjectAndLog(event.getX(), event.getY(), event.getX(), event.getY());
                        }
                        System.out.println("ACTION UP: startDoubleTapTime.isNull" + startDoubleTapTime);
                        break;
                }
                //myCanvas.invalidate();
                return true;
            }
        });
    }

    public void drawNewObject(float x, float y, float x2, float y2, int objectType) {
        if (myCanvas.geometryObject == null) {
            myCanvas.setGeometryObject(new GeometryObject());
        }
        if (myCanvas.geometryObject.isInside(x, y) && myCanvas.geometryObject.isInside(x2, y2)) {
            myCanvas.setTargetHit(true);
        } else {
            myCanvas.setTargetHit(false);
        }

        myCanvas.getGeometryObject().setTapSuccessful(myCanvas.getTargetHit() && doubleTapSuccess == 1);

        if (myCanvas.geometryObject.getTapSuccessful()) {
            myCanvas.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.correct_bg));
        } else {
            myCanvas.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.incorrect_bg));
        }

        myCanvas.invalidate();

        switch (objectType) {
            case 0:
                myCanvas.setGeometryObject(new Circle(myCanvas.getWidth(), myCanvas.getHeight(), myCanvas.getMinBound(), myCanvas.getMaxBound(), myCanvas.getMinRotationDegree(), myCanvas.getMaxRotationDegree()));
                break;
            case 1:
                Boolean isSquare = true;
                myCanvas.setGeometryObject(new Rectangle(myCanvas.getWidth(), myCanvas.getHeight(), myCanvas.getMinBound(), myCanvas.getMaxBound(), myCanvas.getMinRotationDegree(), myCanvas.getMaxRotationDegree(), isSquare));
                break;
            case 2:
                myCanvas.setGeometryObject(new Triangle(myCanvas.getWidth(), myCanvas.getHeight(), myCanvas.getMinBound(), myCanvas.getMaxBound(), myCanvas.getMinRotationDegree(), myCanvas.getMaxRotationDegree()));
                break;
        }
    }

    @Override
    public void fetchConfigurationDataFromSharedPreferences() {
        super.fetchConfigurationDataFromSharedPreferences();
        doubleTapTime = sharedPreferences.getFloat(getString(R.string.double_tap_time_file_key), defaultDoubleTapTime);
    }

    @Override
    public void commitConfigurationDataToSharedPreferences() {
        super.commitConfigurationDataToSharedPreferences();
        editor.putFloat(getString(R.string.double_tap_time_file_key), doubleTapTime);
    }

    @Override
    public void logToCsvFile(float xTouch, float yTouch) {
        doubleTapTimeString = String.valueOf(doubleTapTime);
        super.logToCsvFile(xTouch, yTouch);
    }
}
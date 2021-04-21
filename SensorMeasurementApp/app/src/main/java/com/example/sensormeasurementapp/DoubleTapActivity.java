package com.example.sensormeasurementapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class DoubleTapActivity extends TapActivity {
    float doubleTapTimeConfig;
    float defaultDoubleTapTime = 0.5f;
    Handler doubleTapTimerHandler = new Handler();
    int doubleTapSuccess = 0;
    Long startDoubleTapTime = null;
    Long startFirstTapTime = null;
    Long startSecondTapTime = null;
    Long doubleTapTime;
    Long firstTapTime = null;
    Long secondTapTime = null;

    float x1 = -1;
    float y1 = -1;
    float x2 = -1;
    float y2 = -1;
    float touchSurface2;
    boolean createNewObjectAfterFail;

    Runnable doubleTapTimer = new Runnable() {
        @Override
        public void run() {
            if (startDoubleTapTime == null || doubleTapSuccess != 0)  {
                doubleTapTimerHandler.removeCallbacks(this);
                return;
            } else {
                long millis = SystemClock.elapsedRealtime() - startDoubleTapTime;

                if (millis > (long)(doubleTapTimeConfig * 1000)) {
                    doubleTapSuccess = -1;
                    //startDoubleTapTime = null;
                    //startTapTime = null;
                    //generateObjectAndLog(x1, y1, -1, -1);
                    //doubleTapTimerHandler.removeCallbacks(this);
                }
                doubleTapTimerHandler.postDelayed(this, 0);
            }
        }
    };

    private void generateObjectAndLog(float x1, float y1, float x2, float y2) {
        touchCnt++;
        boolean wasObjectNull = myCanvas.geometryObject == null;
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
        if (x2 > -1 && y2 > -1) {
            drawNewObject(x1, y1, x2, y2, myCanvas.getObjectType());
        }
        checkAndSetPressAnywhereTextViewVisibility();

        if (!wasObjectNull) {
            myCanvas.setTapNum(myCanvas.getTapNum() + 1);
            logToCsvFile(x1,y1, x2, y2);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interactionTypeString = "double tap";
        startTapTime = null;

        myCanvas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (myCanvas.getTapNum() >= sessionTapNum-1) {
                    Toast.makeText(DoubleTapActivity.this, getString(R.string.session_end_toast_msg), Toast.LENGTH_SHORT).show();
                    return true;
                }

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (myCanvas.geometryObject != null) {
                            // First action down in double tap
                            if (startDoubleTapTime == null) {
                                x1 = event.getX();
                                y1 = event.getY();
                                touchSurface = event.getSize();

                                startTapTime = SystemClock.elapsedRealtime();
                                startFirstTapTime = SystemClock.elapsedRealtime();
                            // Second action down in double tap
                            } else {
                                myCanvas.paint.setColor(getResources().getColor(R.color.object_color));
                                x2 = event.getX();
                                y2 = event.getY();
                                touchSurface2 = event.getSize();

                                doubleTapTimerHandler.removeCallbacks(doubleTapTimer,0);
                                doubleTapTime = SystemClock.elapsedRealtime() - startDoubleTapTime;
                                if (doubleTapTime < (long)(doubleTapTimeConfig*1000)) {
                                    doubleTapSuccess = 1;
                                } else {
                                    doubleTapSuccess = -1;
                                }
                                startDoubleTapTime = null;

                                startSecondTapTime = SystemClock.elapsedRealtime();
                            }
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if (myCanvas.geometryObject != null) {
                            // First action up in double tap
                            if (x2 < 0 && y2 < 0) {
                                startDoubleTapTime = SystemClock.elapsedRealtime();
                                doubleTapTimerHandler.postDelayed(doubleTapTimer, 0);
                                firstTapTime = SystemClock.elapsedRealtime() - startFirstTapTime;

                                myCanvas.paint.setColor(getResources().getColor(R.color.after_first_tap_object_color));
                                myCanvas.invalidate();
                            // Second action up in double tap
                            } else {
                                tapTime = SystemClock.elapsedRealtime() - startTapTime;
                                secondTapTime = SystemClock.elapsedRealtime() - startSecondTapTime;

                                generateObjectAndLog(x1, y1, x2, y2);

                                x1 = y1 = x2 = y2 = -1;
                                startTapTime = null;
                                startDoubleTapTime = null;

                                firstTapTime = null;
                                secondTapTime = null;
                                doubleTapTime = null;

                                doubleTapSuccess = 0;
                            }
                        } else {
                            generateObjectAndLog(event.getX(), event.getY(), event.getX(), event.getY());
                        }
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

        boolean isTapSuccessful = myCanvas.getTargetHit() && doubleTapSuccess == 1;

        if (isTapSuccessful) {
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

        myCanvas.geometryObject.setTapSuccessful(isTapSuccessful);
    }

    @Override
    public void fetchConfigurationDataFromSharedPreferences() {
        super.fetchConfigurationDataFromSharedPreferences();
        doubleTapTimeConfig = sharedPreferences.getFloat(getString(R.string.double_tap_time_file_key), defaultDoubleTapTime);
    }

    @Override
    public void commitConfigurationDataToSharedPreferences() {
        super.commitConfigurationDataToSharedPreferences();
        editor.putFloat(getString(R.string.double_tap_time_file_key), doubleTapTimeConfig);
    }

    public void logToCsvFile(float xTouch, float yTouch, float xTouch2, float yTouch2) {
        doubleTapTimeStringConfig = String.valueOf(doubleTapTimeConfig);
        super.logToCsvFile(xTouch, yTouch, xTouch2, yTouch2, touchSurface, touchSurface2);
    }

    @Override
    protected void checkAndSetPressAnywhereTextViewVisibility() {
        if (myCanvas.geometryObject == null && sessionTapNum > 0) {
            pressAnywhereTV.setVisibility(View.VISIBLE);
        } else {
            pressAnywhereTV.setVisibility(View.GONE);
        }
    }
}
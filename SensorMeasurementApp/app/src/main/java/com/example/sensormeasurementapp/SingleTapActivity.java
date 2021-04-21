package com.example.sensormeasurementapp;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class SingleTapActivity extends TapActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interactionTypeString = "single tap";

        myCanvas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (myCanvas.getTapNum() >= sessionTapNum) {
                    Toast.makeText(SingleTapActivity.this, getString(R.string.session_end_toast_msg), Toast.LENGTH_SHORT).show();
                    return true;
                }

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Touch down code
                        startTapTime = SystemClock.elapsedRealtime();
                        touchSurface = event.getSize();
                        break;

                    case MotionEvent.ACTION_UP:
                        // Touch up code
                        float x = event.getX();
                        float y = event.getY();
                        //tapTime = System.currentTimeMillis() - startTapTime;
                        tapTime = SystemClock.elapsedRealtime() - startTapTime;
                        // touch up code
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

                        myCanvas.setTapNum(myCanvas.getTapNum() + 1);
                        checkAndSetPressAnywhereTextViewVisibility();
                        drawNewObject(x, y, myCanvas.getObjectType());
                        if (myCanvas.getTapNum() > 0) logToCsvFile(x, y, -1, -1, touchSurface, -1);
                        break;
                }
                //myCanvas.invalidate();
                return true;
            }
        });
    }
}
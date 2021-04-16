package com.example.sensormeasurementapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class LongTapActivity extends TapActivity {
    Boolean dwellTimeSuccess = null;
    float dwellTime;
    float defaultDwellTime = 1.5f;
    Handler dwellTimerHandler = new Handler();
    Vibrator vibrator;

    Runnable dwellTimer = new Runnable() {
        @Override
        public void run() {
            long millis = SystemClock.elapsedRealtime() - startTapTime;

            System.out.println("dwellTime: " + dwellTime + " millis: " + millis);
            if (millis >= (long)(dwellTime * 1000)) {
                System.out.println("dwellTimer run()");
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    vibrator.vibrate(500);
                }
                dwellTimerHandler.removeCallbacks(this);
            } else {
                dwellTimerHandler.postDelayed(this, 0);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interactionTypeString = "long tap";
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        myCanvas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (myCanvas.getTapNum() >= sessionTapNum) {
                    Toast.makeText(LongTapActivity.this, getString(R.string.session_end_toast_msg), Toast.LENGTH_SHORT).show();
                    return true;
                }

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // touch down code
                        //startTapTime = System.currentTimeMillis();
                        startTapTime = SystemClock.elapsedRealtime();
                        dwellTimerHandler.postDelayed(dwellTimer, 0);
                        break;

                    case MotionEvent.ACTION_UP:
                        dwellTimerHandler.removeCallbacks(dwellTimer);
                        float x = event.getX();
                        float y = event.getY();
                        tapTime = SystemClock.elapsedRealtime() - startTapTime;

                        if (tapTime >= (long)(dwellTime*1000)) {
                            dwellTimeSuccess = true;
                        } else {
                            dwellTimeSuccess = false;
                        }

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
                        drawNewObject(x, y, myCanvas.getObjectType());
                        logToCsvFile(x, y);
                        break;
                }
                //myCanvas.invalidate();
                return true;
            }
        });
    }

    @Override
    public void fetchConfigurationDataFromSharedPreferences() {
        super.fetchConfigurationDataFromSharedPreferences();
        dwellTime = sharedPreferences.getFloat(getString(R.string.dwell_time_file_key), defaultDwellTime);
    }

    @Override
    public void commitConfigurationDataToSharedPreferences() {
        super.commitConfigurationDataToSharedPreferences();
        editor.putFloat(getString(R.string.dwell_time_file_key), dwellTime);
    }

    private class dwellTimeTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            return null;
        }
    }

    @Override
    public void drawNewObject(float x, float y, int objectType) {
        super.drawNewObject(x, y, objectType);
        myCanvas.getGeometryObject().setTapSuccessful(myCanvas.getTargetHit() && dwellTimeSuccess);
    }

    @Override
    public void logToCsvFile(float xTouch, float yTouch) {
        dwellTimeString = String.valueOf(dwellTime);
        super.logToCsvFile(xTouch, yTouch);
    }
}
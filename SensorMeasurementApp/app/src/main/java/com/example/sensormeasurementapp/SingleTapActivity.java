package com.example.sensormeasurementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
                        // touch down code
                        //startTapTime = System.currentTimeMillis();
                        startTapTime = SystemClock.elapsedRealtime();
                        break;

                    case MotionEvent.ACTION_UP:
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
                        drawNewObject(x, y, myCanvas.getObjectType());
                        logToCsvFile(x, y);
                        break;
                }
                //myCanvas.invalidate();
                return true;
            }
        });
    }
}
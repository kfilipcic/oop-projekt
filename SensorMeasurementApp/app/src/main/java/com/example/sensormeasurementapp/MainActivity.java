package com.example.sensormeasurementapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Long startTapTime, tapTime;
    int touchCnt;
    ArrayList<Boolean> objectTypes;
    MyCanvas myCanvas;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String cbValuesString;
    String sessionName;
    String username;
    int sessionTapNum;
    boolean newSettings;

    public void logToCsvFile() {
        // If this value is null, it is probably
        // the first tap, so don't log
        if (myCanvas.getTargetHit() == null) {
            return;
        }
        try {
            String content = "";
            String currentDateAndTime = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS").format(new Date());
            File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS);
            File file = new File(path + "/" + sessionName +".csv");
            // If file doesn't exist, then create it
            if (!file.exists()) {
                content = "timeStamp,sessionName,objectType,xCenter,yCenter,radius,minSizeConfig,maxSizeConfig,minRotationConfig,maxRotationConfig,isTargetHit,tapTime(ms)\n";
                //content = "timeStamp,sessionName,objectType,rectLeft,rectTop,rectRight,rectBottom,circleRadius,minSize,maxSize,minRotation,maxRotation,targetHit,tapTime(ms)\n";
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(content);
                bw.close();
            }

            String circleRadiusValue = "null";
            String centerXString = "null";
            String centerYString = "null";
            String objectTypeString = "null";

            circleRadiusValue = String.valueOf(myCanvas.getGeometryObject().getRadius());
            centerXString = String.valueOf(myCanvas.getGeometryObject().getCenterX());
            centerYString = String.valueOf(myCanvas.getGeometryObject().getCenterY());
            objectTypeString = myCanvas.getGeometryObject().getObjectTypeString();

            // Create new CSV record row as string with appropriate data
            content = currentDateAndTime + "," + sessionName + "," + objectTypeString + ","
                    + centerXString + "," + centerYString + "," + circleRadiusValue + ","
                    + String.valueOf(myCanvas.getMinBound()) + "," + String.valueOf(myCanvas.getMaxBound()) + ","
                    + String.valueOf(myCanvas.getMinRotationDegree()) + "," + String.valueOf(myCanvas.getMaxRotationDegree()) + ","
                    + String.valueOf(myCanvas.getTargetHit()) + "," + String.valueOf(tapTime) + "\n";

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void drawNewObject(float x, float y, int objectType) {
        if (myCanvas.geometryObject == null) {

            myCanvas.setGeometryObject(new GeometryObject());
        }
        if (myCanvas.geometryObject.isInside(x, y)) {
            myCanvas.setTargetHit(true);
            myCanvas.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.correct_bg));
        } else {
            myCanvas.setTargetHit(false);
            myCanvas.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.incorrect_bg));
        }
        myCanvas.invalidate();
        switch (objectType) {
            case 0:
                myCanvas.setGeometryObject(new Circle(myCanvas.getScrWidth(), myCanvas.getScrHeight(), myCanvas.getMinBound(), myCanvas.getMaxBound()));
                break;
            case 1:
                Boolean isSquare = true;
                myCanvas.setGeometryObject(new Rectangle(myCanvas.getScrWidth(), myCanvas.getScrHeight(), myCanvas.getMinBound(), myCanvas.getMaxBound(), isSquare));
                break;
            case 2:
                myCanvas.setGeometryObject(new Triangle(myCanvas.getScrWidth(), myCanvas.getScrHeight(), myCanvas.getMinBound(), myCanvas.getMaxBound()));
                break;
        }
    }

    public static ArrayList<Boolean> stringToBoolArray(String str){
        ArrayList<Boolean> result = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '1') {
                result.add(true);
            } else if (str.charAt(i) == '0') {
                result.add(false);
            }
        }
        return result;
    }

    public static String boolArrayToString(ArrayList<Boolean> arr) {
        String result = "";
        for (Boolean i : arr) {
            if (i) {
                result += "1";
            } else {
                result += "0";
            }
        }
        return result;
    }

    private void fetchConfigurationDataFromSharedPreferences() {
        cbValuesString = sharedPreferences.getString(getString(R.string.shapes_cb_file_key), "01");
        myCanvas.setMinBound(sharedPreferences.getInt(getString(R.string.size_min_bound_file_key), 10));
        myCanvas.setMaxBound(sharedPreferences.getInt(getString(R.string.size_max_bound_file_key), 500));
        myCanvas.setMinRotationDegree(sharedPreferences.getInt(getString(R.string.rotation_min_bound_file_key), 0));
        myCanvas.setMaxRotationDegree(sharedPreferences.getInt(getString(R.string.rotation_max_bound_file_key), 0));
        sessionName = sharedPreferences.getString(getString(R.string.session_name_file_key), "default");
        username = sharedPreferences.getString(getString(R.string.username_file_key), "default");
        sessionTapNum = sharedPreferences.getInt(getString(R.string.session_tapnum_file_key), -1);
        newSettings = sharedPreferences.getBoolean(getString(R.string.new_settings_boolean_file_key), false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        myCanvas = (MyCanvas) findViewById(R.id.my_canvas);


        fetchConfigurationDataFromSharedPreferences();
        myCanvas.setTapNum(0);

        objectTypes = stringToBoolArray(cbValuesString);

        touchCnt = 0;

        myCanvas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (myCanvas.getTapNum() >= sessionTapNum) {
                    Toast.makeText(MainActivity.this, getString(R.string.session_end_toast_msg), Toast.LENGTH_SHORT).show();
                    return true;
                }

                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // touch down code
                        //startTapTime = System.currentTimeMillis();
                        startTapTime = SystemClock.elapsedRealtime();
                        break;

                    case MotionEvent.ACTION_UP:
                        float x = event.getRawX();
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
                        logToCsvFile();
                        break;
                }
                //myCanvas.invalidate();
                return true;
            }
        });
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.configActivityItem:
                Intent intent = new Intent(this, ConfigurationActivity.class);
                editor.putString(getString(R.string.shapes_cb_file_key), boolArrayToString(objectTypes));
                editor.putString(getString(R.string.session_name_file_key), sessionName);
                editor.putBoolean(getString(R.string.new_settings_boolean_file_key), false);
                editor.apply();
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        fetchConfigurationDataFromSharedPreferences();
        System.out.println("MainActivity onResume");
        if (newSettings) {
            myCanvas.setTapNum(0);
            System.out.println("NEW SETTINGS MAINACT");
            newSettings = false;
        }
        objectTypes = stringToBoolArray(cbValuesString);
    }
}
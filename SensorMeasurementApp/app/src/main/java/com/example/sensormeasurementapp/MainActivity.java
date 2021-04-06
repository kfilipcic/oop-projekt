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
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

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
                content = "timeStamp,sessionName,objectType,rectLeft,rectTop,rectRight,rectBottom,circleRadius,minSize,maxSize,minRotation,maxRotation,targetHit,tapTime\n";
                file.createNewFile();
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(content);
                bw.close();
            }
            // If object was a circle, clear previous rect values
            // (assign -1 value)
            if (myCanvas.objectType == 0) {
                myCanvas.setRndLeft(-1);
                myCanvas.setRndTop(-1);
                myCanvas.setRndRight(-1);
                myCanvas.setRndBottom(-1);
            }
            content = currentDateAndTime + "," + sessionName + "," + String.valueOf(myCanvas.objectType) + "," + String.valueOf(myCanvas.getRndLeft()) + ","
                    + String.valueOf(myCanvas.getRndTop()) + "," + String.valueOf(myCanvas.getRndRight()) + "," + String.valueOf(myCanvas.getRndBottom()) + ","
                    + String.valueOf(myCanvas.circleRadius) + "," + String.valueOf(myCanvas.getMinBound()) + "," + String.valueOf(myCanvas.getMaxBound()) + ","
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

    private void drawNewRandomRect(float x, float y) {
        RectF canvasRect = myCanvas.rndRect;
        // User touched the object
        if (x > canvasRect.left && x < canvasRect.right && y < canvasRect.bottom && y > canvasRect.top) {
            myCanvas.setTargetHit(true);
            //if (myCanvas.rndRect.contains(event.getRawX(), event.getRawY())) {
            myCanvas.invalidate();
            //myCanvas.randomizePaintColor();
            myCanvas.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.correct_bg));
            // User failed to touch the object
        } else {
            myCanvas.setTargetHit(false);
            myCanvas.invalidate();
            //myCanvas.randomizePaintColor();
            myCanvas.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.incorrect_bg));
        }
        myCanvas.generateNewRandomRect(true);
    }

    private Boolean isInsideCircle(float xTouch, float yTouch) {
        float distanceX = xTouch - myCanvas.rndCircleX; // ABSOLUTE VALUE!!!!1
        float distanceY = yTouch - myCanvas.rndCircleY;
        System.out.println("distanceX: " + distanceX + ", " + "distanceY: " + distanceY + ", " + "xTouch: " + xTouch + ", " + "yTouch: " + yTouch);
        System.out.println(myCanvas.rndCircleX + ", " + myCanvas.rndCircleY);
        return Math.sqrt(distanceX*distanceX + distanceY*distanceY) <= myCanvas.circleRadius;
    }

    private void drawNewRandomCircle(float x, float y) {
        if (isInsideCircle(x, y)) {
            myCanvas.setTargetHit(true);
            myCanvas.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.correct_bg));
        } else {
            myCanvas.setTargetHit(false);
            myCanvas.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.incorrect_bg));
        }
        myCanvas.invalidate();
        myCanvas.generateNewRandomCircle();
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

        objectTypes = stringToBoolArray(cbValuesString);

        //TextView sampleText = (TextView) findViewById(R.id.sample_text);
        //myCanvas = new MyCanvas(this);
        //setContentView(myCanvas);
        touchCnt = 0;
        //objectTypes.add(0);
        //objectTypes.add(1);

        myCanvas.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Create rectangle from the point of touch

                //RectF touchpoint = new RectF((int)event.getX(), (int)event.getY(), 10, 10);
                //System.out.println(event.getRawX() + ", " + event.getRawY());
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // touch down code
                        startTapTime = System.currentTimeMillis();
                        break;

                    case MotionEvent.ACTION_UP:
                        float x = event.getRawX();
                        float y = event.getY();
                        tapTime = System.currentTimeMillis() - startTapTime;
                        // touch up code
                        touchCnt++;
                        System.out.println("on touch: " + String.valueOf(touchCnt));

                        Random rnd = new Random();
                        ArrayList<Integer> objectTypesRandom = new ArrayList<Integer>();
                        System.out.println("obj types size " + objectTypes.size());
                        for (int i = 0; i < objectTypes.size(); i++) {
                            if (objectTypes.get(i)) {
                                objectTypesRandom.add(i);
                            }
                        }
                        System.out.println("OBJECT types random: " + objectTypesRandom);
                        if (objectTypesRandom.isEmpty()) {
                            myCanvas.setObjectType(-1);
                        } else {
                            myCanvas.setObjectType(objectTypesRandom.get(rnd.nextInt(objectTypesRandom.size())));
                        }

                        switch(myCanvas.getObjectType()) {
                            // Rect
                            case 0:
                                drawNewRandomCircle(x, y);
                                break;
                            // Circle
                            case 1:
                                drawNewRandomRect(x, y);
                                break;
                        }
                        break;
                }
                //myCanvas.invalidate();
                logToCsvFile();
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
        objectTypes = stringToBoolArray(cbValuesString);
    }
}
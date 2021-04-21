package com.example.sensormeasurementapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TapActivity extends AppCompatActivity {
    Long startTapTime, tapTime;
    int touchCnt;
    ArrayList<Boolean> objectTypes;
    MyCanvas myCanvas;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String cbValuesString;
    String sessionName;
    String username;
    String interactionTypeString = "null";
    int sessionTapNum;
    boolean newSettings;
    String dwellTimeString = "null";
    String doubleTapTimeStringConfig = "null";

    String touchSurface2String = "null";
    String firstTapTimeString = "null";
    String secondTapTimeString = "null";
    String doubleTapTimeString = "null";

    TextView pressAnywhereTV;
    boolean newSessionIntentBoolean;
    int configIntentResultCode = 100;
    float touchSurface;

    public void logToCsvFile(float xTouch, float yTouch, float xTouch2, float yTouch2, float touchSurface, float touchSurface2) {
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
            // Create one log for everything
            File file = new File(path + "/" + getString(R.string.log_name) +".csv");
            // Creating log for every different session
            //File file = new File(path + "/" + sessionName +".csv");

            // If file doesn't exist, then create it
            if (!file.exists()) {
                // Create header for new log file
                content = getString(R.string.log_file_header) + "\n";
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
            String objectRotationValueString = "null";
            String tapSuccessfulString = "null";
            String xTouch2String = "null";
            String yTouch2String = "null";

            circleRadiusValue = String.valueOf(myCanvas.getGeometryObject().getRadius());
            centerXString = String.valueOf(myCanvas.getGeometryObject().getCenterX());
            centerYString = String.valueOf(myCanvas.getGeometryObject().getCenterY());
            objectTypeString = myCanvas.getGeometryObject().getObjectTypeString();
            objectRotationValueString = String.valueOf(myCanvas.getGeometryObject().getRotationValue());

            // Only long and double tapping mode have this info
            // (for single tap if target is hit is enough to know it's been a successful tap)
            if (myCanvas.getGeometryObject().getTapSuccessful() != null) {
                tapSuccessfulString = String.valueOf(myCanvas.getGeometryObject().getTapSuccessful());
            }
            // If logging a double tap, these values should be positive
            // and therefore should be logged
            if (this instanceof DoubleTapActivity) {
                touchSurface2String = String.valueOf(((DoubleTapActivity) this).touchSurface2);
                firstTapTimeString = String.valueOf(((DoubleTapActivity) this).firstTapTime);
                secondTapTimeString = String.valueOf(((DoubleTapActivity) this).secondTapTime);
                doubleTapTimeString = String.valueOf(((DoubleTapActivity) this).doubleTapTime);
            }
            if (xTouch2 > -1 && yTouch2 > -1) {
                xTouch2String = String.valueOf(xTouch2);
                yTouch2String = String.valueOf(yTouch2);
                touchSurface2String = Float.toString(touchSurface2);
            }

            String delimiter = ",";

            // Create new CSV record row as string with appropriate data
            content = currentDateAndTime + delimiter + username + delimiter + sessionName + delimiter + objectTypeString + delimiter
                    + centerXString + delimiter + centerYString + delimiter + circleRadiusValue + delimiter
                    + xTouch + delimiter + yTouch + delimiter + xTouch2String + delimiter + yTouch2String + delimiter
                    + Float.toString(touchSurface) + delimiter + touchSurface2String + delimiter
                    + objectRotationValueString + delimiter + interactionTypeString + delimiter
                    + String.valueOf(sessionTapNum) + delimiter
                    + String.valueOf(myCanvas.getMinBound()) + delimiter + String.valueOf(myCanvas.getMaxBound()) + delimiter
                    + String.valueOf(myCanvas.getMinRotationDegree()) + delimiter + String.valueOf(myCanvas.getMaxRotationDegree()) + delimiter
                    + dwellTimeString + delimiter + doubleTapTimeStringConfig + delimiter
                    + String.valueOf(myCanvas.getTargetHit()) + delimiter + tapSuccessfulString + delimiter
                    + String.valueOf(tapTime) + delimiter + firstTapTimeString + delimiter + doubleTapTimeString + delimiter + secondTapTimeString + "\n";

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawNewObject(float x, float y, int objectType) {
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

    public void fetchConfigurationDataFromSharedPreferences() {
        cbValuesString = sharedPreferences.getString(getString(R.string.shapes_cb_file_key), "111");
        myCanvas.setMinBound(sharedPreferences.getInt(getString(R.string.size_min_bound_file_key), 10));
        myCanvas.setMaxBound(sharedPreferences.getInt(getString(R.string.size_max_bound_file_key), 500));
        myCanvas.setMinRotationDegree(sharedPreferences.getInt(getString(R.string.rotation_min_bound_file_key), 0));
        myCanvas.setMaxRotationDegree(sharedPreferences.getInt(getString(R.string.rotation_max_bound_file_key), 0));
        sessionName = sharedPreferences.getString(getString(R.string.session_name_file_key), "default");
        username = sharedPreferences.getString(getString(R.string.username_file_key), "default");
        sessionTapNum = sharedPreferences.getInt(getString(R.string.session_tapnum_file_key), 0);
        if (newSessionIntentBoolean) {
            newSettings = sharedPreferences.getBoolean(getString(R.string.new_settings_boolean_file_key), false);
        } else {
            newSettings = false;
        }
    }

    protected void checkAndSetPressAnywhereTextViewVisibility() {
        if (myCanvas.getTapNum() < 0 && sessionTapNum > 0) {
            pressAnywhereTV.setVisibility(View.VISIBLE);
        } else {
            pressAnywhereTV.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newSessionIntentBoolean = getIntent().hasExtra(getString(R.string.start_new_session_intent_boolean_file_key));
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        setContentView(R.layout.activity_tap);

        pressAnywhereTV = findViewById(R.id.pressAnywhereTV);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        myCanvas = (MyCanvas) findViewById(R.id.my_canvas);
        myCanvas.setTapNum(-1);

        fetchConfigurationDataFromSharedPreferences();
        checkAndSetPressAnywhereTextViewVisibility();

        objectTypes = stringToBoolArray(cbValuesString);

        touchCnt = 0;

    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    public void commitConfigurationDataToSharedPreferences() {
        editor.putString(getString(R.string.shapes_cb_file_key), boolArrayToString(objectTypes));
        editor.putString(getString(R.string.session_name_file_key), sessionName);
        editor.putBoolean(getString(R.string.new_settings_boolean_file_key), false);
        editor.putInt(getString(R.string.canvas_width_file_key), myCanvas.getWidth());
        editor.putInt(getString(R.string.canvas_height_file_key), myCanvas.getHeight());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.configActivityItem:
                Intent intent = new Intent(this, ConfigurationActivity.class);
                intent.putExtra(getString(R.string.interaction_type_file_key), interactionTypeString);
                commitConfigurationDataToSharedPreferences();
                editor.apply();
                startActivityForResult(intent, configIntentResultCode);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        fetchConfigurationDataFromSharedPreferences();
        checkAndSetPressAnywhereTextViewVisibility();
        if (newSettings && newSessionIntentBoolean) {
            myCanvas.setTapNum(-1);
            newSettings = false;
        }
        objectTypes = stringToBoolArray(cbValuesString);
    }

    @Override
    protected void onPause() {
        super.onPause();
        newSessionIntentBoolean = false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == configIntentResultCode) {
            if (resultCode == configIntentResultCode) {
                newSessionIntentBoolean = data.getBooleanExtra(getString(R.string.start_new_session_intent_boolean_file_key), false);
            }
        }
    }
}
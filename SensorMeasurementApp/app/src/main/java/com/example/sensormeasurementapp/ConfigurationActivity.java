package com.example.sensormeasurementapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class ConfigurationActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<Boolean> objectTypes;
    int minBound, maxBound, minRotationDegree, maxRotationDegree;
    String sessionName;
    String username;
    int sessionTapNum;
    boolean newSettings;
    String interactionTypeString;
    float dwellTime;
    float doubleTapTime;
    int canvasWidth;
    int canvasHeight;
    int canvasShorterSide;
    boolean createNewObjectAfterFail;
    CheckBox doubleTapCB;

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        setContentView(R.layout.activity_configuration);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if (b != null) {
            interactionTypeString = (String) b.get(getString(R.string.interaction_type_file_key));
        }

        // Fetch relevant data from Shared Preferences
        String objectTypesString = sharedPreferences.getString(getString(R.string.shapes_cb_file_key), "111");
        minBound = sharedPreferences.getInt(getString(R.string.size_min_bound_file_key), 100);
        maxBound = sharedPreferences.getInt(getString(R.string.size_max_bound_file_key), 400);
        minRotationDegree = sharedPreferences.getInt(getString(R.string.rotation_min_bound_file_key), 0);
        maxRotationDegree = sharedPreferences.getInt(getString(R.string.rotation_max_bound_file_key), 180);
        sessionName = sharedPreferences.getString(getString(R.string.session_name_file_key), "default");
        username = sharedPreferences.getString(getString(R.string.username_file_key), "default");
        sessionTapNum = sharedPreferences.getInt(getString(R.string.session_tapnum_file_key), 0);
        newSettings = sharedPreferences.getBoolean(getString(R.string.new_settings_boolean_file_key), false);
        dwellTime = sharedPreferences.getFloat(getString(R.string.dwell_time_file_key), 0);
        doubleTapTime = sharedPreferences.getFloat(getString(R.string.double_tap_time_file_key), 0);
        createNewObjectAfterFail = sharedPreferences.getBoolean(getString(R.string.double_tap_cb_file_key), true);

        // Get Canvas width/height information to handle
        // min/max bounds
        canvasWidth = sharedPreferences.getInt(getString(R.string.canvas_width_file_key), -1);
        canvasHeight = sharedPreferences.getInt(getString(R.string.canvas_height_file_key), -1);
        if (canvasWidth < canvasHeight) {
            canvasShorterSide = canvasWidth;
        } else {
            canvasShorterSide = canvasHeight;
        }

        EditText maxSizeET = findViewById(R.id.maxSizeInputText);
        EditText minSizeET = findViewById(R.id.minSizeInputText);
        EditText maxRotationET = findViewById(R.id.maxRotationInputText);
        EditText minRotationET = findViewById(R.id.minRotationInputText);

        EditText sessionTapNumET = findViewById(R.id.numTapsSessionET);

        EditText sessionNameET = findViewById(R.id.sessionNameInputText);
        EditText usernameET = findViewById(R.id.usernameET);

        TextView dwellTimeTV = findViewById(R.id.dwellTimeTV);
        EditText dwellTimeET = findViewById(R.id.dwellTimeET);

        TextView doubleTapTimeTV = findViewById(R.id.doubleTapTimeTV);
        EditText doubleTapTimeET = findViewById(R.id.doubleTapTimeET);
        doubleTapCB = findViewById(R.id.doubleTapCB);

        Button backBtn = findViewById(R.id.backConfigurationButton);
        Button startNewSessionBtn = findViewById(R.id.startSessionConfigurationButton);

        if (interactionTypeString.equals("long tap")) {
            dwellTimeTV.setEnabled(true);
            dwellTimeET.setEnabled(true);
            dwellTimeET.setText(String.valueOf(dwellTime));

            dwellTimeET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().isEmpty()) {
                        try {
                            dwellTime = Float.parseFloat(s.toString());
                            dwellTimeET.setError(null);
                        } catch (Exception e) {
                            System.err.println("Error while parsing float from EditText! (dwellTimeET)");
                            dwellTimeET.setError(getString(R.string.invalid_value_error_msg));
                        }
                    }
                }
            });
        } else {
            dwellTimeTV.setEnabled(false);
            dwellTimeET.setEnabled(false);
        }

        if (interactionTypeString.equals("double tap")) {
            doubleTapTimeTV.setEnabled(true);
            doubleTapTimeET.setEnabled(true);
            doubleTapCB.setEnabled(true);

            doubleTapCB.setChecked(createNewObjectAfterFail);

            doubleTapTimeET.setText(String.valueOf(doubleTapTime));

            doubleTapTimeET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!s.toString().isEmpty()) {
                        try {
                            doubleTapTime = Float.parseFloat(s.toString());
                            dwellTimeET.setError(null);
                        } catch (Exception e) {
                            System.err.println("Error while parsing float from EditText! (doubleTapTimeET)");
                            dwellTimeET.setError(getString(R.string.invalid_value_error_msg));
                        }
                    }
                }
            });
        } else {
            doubleTapTimeTV.setEnabled(false);
            doubleTapTimeET.setEnabled(false);
            doubleTapCB.setEnabled(false);
        }

        // Create array containing EditTexts for min/max value
        // parameters
        ArrayList<EditText> configParamETs = new ArrayList<>();
        configParamETs.add(maxSizeET);
        configParamETs.add(minSizeET);
        configParamETs.add(maxRotationET);
        configParamETs.add(minRotationET);
        configParamETs.add(sessionTapNumET);

        // Create array which contains ALL EditTexts from this activity
        ArrayList<EditText> configETs = new ArrayList<>();
        configETs.add(maxSizeET);
        configETs.add(minSizeET);
        configETs.add(maxRotationET);
        configETs.add(minRotationET);
        configETs.add(sessionTapNumET);
        configETs.add(sessionNameET);
        configETs.add(usernameET);
        configETs.add(sessionTapNumET);
        configETs.add(dwellTimeET);
        configETs.add(doubleTapTimeET);

        int[] configETsIDs = new int[]{R.id.maxSizeInputText, R.id.minSizeInputText, R.id.maxRotationInputText, R.id.minRotationInputText, R.id.numTapsSessionET};

        maxSizeET.setText(String.valueOf(maxBound));
        minSizeET.setText(String.valueOf(minBound));
        maxRotationET.setText(String.valueOf(maxRotationDegree));
        minRotationET.setText(String.valueOf(minRotationDegree));

        sessionTapNumET.setText(String.valueOf(sessionTapNum));

        sessionNameET.setText(sessionName);
        usernameET.setText(username);

        for (EditText ET : configETs) {
            ET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        hideKeyboard(v);
                    }
                }
            });
        }

        for (int i = 0; i < configParamETs.size(); i++) {
            EditText ET = configParamETs.get(i);
            int finalI = i;
            ET.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    int valueET = 0;
                    if (!s.toString().isEmpty()) {
                        try {
                            valueET = Integer.parseInt(s.toString());
                            ET.setError(null);
                        } catch (Exception e) {
                            ET.setError(getString(R.string.invalid_value_error_msg));
                        }
                    }

                    switch (finalI) {
                        // Max size ET
                        case 0:
                            if (valueET < minBound) {
                                ET.setError(getString(R.string.max_size_error_msg));
                            }
                            if (valueET >= minBound) {
                                configParamETs.get(1).setError(null);
                            }
                            // If it can't theoretically fit on screen,
                            // size value is invalid
                            if (2*valueET >= canvasShorterSide) {
                                ET.setError(getString(R.string.too_big_value_error_msg));
                            }
                            maxBound = valueET;
                            break;
                        // Min size ET
                        case 1:
                            if (valueET > maxBound) {
                                ET.setError(getString(R.string.min_size_error_msg));
                            }
                            if (valueET <= maxBound) {
                                configParamETs.get(0).setError(null);
                            }
                            // If it can't theoretically fit on screen,
                            // size value is invalid
                            if (2*valueET >= canvasShorterSide) {
                                ET.setError(getString(R.string.too_big_value_error_msg));
                            }
                            minBound = valueET;
                            break;
                        // Max rotation ET
                        case 2:
                            if (valueET < minRotationDegree) {
                                ET.setError(getString(R.string.max_rot_error_msg));
                            }
                            if (valueET >= minRotationDegree) {
                                configParamETs.get(3).setError(null);
                            }
                            maxRotationDegree = valueET;
                            break;
                        // Min rotation ET
                        case 3:
                            if (valueET > maxRotationDegree) {
                                ET.setError(getString(R.string.min_rot_error_msg));
                            }
                            if (valueET <= maxRotationDegree) {
                                configParamETs.get(2).setError(null);
                            }
                            minRotationDegree = valueET;
                            break;
                        // Session tap number ET
                        case 4:
                            sessionTapNum = valueET;
                            break;
                    }
                }
            });
        }

        sessionNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sessionName = s.toString();
            }
        });

        usernameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                username = s.toString();
            }
        });

        startNewSessionBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (settingsValid()) {
                    newSettings = true;
                    saveNewSettings();
                    newSettings = false;
                    finish();
                } else {
                    showInvalidValuesErrorDialog();
                }
                return true;
            }
        });

        backBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return true;
            }
        });

        objectTypes = TapActivity.stringToBoolArray(objectTypesString);

        int[] objectTypeIds = new int[]{R.id.circleCB, R.id.squaresCB, R.id.trianglesCB};

        for (int i = 0; i < objectTypes.size(); i++) {
            CheckBox cb = findViewById(objectTypeIds[i]);
            if (objectTypes.get(i)) {
                cb.setChecked(true);
            } else {
                cb.setChecked(false);
            }
        }

    }

    private String getEnabledShapes() {
        int[] objectCBids = new int[]{R.id.circleCB, R.id.squaresCB, R.id.trianglesCB};
        String result = "";

        for (int i = 0; i < objectCBids.length; i++) {
            CheckBox cb = findViewById(objectCBids[i]);
            if (cb.isChecked()) {
                result += "1";
            } else {
                result += "0";
            }
        }

        return result;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(getString(R.string.save_settings_dialog_msg));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getString(R.string.save_btn_text),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (settingsValid()) {
                            newSettings = true;
                            saveNewSettings();
                            newSettings = false;
                            finish();
                        } else {
                            dialog.dismiss();
                            showInvalidValuesErrorDialog();
                        }
                    }
                });

        builder1.setNegativeButton(
                getString(R.string.dont_save_btn_text),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        builder1.setNeutralButton(
                getString(R.string.cancel_btn_text),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
        alert11.setCancelable(false);
    }

    private void showInvalidValuesErrorDialog() {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(ConfigurationActivity.this);
        builder2.setMessage(getString(R.string.invalid_settings_dialog_msg));
        builder2.setCancelable(false);

        builder2.setPositiveButton(
                getString(R.string.ok_btn_text),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }
        );
        AlertDialog alert2 = builder2.create();
        alert2.show();
    }

    private Boolean settingsValid() {
        if (maxBound >= minBound && canvasShorterSide > 2*minBound &&
            canvasShorterSide > 2*maxBound && maxRotationDegree >= minRotationDegree) {
            return true;
        }
        return false;
    }

    private void saveNewSettings() {
        // Commit configuration / preferences data to Shared Preferences
        editor.putString(getString(R.string.shapes_cb_file_key), getEnabledShapes());
        editor.putInt(getString(R.string.size_max_bound_file_key), maxBound);
        editor.putInt(getString(R.string.size_min_bound_file_key), minBound);
        editor.putInt(getString(R.string.rotation_max_bound_file_key), maxRotationDegree);
        editor.putInt(getString(R.string.rotation_min_bound_file_key), minRotationDegree);
        editor.putString(getString(R.string.session_name_file_key), sessionName);
        editor.putString(getString(R.string.username_file_key), username);
        editor.putInt(getString(R.string.session_tapnum_file_key), sessionTapNum);
        editor.putBoolean(getString(R.string.new_settings_boolean_file_key), newSettings);
        editor.putBoolean(getString(R.string.double_tap_cb_file_key), doubleTapCB.isChecked());

        if (interactionTypeString.equals("long tap")) {
            editor.putFloat(getString(R.string.dwell_time_file_key), dwellTime);
        }
        else if (interactionTypeString.equals("double tap")) {
            editor.putFloat(getString(R.string.double_tap_time_file_key), doubleTapTime);
        }

        editor.commit();
    }

}


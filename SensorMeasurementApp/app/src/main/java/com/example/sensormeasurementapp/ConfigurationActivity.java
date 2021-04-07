package com.example.sensormeasurementapp;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.ObjectStreamException;
import java.util.ArrayList;

public class ConfigurationActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<Boolean> objectTypes;
    int minBound, maxBound, minRotationDegree, maxRotationDegree;
    String sessionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        setContentView(R.layout.activity_configuration);

        // Fetch relevant data from Shared Preferences
        String objectTypesString = sharedPreferences.getString(getString(R.string.shapes_cb_file_key), "01");
        minBound = sharedPreferences.getInt(getString(R.string.size_min_bound_file_key), 10);
        maxBound = sharedPreferences.getInt(getString(R.string.size_max_bound_file_key), 500);
        minRotationDegree = sharedPreferences.getInt(getString(R.string.rotation_min_bound_file_key), 0);
        maxRotationDegree = sharedPreferences.getInt(getString(R.string.rotation_max_bound_file_key), 0);
        sessionName = sharedPreferences.getString(getString(R.string.session_name_file_key), "default");

        EditText maxSizeET = findViewById(R.id.maxSizeInputText);
        EditText minSizeET = findViewById(R.id.minSizeInputText);
        EditText maxRotationET = findViewById(R.id.maxRotationInputText);
        EditText minRotationET = findViewById(R.id.minRotationInputText);

        EditText sessionNameET = findViewById(R.id.sessionNameInputText);

        ArrayList<EditText> configETs = new ArrayList<>();
        configETs.add(maxSizeET);
        configETs.add(minSizeET);
        configETs.add(maxRotationET);
        configETs.add(minRotationET);

        int[] configETsIDs = new int[]{R.id.maxSizeInputText, R.id.minSizeInputText, R.id.maxRotationInputText, R.id.minRotationInputText};

        maxSizeET.setText(String.valueOf(maxBound));
        minSizeET.setText(String.valueOf(minBound));
        maxRotationET.setText(String.valueOf(maxRotationDegree));
        minRotationET.setText(String.valueOf(minRotationDegree));

        sessionNameET.setText(sessionName);

        for (int i = 0; i < configETs.size(); i++) {
            EditText ET = configETs.get(i);
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
                        valueET = Integer.parseInt(s.toString());
                    }

                    switch (finalI) {
                        case 0:
                            if (valueET < minBound) {
                                ET.setError("Must be greater than Min. size!");
                            }
                            if (valueET >= minBound) {
                                configETs.get(1).setError(null);
                            }
                            maxBound = valueET;
                            break;
                        case 1:
                            if (valueET > maxBound) {
                                ET.setError("Must be lesser than Max. size!");
                            }
                            if (valueET <= maxBound) {
                                configETs.get(0).setError(null);
                            }
                            minBound = valueET;
                            break;
                        case 2:
                            if (valueET < minRotationDegree) {
                                ET.setError("Must be greater than Min. rotation!");
                            }
                            if (valueET >= minRotationDegree) {
                                configETs.get(3).setError(null);
                            }
                            maxRotationDegree = valueET;
                            break;
                        case 3:
                            if (valueET > maxRotationDegree) {
                                ET.setError("Must be lesser than Max. rotation!");
                            }
                            if (valueET <= maxRotationDegree) {
                                configETs.get(2).setError(null);
                            }
                            minRotationDegree = valueET;
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

        System.out.println("objectTypesString: " + objectTypesString);
        objectTypes = MainActivity.stringToBoolArray(objectTypesString);


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
    protected void onDestroy() {
        super.onDestroy();
        //editor.commit();

        //Intent intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private class SharedPreferencesAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            editor.commit();
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Save these settings? Select Cancel to stay on this screen.");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        if (settingsValid()) {
                            saveNewSettings();
                            finish();
                        } else {
                            dialog.dismiss();

                            AlertDialog.Builder builder2 = new AlertDialog.Builder(ConfigurationActivity.this);
                            builder2.setMessage("Invalid settings! Input correct values and try again.");
                            builder2.setCancelable(false);

                            builder2.setPositiveButton(
                                    "OK",
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
                    }
                });

        builder1.setNegativeButton(
                "Don't Save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        builder1.setNeutralButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
        alert11.setCancelable(false);
    }

    private Boolean settingsValid() {
        if (maxBound >= minBound && maxRotationDegree >= minRotationDegree) {
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

        editor.commit();
    }

}


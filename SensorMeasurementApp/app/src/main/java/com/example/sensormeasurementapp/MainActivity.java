package com.example.sensormeasurementapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button singleTapTestingMenuButton = findViewById(R.id.singleTapTestingButton);
        Button longTapTestingMenuButton = findViewById(R.id.longTapTestingButton);
        Button doubleTapTestingMenuButton = findViewById(R.id.doubleTapTestingButton);

        singleTapTestingMenuButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(v.getContext(), SingleTapActivity.class);
                startActivity(intent);
                return true;
            }
        });

        longTapTestingMenuButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(v.getContext(), LongTapActivity.class);
                startActivity(intent);
                return true;
            }
        });
    }
}
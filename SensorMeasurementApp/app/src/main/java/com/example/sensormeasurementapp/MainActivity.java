package com.example.sensormeasurementapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView singleTapTestingMenuButton = findViewById(R.id.singleTapTestingButton);
        ImageView longTapTestingMenuButton = findViewById(R.id.longTapTestingButton);
        ImageView doubleTapTestingMenuButton = findViewById(R.id.doubleTapTestingButton);

        singleTapTestingMenuButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        Intent intent = new Intent(v.getContext(), SingleTapActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        longTapTestingMenuButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        Intent intent = new Intent(v.getContext(), LongTapActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });

        doubleTapTestingMenuButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        Intent intent = new Intent(v.getContext(), DoubleTapActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
    }
}
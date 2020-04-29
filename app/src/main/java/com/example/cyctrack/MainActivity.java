package com.example.cyctrack;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Image;
import android.media.MicrophoneDirection;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.cyctrack.Model.Main;
import com.example.cyctrack.Model.Weather;

public class MainActivity extends AppCompatActivity {
    private NotificationManager mNotificationManager;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        ImageView img_maps, img_weather, img_reviews, img_speedo;
        Button btn_dnd, btn_map, btn_weather, btn_review, btn_speedometer;

        img_maps = findViewById(R.id.img_maps);
        img_weather = findViewById(R.id.img_weather);
        img_reviews = findViewById(R.id.img_reviews);
        img_speedo = findViewById(R.id.img_speedo);
        btn_dnd = findViewById(R.id.btn_dnd);

        btn_map = findViewById(R.id.btn_maps);
        btn_weather = findViewById(R.id.btn_weather);
        btn_review = findViewById(R.id.btn_review);
        btn_speedometer = findViewById(R.id.btn_speedometer);

        btn_dnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeInterruptionFiler(NotificationManager.INTERRUPTION_FILTER_NONE);
                Toast.makeText(MainActivity.this, "DND Enabled!", Toast.LENGTH_SHORT).show();
            }
        });

        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

        btn_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, WeatherActivity.class);
                startActivity(i);
            }
        });

        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ReviewActivity.class);
                startActivity(i);
            }
        });

        btn_speedometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SpeedActivity.class);
                startActivity(i);
            }
        });
    }

    protected void changeInterruptionFiler(int interruptionFilter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // If api level minimum 23
            if (mNotificationManager.isNotificationPolicyAccessGranted()) {
                mNotificationManager.setInterruptionFilter(interruptionFilter);
            } else {
                Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                startActivity(intent);
            }
        }
    }
}
package com.mirrormind.ipsgroup.backgroundservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import com.mirrormind.ipsgroup.R;

public class StartService extends AppCompatActivity {

    Handler Lochand;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);


        Lochand = new Handler();

        Button bn_start = findViewById(R.id.start);
        Button bn_stop = findViewById(R.id.stop);
        bn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(getApplicationContext(), NotificationService.class);
                serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
                ContextCompat.startForegroundService(getApplicationContext(), serviceIntent);
            }
        });
        bn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(getApplicationContext(), NotificationService.class);
                stopService(serviceIntent);
            }
        });
    }
}
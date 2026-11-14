package com.example.app;
import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;

import networking.ConnectionManager;
import random.SwitchActivities_Runnable;

public class MainActivity extends ComponentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) { public void handleOnBackPressed() {} });
        setContentView(R.layout.activity_main);

        ConnectionManager.startListening(new SwitchActivities_Runnable(this, RemoteSessionActivity.class));
        findViewById(R.id.connect).setOnClickListener(v -> ConnectionManager.sendBroadcast());
    }
}
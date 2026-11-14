package com.example.app;
import android.app.Activity;
import android.os.Bundle;

import networking.ConnectionManager;
import random.SwitchActivities_Runnable;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectionManager.startListening(new SwitchActivities_Runnable(this, RemoteSessionActivity.class));
        findViewById(R.id.connect).setOnClickListener(v -> ConnectionManager.sendBroadcast());
    }
}
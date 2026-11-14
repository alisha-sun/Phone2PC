package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;

import networking.SessionManager;
import random.SwitchActivities_Runnable;

public class RemoteSessionActivity extends ComponentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) { public void handleOnBackPressed() {} });

        View view = new View(this);
        setContentView(view);

        SessionManager.start(new SwitchActivities_Runnable(this, MainActivity.class));
    }
}

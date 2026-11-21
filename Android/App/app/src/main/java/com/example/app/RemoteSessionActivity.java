package com.example.app;

import android.os.Bundle;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import networking.SessionManager;
import random.SwitchActivities_Runnable;

public class RemoteSessionActivity extends ComponentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.hide(WindowInsetsCompat.Type.systemBars());
        controller.setSystemBarsBehavior(
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        );

        getOnBackPressedDispatcher().addCallback(
            this,
            new OnBackPressedCallback(true) {
                public void handleOnBackPressed() {
                    SessionManager.closeConnection();
                }
            }
        );

        View view = new View(this);
        setContentView(view);

        SessionManager.start(new SwitchActivities_Runnable(this, MainActivity.class));
    }
}

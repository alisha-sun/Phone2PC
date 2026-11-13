package com.example.app;
import android.app.Activity;
import android.os.Bundle;

import networking.ConnectionManager;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread listener = new Thread(ConnectionManager::listenForResponse);
        listener.start();

        findViewById(R.id.connect).setOnClickListener(  v -> {
            Thread thread = new Thread(() -> System.out.println("Broadcast count: "  + ConnectionManager.sendBroadcast()) );
            thread.start();
        });
    }
}
package com.example.app;
import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.submit);

        button.setOnClickListener(v -> {
            Thread thread = new Thread(() -> {
                System.out.println(
                    "Broadcast count: "  + ConnectionManager.sendBroadcast()
                );
            });
            thread.start();
        });
    }
}
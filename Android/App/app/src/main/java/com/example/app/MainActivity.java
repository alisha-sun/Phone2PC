package com.example.app;
import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText input = findViewById(R.id.input);
        Button button = findViewById(R.id.submit);
        button.setOnClickListener(v -> {
            String text = input.getText().toString();
            Thread thread = new Thread(() -> {
                ConnectionManager.connectToPC(text);
            });
            thread.start();
        });
    }
}
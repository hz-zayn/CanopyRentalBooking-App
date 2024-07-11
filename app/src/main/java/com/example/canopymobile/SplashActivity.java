package com.example.canopymobile;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Start a new thread to show the splash screen for a few seconds
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000); // Show splash screen for 3 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Start the main activity
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }).start();
    }
}
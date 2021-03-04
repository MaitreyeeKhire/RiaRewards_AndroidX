package com.pegasusgroup.riarewards.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.pegasusgroup.riarewards.utils.SessionManager;

public class Splash extends AppCompatActivity {
    private SessionManager sessionManager;
    private Handler splashHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        splashHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!sessionManager.getUserName().isEmpty() && !sessionManager.getPassword().isEmpty()) {
                    startActivity(new Intent(Splash.this, Home.class));
                    overridePendingTransition(0, 0);
                    finish();
                } else {
                    startActivity(new Intent(Splash.this, Login.class));
                    overridePendingTransition(0, 0);
                    finish();
                }
            }
        }, 2000);
    }
}
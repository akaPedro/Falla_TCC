package com.example.falla.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.falla.R;

public class SplashActivity extends AppCompatActivity {

    private static final int DURACAO_MS = 3500; // 3,5 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
        }, DURACAO_MS);
    }
}
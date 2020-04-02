package com.rentme.rentme.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.rentme.rentme.R;
import com.rentme.rentme.models.User;
import com.rentme.rentme.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        new Handler().postDelayed(new Runnable() {

// Using handler with postDelayed called runnable run method

            @Override

            public void run() {
                SharedPreferences preferences = getSharedPreferences(Constants.sharePrefName, MODE_PRIVATE);
                final String userData = preferences.getString("userDetails", null);
                if (userData == null) {
                    startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                    // close this activity
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                }
            }

        }, 1000); // wait for 5 seconds


    }
}

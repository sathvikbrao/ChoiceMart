package com.example.choicemart1;

import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class MainActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 3000;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Delayed execution of the next activity using a Handler
        new Handler().postDelayed(() -> {
            // Start the next activity after the splash screen duration
            Intent intent = new Intent(MainActivity.this,   SignupLogin.class);
            startActivity(intent);


            finish(); // Close the splash screen activity
        }, SPLASH_DURATION);

    }

}
package com.example.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LandingPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);
        Button logInHereButton  = (Button)findViewById(R.id.logInHereButton);
        logInHereButton.setPaintFlags(logInHereButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public void startJourneyButtonClicked(View view) {
        Intent intent = new Intent(this, TestOnboardingActivity.class);
        startActivity(intent);
    }

    public void logInHereButtonClicked(View view) {
        Intent intent = new Intent(this, JoinOrLogInActivity.class);
        startActivity(intent);
    }
}
package com.example.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class TestOnboardingActivity extends AppCompatActivity {

    FirstRegistrationFragment firstRegistrationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_onboarding);
        firstRegistrationFragment = new FirstRegistrationFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.testFragmentContainerView,
                firstRegistrationFragment).commit();
    }
}
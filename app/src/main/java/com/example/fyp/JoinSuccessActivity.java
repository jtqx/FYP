package com.example.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class JoinSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_success);
    }

    public void onNextButtonClicked(View view) {
        Intent intent = new Intent(this, JoinOrLogInActivity.class);
        startActivity(intent);
        finish();
    }
}
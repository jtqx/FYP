package com.example.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class PostLogInActivity extends AppCompatActivity {
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_log_in);
        SharedPreferences sharedPreferences = getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        TextView welcomeTextView = (TextView)findViewById(R.id.welcomeTextView);
        welcomeTextView.setText("Welcome, " + email);
    }
}
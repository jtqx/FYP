package com.example.fyp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BusinessHomeActivity extends AppCompatActivity {

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_business);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        TextView welcomeTextView = (TextView)findViewById(R.id.businessWelcomeText);
        welcomeTextView.setText("Welcome, " + email);

        LinearLayout recipe = toolbar.findViewById(R.id.action_recipe);
        LinearLayout logoff = toolbar.findViewById(R.id.action_logoff);

        recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start intent for recipe activity
            }
        });

        logoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start activity for logoff
            }
        });
    }
}
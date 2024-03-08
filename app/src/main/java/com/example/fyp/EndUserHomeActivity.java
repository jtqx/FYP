package com.example.fyp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EndUserHomeActivity extends AppCompatActivity {

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_user_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_user);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPreferences = getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        TextView welcomeTextView = (TextView)findViewById(R.id.userWelcomeText);
        welcomeTextView.setText("Welcome, " + email);

        LinearLayout home = toolbar.findViewById(R.id.action_home);
        LinearLayout log = toolbar.findViewById(R.id.action_log);
        LinearLayout recipe = toolbar.findViewById(R.id.action_recipe);
        LinearLayout account = toolbar.findViewById(R.id.action_account);
        LinearLayout logoff = toolbar.findViewById(R.id.action_logoff);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start activity for home
            }
        });
        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start activity for meal log
            }
        });
        recipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start intent for recipe activity
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start activity for account
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
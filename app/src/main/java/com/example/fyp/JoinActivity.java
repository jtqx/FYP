package com.example.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class JoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
    }

    public void onJoinButtonClicked(View view) {
        EditText emailEditText = (EditText)findViewById(R.id.emailEditText);
        String email = emailEditText.getText().toString();
        EditText enterPasswordEditText = (EditText)findViewById(R.id.passwordEditText);
        String password = enterPasswordEditText.getText().toString();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        boolean joinSuccess = dbHelper.createUser(email, password);
        if (!joinSuccess) {
            Toast.makeText(this,
                    "An account with this email address already exists!",
                    Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, JoinSuccessActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
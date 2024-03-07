package com.example.fyp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }

    public void loginButtonClicked(View view) {
        EditText emailEditText = (EditText)findViewById(R.id.emailEditText);
        String email = emailEditText.getText().toString();
        EditText passwordEditText = (EditText)findViewById(R.id.passwordEditText);
        String password = passwordEditText.getText().toString();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        boolean logInUser = dbHelper.logInUser(email, password);
        if (!logInUser) {
            Toast.makeText(this, "Invalid Email or Password",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
            SharedPreferences sharedPreferences = getSharedPreferences("SharedPref",
                    MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", email);
            editor.apply();
            Intent intent = new Intent(this, PostLogInActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
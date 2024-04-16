package com.example.fyp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogInFragment extends Fragment implements View.OnClickListener {

    View view;
    Button logInButton;
    EditText emailEditText;
    EditText passwordEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_log_in, container, false);
        // return inflater.inflate(R.layout.fragment_log_in, container, false);
        emailEditText = (EditText)view.findViewById(R.id.emailEditText);
        passwordEditText = (EditText)view.findViewById(R.id.passwordEditText);
        logInButton = (Button)view.findViewById(R.id.logInButton);
        logInButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        /*DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        boolean logInUser = dbHelper.logInUser(email, password);
        if (!logInUser) {
            Toast.makeText(getActivity(), "Invalid Email or Password",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Login Success", Toast.LENGTH_SHORT).show();
            SharedPreferences sharedPreferences = getActivity()
                    .getSharedPreferences("SharedPref",
                    Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("email", email);
            editor.apply();
            Intent intent = new Intent(getActivity(), EndUserHomeActivity.class);
            startActivity(intent);
            getActivity().finish();
        }*/

        User user = new User();
        user.logInUser(email, password, new User.UserCallback() {

            @Override
            public void onSuccess() {
                Toast.makeText(getActivity(), "Login Success", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getActivity()
                        .getSharedPreferences("SharedPref",
                                Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", email);
                editor.apply();
                Intent intent = new Intent(getActivity(), EndUserHomeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getActivity(), "Invalid Email or Password",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
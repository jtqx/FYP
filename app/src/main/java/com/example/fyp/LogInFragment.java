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
        view = inflater.inflate(R.layout.fragment_log_in, container, false);
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

        User user = new User();
        user.logInUser(email, password, new User.UserCallbackWithType<String>() {

            @Override
            public void onSuccess(String result) {
                Toast.makeText(getActivity(), "Login Success", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getActivity()
                        .getSharedPreferences("SharedPref",
                                Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", email);
                editor.apply();
                if (result.equals("End User")) {
                    Intent intent = new Intent(getActivity(), EndUserHomeActivity.class);
                    startActivity(intent);
                } else if (result.equals("Business Partner")) {
                    Intent intent = new Intent(getActivity(), BusinessHomeActivity.class);
                    startActivity(intent);
                } else if (result.equals("Admin")) {
                    Intent intent = new Intent(getActivity(), AdminHomeActivity.class);
                    startActivity(intent);
                }
                getActivity().finish();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getActivity(), "Invalid email/password or account deactivated",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
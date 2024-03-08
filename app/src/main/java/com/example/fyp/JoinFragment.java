package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class JoinFragment extends Fragment implements View.OnClickListener {

    View view;
    Button logInButton;
    EditText emailEditText;
    EditText passwordEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_join, container, false);
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
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        boolean joinSuccess = dbHelper.createUser(email, password);
        if (!joinSuccess) {
            Toast.makeText(getActivity(),
                    "An account with this email address already exists!",
                    Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getActivity(), JoinSuccessActivity.class);
            startActivity(intent);
        }
    }
}
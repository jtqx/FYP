package com.example.fyp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

public class EndUserEditGeneralProfileFragment extends Fragment implements View.OnClickListener {

    View view;
    String email;
    EditText firstNameEditText;
    EditText lastNameEditText;
    EditText emailAddressEditText;
    Button confirmChangesButton;
    EndUserGeneralProfileFragment endUserGeneralProfileFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_end_user_edit_general_profile, container,
                false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                "SharedPref", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

        firstNameEditText = (EditText)view.findViewById(R.id.firstNameEditText);
        lastNameEditText = (EditText)view.findViewById(R.id.lastNameEditText);
        emailAddressEditText = (EditText)view.findViewById(R.id.emailAddressEditText);
        confirmChangesButton = (Button)view.findViewById(R.id.confirmChangesButton);
        endUserGeneralProfileFragment = new EndUserGeneralProfileFragment();

        emailAddressEditText.setText(email);
        emailAddressEditText.setEnabled(false);
        confirmChangesButton.setOnClickListener(this);

        User user = new User();
        user.getUser(email, new User.UserCallbackWithType<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                firstNameEditText.setText(result.get("firstName").toString());
                lastNameEditText.setText(result.get("lastName").toString());
            }

            @Override
            public void onFailure(Exception e) {
                Toast toast = Toast.makeText(getActivity(), "Error",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        String newFirstName = firstNameEditText.getText().toString();
        String newLastName = lastNameEditText.getText().toString();
        if (newFirstName.isEmpty() || newLastName.isEmpty()) {
            Toast toast = Toast.makeText(getActivity(), "No field must be empty",
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        User user = new User();
        user.updateUserGeneralProfile(email, newFirstName, newLastName, new User.UserCallback() {
            @Override
            public void onSuccess() {
                Toast toast = Toast.makeText(getActivity(), "Changes Saved",
                        Toast.LENGTH_SHORT);
                toast.show();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.endUserFragmentContainerView, endUserGeneralProfileFragment)
                        .commit();
            }

            @Override
            public void onFailure(Exception e) {
                Toast toast = Toast.makeText(getActivity(), "Changes Not Saved",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
package com.example.fyp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

public class AdminEditAccountInformationFragment extends Fragment implements View.OnClickListener {

    View view;
    String email;
    EditText adminFirstNameEditText;
    EditText adminLastNameEditText;
    Button confirmChangesButton;
    AdminAccountInformationFragment adminAccountInformationFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_admin_edit_account_information, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                "SharedPref", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

        adminFirstNameEditText = (EditText)view.findViewById(R.id.adminFirstNameEditText);
        adminLastNameEditText = (EditText)view.findViewById(R.id.adminLastNameEditText);
        confirmChangesButton = (Button)view.findViewById(R.id.confirmChangesButton);

        adminAccountInformationFragment = new AdminAccountInformationFragment();

        confirmChangesButton.setOnClickListener(this);

        User user = new User();
        user.getUser(email, new User.UserCallbackWithType<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                adminFirstNameEditText.setText(result.get("firstName").toString());
                adminLastNameEditText.setText(result.get("lastName").toString());
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
        String newAdminFirstName = adminFirstNameEditText.getText().toString();
        String newAdminLastName = adminLastNameEditText.getText().toString();
        if (newAdminFirstName.isEmpty() || newAdminLastName.isEmpty()) {
            Toast toast = Toast.makeText(getActivity(), "No field must be empty",
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        User user = new User();

        user.updateAdminAccountInformation(email, newAdminFirstName, newAdminLastName, new User.UserCallback() {
            @Override
            public void onSuccess() {
                Toast toast = Toast.makeText(getActivity(), "Changes Saved",
                        Toast.LENGTH_SHORT);
                toast.show();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.adminFragmentContainerView, adminAccountInformationFragment)
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
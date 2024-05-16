package com.example.fyp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AdminEndUserDetailsFragment extends Fragment {

    private TextView firstNameTextView;
    private TextView lastNameTextView;
    private TextView emailAddressTextView;
    private Admin admin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_end_user_details, container, false);

        firstNameTextView = view.findViewById(R.id.firstNameTextView);
        lastNameTextView = view.findViewById(R.id.lastNameTextView);
        emailAddressTextView = view.findViewById(R.id.emailAddressTextView);
        admin = new Admin();

        Bundle args = getArguments();
        if (args != null) {
            String email = args.getString("email");
            String userType = args.getString("userType");
            getUserDetails(email, userType);
        }

        return view;
    }

    private void getUserDetails(String email, String userType) {
        admin.getEndUserDetails(email, userType, new Admin.EndUserDetailsCallback() {
            @Override
            public void onSuccess(String firstName, String lastName, String email) {
                if (firstName != null && lastName != null) {
                    firstNameTextView.setText(firstName);
                    lastNameTextView.setText(lastName);
                    emailAddressTextView.setText(email);
                } else {
                    Log.d("AdminAdminDetails", "No matching document found.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("AdminAdminDetails", "Error getting documents: ", e);
            }
        });
    }
}
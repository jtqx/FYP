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

public class AdminAdminDetailsFragment extends Fragment {

    private TextView adminFirstNameTextView;
    private TextView adminLastNameTextView;
    private Admin admin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_admin_details, container, false);

        adminFirstNameTextView = view.findViewById(R.id.adminFirstNameTextView);
        adminLastNameTextView = view.findViewById(R.id.adminLastNameTextView);
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
        admin.getAdminDetails(email, userType, new Admin.AdminDetailsCallback() {
            @Override
            public void onSuccess(String firstName, String lastName) {
                if (firstName != null && lastName != null) {
                    adminFirstNameTextView.setText(firstName);
                    adminLastNameTextView.setText(lastName);
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
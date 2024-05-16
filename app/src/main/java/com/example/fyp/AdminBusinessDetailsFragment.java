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

import org.w3c.dom.Text;

public class AdminBusinessDetailsFragment extends Fragment {

    private TextView companyNameTextView;
    private TextView companyAddressTextView;
    private TextView companyEmailTextView;
    private TextView contactNumberTextView;
    private Admin admin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_business_details, container, false);

        companyNameTextView = view.findViewById(R.id.companyNameTextView);
        companyAddressTextView = view.findViewById(R.id.companyAddressTextView);
        companyEmailTextView = view.findViewById(R.id.companyEmailTextView);
        contactNumberTextView = view.findViewById(R.id.contactNumberTextView);
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
        admin.getBusinessDetails(email, userType, new Admin.BusinessDetailsCallback() {
            @Override
            public void onSuccess(String name, String address, int contact, String email) {
                if (name != null && address != null && contact != 0 && email != null) {
                    companyNameTextView.setText(name);
                    companyAddressTextView.setText(address);
                    contactNumberTextView.setText(String.valueOf(contact));
                    companyEmailTextView.setText(email);
                } else {
                    Log.d("AdminBusinessDetails", "No matching document found.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("AdminBusinessDetails", "Error getting documents: ", e);
            }
        });
    }
}
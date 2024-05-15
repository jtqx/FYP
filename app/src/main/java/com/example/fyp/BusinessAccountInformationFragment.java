package com.example.fyp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class BusinessAccountInformationFragment extends Fragment implements View.OnClickListener {
    String email;
    View view;
    TextView companyNameTextView;
    TextView companyEmailTextView;
    TextView companyAddressTextView;
    TextView contactNumberTextView;
    Button editAccountInformationButton;
    BusinessEditAccountInformationFragment businessEditAccountInformationFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_business_account_information, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                "SharedPref", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

        companyNameTextView = (TextView)view.findViewById(R.id.companyNameTextView);
        companyEmailTextView = (TextView)view.findViewById(R.id.companyEmailTextView);
        companyEmailTextView.setText(email);
        companyAddressTextView = (TextView)view.findViewById(R.id.companyAddressTextView);
        contactNumberTextView = (TextView)view.findViewById(R.id.contactNumberTextView);

        editAccountInformationButton = (Button)view.findViewById(R.id.editAccountInformationButton);

        businessEditAccountInformationFragment = new BusinessEditAccountInformationFragment();

        editAccountInformationButton.setOnClickListener(this);

        User user = new User();
        user.getUser(email, new User.UserCallbackWithType<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                companyNameTextView.setText(result.get("companyName").toString());
                companyAddressTextView.setText(result.get("companyAddress").toString());
                contactNumberTextView.setText(result.get("contactNumber").toString());
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
        int id = v.getId();
        if (id == R.id.editAccountInformationButton) {
            Toast toast = Toast.makeText(getActivity(), "Edit Account Information", Toast.LENGTH_SHORT);
            toast.show();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.businessFragmentContainerView, businessEditAccountInformationFragment)
                    .commit();
        }
    }
}
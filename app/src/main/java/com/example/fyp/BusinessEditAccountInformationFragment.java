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

public class BusinessEditAccountInformationFragment extends Fragment implements View.OnClickListener {

    View view;
    String email;
    EditText companyNameEditText;
    EditText companyEmailEditText;
    EditText companyAddressEditText;
    EditText contactNumberEditText;
    Button confirmChangesButton;
    BusinessAccountInformationFragment businessAccountInformationFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_business_edit_account_information, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                "SharedPref", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

        companyNameEditText = (EditText)view.findViewById(R.id.companyNameEditText);
        companyEmailEditText = (EditText)view.findViewById(R.id.companyEmailEditText);
        companyAddressEditText = (EditText)view.findViewById(R.id.companyAddressEditText);
        contactNumberEditText = (EditText)view.findViewById(R.id.contactNumberEditText);
        confirmChangesButton = (Button)view.findViewById(R.id.confirmChangesButton);

        businessAccountInformationFragment = new BusinessAccountInformationFragment();

        companyEmailEditText.setText(email);
        companyEmailEditText.setEnabled(false);
        confirmChangesButton.setOnClickListener(this);

        User user = new User();
        user.getUser(email, new User.UserCallbackWithType<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                companyNameEditText.setText(result.get("companyName").toString());
                companyAddressEditText.setText(result.get("companyAddress").toString());
                contactNumberEditText.setText(result.get("contactNumber").toString());
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
        String newCompanyName = companyNameEditText.getText().toString();
        String newCompanyAddress = companyAddressEditText.getText().toString();
        int newContactNumber = Integer.parseInt(contactNumberEditText.getText().toString());
        if (newCompanyName.isEmpty() || newCompanyAddress.isEmpty()) {
            Toast toast = Toast.makeText(getActivity(), "No field must be empty",
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        User user = new User();

        user.updateBusinessPartnerAccountInformation(email, newCompanyName, newCompanyAddress, newContactNumber, new User.UserCallback() {
            @Override
            public void onSuccess() {
                Toast toast = Toast.makeText(getActivity(), "Changes Saved",
                        Toast.LENGTH_SHORT);
                toast.show();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.businessFragmentContainerView, businessAccountInformationFragment)
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
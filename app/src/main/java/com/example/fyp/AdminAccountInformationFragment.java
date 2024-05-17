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

public class AdminAccountInformationFragment extends Fragment implements View.OnClickListener {

    String email;
    View view;
    TextView adminFirstNameTextView;
    TextView adminLastNameTextView;
    Button editAccountInformationButton;
    AdminEditAccountInformationFragment adminEditAccountInformationFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin_account_information, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                "SharedPref", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

        adminFirstNameTextView = (TextView)view.findViewById(R.id.adminFirstNameTextView);
        adminLastNameTextView = (TextView)view.findViewById(R.id.adminLastNameTextView);

        editAccountInformationButton = (Button)view.findViewById(R.id.editAccountInformationButton);

        adminEditAccountInformationFragment = new AdminEditAccountInformationFragment();

        editAccountInformationButton.setOnClickListener(this);

        User user = new User();
        user.getUser(email, new User.UserCallbackWithType<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                adminFirstNameTextView.setText(result.get("firstName").toString());
                adminLastNameTextView.setText(result.get("lastName").toString());
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
                    .replace(R.id.adminFragmentContainerView, adminEditAccountInformationFragment)
                    .commit();
        }
    }
}
package com.example.fyp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class BusinessAccountFragment extends Fragment implements View.OnClickListener {

    String email;
    View view;
    TextView nameTextView;
    TextView accountInformationTextView;
    TextView logOutTextView;
    BusinessAccountInformationFragment businessAccountInformationFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_business_account, container, false);

        nameTextView = (TextView)view.findViewById(R.id.nameTextView);
        accountInformationTextView = (TextView)view.findViewById(R.id.generalProfileTextView);
        logOutTextView = (TextView)view.findViewById(R.id.logOutTextView);

        businessAccountInformationFragment = new BusinessAccountInformationFragment();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SharedPref",
                Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        nameTextView.setText(email);

        accountInformationTextView.setOnClickListener(this);
        logOutTextView.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.generalProfileTextView) {
            Toast toast = Toast.makeText(getActivity(), "General Profile", Toast.LENGTH_SHORT);
            toast.show();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.businessFragmentContainerView, businessAccountInformationFragment)
                    .commit();
        } else if (id == R.id.logOutTextView) {
            Intent intent = new Intent(getActivity(), JoinOrLogInActivity.class);
            getActivity().finish();
        } else {
            Toast toast = Toast.makeText(getActivity(), "Deactivate Account", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
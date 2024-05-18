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

public class EndUserAccountFragment extends Fragment implements View.OnClickListener {

    String email;
    View view;
    TextView nameTextView;
    TextView generalProfileTextView;
    TextView bodyProfileTextView;
    TextView medicalHistoryTextView;
    TextView logOutTextView;
    TextView deactivateAccountTextView;
    TextView convertToBusinessPartnerTextView;
    EndUserGeneralProfileFragment endUserGeneralProfileFragment;
    EndUserBodyProfileFragment endUserBodyProfileFragment;
    EndUserMedicalHistoryFragment endUserMedicalHistoryFragment;
    EndUserConvertToBusinessPartnerFragment endUserConvertToBusinessPartnerFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_end_user_account, container, false);
        nameTextView = (TextView)view.findViewById(R.id.nameTextView);
        generalProfileTextView = (TextView)view.findViewById(R.id.generalProfileTextView);
        bodyProfileTextView = (TextView)view.findViewById(R.id.bodyProfileTextView);
        medicalHistoryTextView = (TextView)view.findViewById(R.id.medicalHistoryTextView);
        logOutTextView = (TextView)view.findViewById(R.id.logOutTextView);
        deactivateAccountTextView = (TextView)view.findViewById(R.id.deactivateAccountTextView);
        convertToBusinessPartnerTextView = (TextView)view.findViewById(R.id.convertToBusinessPartnerTextView);
        endUserGeneralProfileFragment = new EndUserGeneralProfileFragment();
        endUserBodyProfileFragment = new EndUserBodyProfileFragment();
        endUserMedicalHistoryFragment = new EndUserMedicalHistoryFragment();
        endUserConvertToBusinessPartnerFragment = new EndUserConvertToBusinessPartnerFragment();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SharedPref",
                Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        nameTextView.setText(email);
        generalProfileTextView.setOnClickListener(this);
        bodyProfileTextView.setOnClickListener(this);
        medicalHistoryTextView.setOnClickListener(this);
        logOutTextView.setOnClickListener(this);
        deactivateAccountTextView.setOnClickListener(this);
        convertToBusinessPartnerTextView.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.generalProfileTextView) {
            Toast toast = Toast.makeText(getActivity(), "General Profile", Toast.LENGTH_SHORT);
            toast.show();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.endUserFragmentContainerView, endUserGeneralProfileFragment)
                    .commit();
        } else if (id == R.id.bodyProfileTextView) {
            Toast toast = Toast.makeText(getActivity(), "Body Profile", Toast.LENGTH_SHORT);
            toast.show();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.endUserFragmentContainerView, endUserBodyProfileFragment)
                    .commit();
        } else if (id == R.id.medicalHistoryTextView) {
            Toast toast = Toast.makeText(getActivity(), "Medical History", Toast.LENGTH_SHORT);
            toast.show();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.endUserFragmentContainerView, endUserMedicalHistoryFragment)
                    .commit();
        } else if (id == R.id.logOutTextView) {
            Intent intent = new Intent(getActivity(), JoinOrLogInActivity.class);
            getActivity().finish();
        } else if (id == R.id.convertToBusinessPartnerTextView) {
            Toast toast = Toast.makeText(getActivity(), "Account Conversion", Toast.LENGTH_SHORT);
            toast.show();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.endUserFragmentContainerView, endUserConvertToBusinessPartnerFragment)
                    .commit();
        } else {
            Toast toast = Toast.makeText(getActivity(), "Deactivate Account", Toast.LENGTH_SHORT);
            toast.show();
            User user = new User();
            user.deactivateUser(email, new User.UserCallback() {
                @Override
                public void onSuccess() {
                    Toast toast = Toast.makeText(getActivity(), "Account Deactivated", Toast.LENGTH_SHORT);
                    toast.show();
                    Intent intent = new Intent(getActivity(), JoinOrLogInActivity.class);
                    getActivity().finish();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast toast = Toast.makeText(getActivity(), "Error in deactivation", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });
        }
    }
}
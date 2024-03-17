package com.example.fyp;

import android.content.Context;
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
    EndUserGeneralProfileFragment endUserGeneralProfileFragment;
    EndUserBodyProfileFragment endUserBodyProfileFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_end_user_account, container, false);
        nameTextView = (TextView)view.findViewById(R.id.nameTextView);
        generalProfileTextView = (TextView)view.findViewById(R.id.generalProfileTextView);
        bodyProfileTextView = (TextView)view.findViewById(R.id.bodyProfileTextView);
        medicalHistoryTextView = (TextView)view.findViewById(R.id.medicalHistoryTextView);
        logOutTextView = (TextView)view.findViewById(R.id.logOutTextView);
        deactivateAccountTextView = (TextView)view.findViewById(R.id.deactivateAccountTextView);
        endUserGeneralProfileFragment = new EndUserGeneralProfileFragment();
        endUserBodyProfileFragment = new EndUserBodyProfileFragment();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SharedPref",
                Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        nameTextView.setText(email);
        generalProfileTextView.setOnClickListener(this);
        bodyProfileTextView.setOnClickListener(this);
        medicalHistoryTextView.setOnClickListener(this);
        logOutTextView.setOnClickListener(this);
        deactivateAccountTextView.setOnClickListener(this);
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
        } else if (id == R.id.logOutTextView) {
            Toast toast = Toast.makeText(getActivity(), "Log Out", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(getActivity(), "Deactivate Account", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
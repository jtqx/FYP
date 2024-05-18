package com.example.fyp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class AdminAccountFragment extends Fragment implements View.OnClickListener {
    View view;
    String email;
    TextView nameTextView;
    TextView accountInformationTextView;
    TextView logOutTextView;
    AdminAccountInformationFragment adminAccountInformationFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_admin_account, container, false);

        nameTextView = (TextView)view.findViewById(R.id.nameTextView);
        accountInformationTextView = (TextView)view.findViewById(R.id.generalProfileTextView);
        logOutTextView = (TextView)view.findViewById(R.id.logOutTextView);

        adminAccountInformationFragment = new AdminAccountInformationFragment();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SharedPref",
                MODE_PRIVATE);
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
                    .replace(R.id.adminFragmentContainerView, adminAccountInformationFragment)
                    .commit();
        } else {
            Intent intent = new Intent(getActivity(), JoinOrLogInActivity.class);
            getActivity().finish();
        }
    }
}
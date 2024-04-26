package com.example.fyp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class EndUserGeneralProfileFragment extends Fragment implements View.OnClickListener {

    String email;
    View view;
    TextView emailTextView;
    TextView firstNameTextView;
    TextView lastNameTextView;
    Button editProfileButton;
    EndUserEditGeneralProfileFragment endUserEditGeneralProfileFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_end_user_general_profile, container,
                false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                "SharedPref", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

        emailTextView = (TextView)view.findViewById(R.id.emailAddressTextView);
        firstNameTextView = (TextView)view.findViewById(R.id.firstNameTextView);
        lastNameTextView = (TextView)view.findViewById(R.id.lastNameTextView);
        editProfileButton = (Button)view.findViewById(R.id.editProfileButton);
        endUserEditGeneralProfileFragment = new EndUserEditGeneralProfileFragment();

        emailTextView.setText(email);
        editProfileButton.setOnClickListener(this);

        /*DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        Cursor userInfo = dbHelper.getUser(email);
        userInfo.moveToFirst();
        if (!userInfo.isNull(1)) {
            firstNameTextView.setText(userInfo.getString(1));
        }
        if (!userInfo.isNull(2)) {
            lastNameTextView.setText(userInfo.getString(2));
        }*/

        User user = new User();
        user.getUser(email, new User.UserCallbackWithType<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                firstNameTextView.setText(result.get("firstName").toString());
                lastNameTextView.setText(result.get("lastName").toString());
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
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.endUserFragmentContainerView, endUserEditGeneralProfileFragment)
                .commit();
    }
}
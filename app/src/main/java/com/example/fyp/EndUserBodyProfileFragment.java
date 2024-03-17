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

public class EndUserBodyProfileFragment extends Fragment implements View.OnClickListener {

    View view;
    String email;
    TextView heightTextView;
    TextView weightTextView;
    TextView bmiTextView;
    Button editBodyProfileButton;
    EndUserEditBodyProfileFragment endUserEditBodyProfileFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_end_user_body_profile, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SharedPref",
                Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

        heightTextView = (TextView)view.findViewById(R.id.heightTextView);
        weightTextView = (TextView)view.findViewById(R.id.weightTextView);
        bmiTextView = (TextView)view.findViewById(R.id.bmiTextView);
        editBodyProfileButton = (Button)view.findViewById(R.id.editBodyProfileButton);
        endUserEditBodyProfileFragment = new EndUserEditBodyProfileFragment();

        editBodyProfileButton.setOnClickListener(this);

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        Cursor c = dbHelper.getBodyProfile(email);

        if (!c.isClosed()) {
            c.moveToFirst();
            heightTextView.setText(c.getString(1));
            weightTextView.setText(c.getString(2));
            bmiTextView.setText(c.getString(3));
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.endUserFragmentContainerView, endUserEditBodyProfileFragment)
                .commit();
    }
}
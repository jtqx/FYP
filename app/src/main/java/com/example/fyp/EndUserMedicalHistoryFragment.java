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

public class EndUserMedicalHistoryFragment extends Fragment implements View.OnClickListener {

    View view;
    String email;
    TextView allergiesTextView;
    TextView chronicConditionsTextView;
    TextView medicationTextView;
    Button editMedicalHistoryButton;
    EndUserEditMedicalHistoryFragment endUserEditMedicalHistoryFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_end_user_medical_history, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SharedPref",
                Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

        allergiesTextView = (TextView)view.findViewById(R.id.allergiesTextView);
        chronicConditionsTextView = (TextView)view.findViewById(R.id.chronicConditionsTextView);
        medicationTextView = (TextView)view.findViewById(R.id.medicationTextView);
        editMedicalHistoryButton = (Button)view.findViewById(R.id.editMedicalHistoryButton);
        endUserEditMedicalHistoryFragment = new EndUserEditMedicalHistoryFragment();

        editMedicalHistoryButton.setOnClickListener(this);

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        Cursor c = dbHelper.getMedicalHistory(email);

        if (!c.isClosed()) {
            c.moveToFirst();
            allergiesTextView.setText(c.getString(1));
            chronicConditionsTextView.setText(c.getString(2));
            medicationTextView.setText(c.getString(3));
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.endUserFragmentContainerView, endUserEditMedicalHistoryFragment)
                .commit();
    }
}
package com.example.fyp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EndUserEditMedicalHistoryFragment extends Fragment implements View.OnClickListener {

    View view;
    String email;
    EditText allergiesEditText;
    EditText chronicConditionsEditText;
    EditText medicationEditText;
    Button confirmMedicalHistoryChangesButton;
    EndUserMedicalHistoryFragment endUserMedicalHistoryFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_end_user_edit_medical_history, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SharedPref",
                Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

        allergiesEditText = (EditText)view.findViewById(R.id.allergiesEditText);
        chronicConditionsEditText = (EditText)view.findViewById(R.id.chronicConditionsEditText);
        medicationEditText = (EditText)view.findViewById(R.id.medicationEditText);
        confirmMedicalHistoryChangesButton = (Button)
                view.findViewById(R.id.confirmMedicalHistoryChangesButton);
        endUserMedicalHistoryFragment = new EndUserMedicalHistoryFragment();

        confirmMedicalHistoryChangesButton.setOnClickListener(this);

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        Cursor c = dbHelper.getMedicalHistory(email);

        if (!c.isClosed()) {
            c.moveToFirst();
            allergiesEditText.setText(c.getString(1));
            chronicConditionsEditText.setText(c.getString(2));
            medicationEditText.setText(c.getString(3));
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        String allergies = allergiesEditText.getText().toString();
        String chronicConditions = chronicConditionsEditText.getText().toString();
        String medication = medicationEditText.getText().toString();

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        boolean success = dbHelper.createMedicalHistory(allergies, chronicConditions,
                medication, email);
        if (success) {
            Toast.makeText(getActivity(), "Medical history successfully added or updated",
                    Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.endUserFragmentContainerView, endUserMedicalHistoryFragment)
                    .commit();
        } else {
            Toast.makeText(getActivity(), "Unsuccessful",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
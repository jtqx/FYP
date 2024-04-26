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

        /*DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        Cursor c = dbHelper.getMedicalHistory(email);

        if (!c.isClosed()) {
            c.moveToFirst();
            allergiesTextView.setText(c.getString(1));
            chronicConditionsTextView.setText(c.getString(2));
            medicationTextView.setText(c.getString(3));
        }*/

        User user = new User();
        user.getUser(email, new User.UserCallbackWithType<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                allergiesTextView.setText(result.get("allergies").toString());
                chronicConditionsTextView.setText(result.get("chronicConditions").toString());
                medicationTextView.setText(result.get("medication").toString());
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
                .replace(R.id.endUserFragmentContainerView, endUserEditMedicalHistoryFragment)
                .commit();
    }
}
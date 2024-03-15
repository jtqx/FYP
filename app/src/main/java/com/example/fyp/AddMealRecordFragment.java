package com.example.fyp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AddMealRecordFragment extends Fragment implements View.OnClickListener {

    View view;
    String date;
    String email;
    String mealType;
    EditText mealNameEditText;
    EditText caloriesEditText;
    EditText carbsEditText;
    EditText fatEditText;
    EditText proteinEditText;
    Button logFoodButton;
    EndUserLogFragment endUserLogFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_meal_record, container, false);
        date = getArguments().getString("Date");
        // Toast.makeText(getActivity(), "Date is: " + date, Toast.LENGTH_SHORT).show();
        mealType = getArguments().getString("Type");
        mealNameEditText = (EditText)view.findViewById(R.id.mealNameEditText);
        caloriesEditText = (EditText)view.findViewById(R.id.caloriesEditText);
        carbsEditText = (EditText)view.findViewById(R.id.carbsEditText);
        fatEditText = (EditText)view.findViewById(R.id.fatEditText);
        proteinEditText = (EditText)view.findViewById(R.id.proteinEditText);
        logFoodButton = (Button)view.findViewById(R.id.logFoodButton);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                "SharedPref", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        endUserLogFragment = new EndUserLogFragment();

        logFoodButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.logFoodButton) {
            String mealName = mealNameEditText.getText().toString();
            int calories = Integer.parseInt(caloriesEditText.getText().toString());
            int carbs = Integer.parseInt(carbsEditText.getText().toString());
            int fat = Integer.parseInt(fatEditText.getText().toString());
            int protein = Integer.parseInt(proteinEditText.getText().toString());
            DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
            boolean success = dbHelper.addMealRecord(date, mealType, mealName, calories, carbs,
                    fat, protein, email);
            if (success) {
                Toast.makeText(getActivity(), "Meal record successfully added",
                        Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.endUserFragmentContainerView, endUserLogFragment)
                        .commit();
            } else {
                Toast.makeText(getActivity(), "Meal record not added",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
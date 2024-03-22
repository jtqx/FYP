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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EndUserEditMealRecordFragment extends Fragment implements View.OnClickListener {

    View view;
    String email;
    String date;
    String mealType;
    String mealName;
    int calories;
    int carbs;
    int fats;
    int protein;
    EditText editCaloriesEditText;
    EditText editCarbsEditText;
    EditText editFatsEditText;
    EditText editProteinEditText;
    Button confirmMealChangesButton;
    ViewMealRecordFragment viewMealRecordFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_end_user_edit_meal_record, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                "SharedPref", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        date = getArguments().getString("Date");
        mealType = getArguments().getString("Meal Type");
        mealName = getArguments().getString("Meal Name");

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        Cursor cursor = dbHelper.getMealRecordByDateTypeName(email, date,
                mealType, mealName);

        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            calories = cursor.getInt(1);
            carbs = cursor.getInt(2);
            fats = cursor.getInt(3);
            protein = cursor.getInt(4);
        }

        editCaloriesEditText = (EditText)view.findViewById(R.id.editCaloriesEditText);
        editCaloriesEditText.setText(String.valueOf(calories));

        editCarbsEditText = (EditText)view.findViewById(R.id.editCarbsEditText);
        editCarbsEditText.setText(String.valueOf(carbs));

        editFatsEditText = (EditText)view.findViewById(R.id.editFatsEditText);
        editFatsEditText.setText(String.valueOf(fats));

        editProteinEditText = (EditText)view.findViewById(R.id.editProteinEditText);
        editProteinEditText.setText(String.valueOf(protein));

        confirmMealChangesButton = (Button)view.findViewById(R.id.confirmMealChangesButton);
        confirmMealChangesButton.setOnClickListener(this);

        viewMealRecordFragment = new ViewMealRecordFragment();

        return view;
    }

    @Override
    public void onClick(View v) {
        calories = Integer.parseInt(editCaloriesEditText.getText().toString());
        carbs = Integer.parseInt(editCarbsEditText.getText().toString());
        fats = Integer.parseInt(editProteinEditText.getText().toString());
        protein = Integer.parseInt(editProteinEditText.getText().toString());
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        boolean success = dbHelper.updateMealRecord(date, mealType, mealName, calories,
                carbs, fats, protein, email);
        if (success) {
            Toast.makeText(getActivity(), "Meal record successfully updated",
                    Toast.LENGTH_SHORT).show();
            Bundle args = new Bundle();
            args.putString("Date", date);
            args.putString("Meal Type", mealType);
            args.putString("Meal Name", mealName);
            viewMealRecordFragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.endUserFragmentContainerView, viewMealRecordFragment)
                    .commit();
        } else {
            Toast.makeText(getActivity(), "Meal record not updated",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
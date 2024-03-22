package com.example.fyp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ViewMealRecordFragment extends Fragment implements View.OnClickListener {

    View view;
    String email;
    String date;
    String mealType;
    String mealName;
    String calories;
    String carbs;
    String fats;
    String protein;
    TextView mealNameTextView;
    TextView caloriesTextView;
    TextView carbsTextView;
    TextView fatsTextView;
    TextView proteinTextView;
    Button deleteMealRecordButton;
    Button editMealRecordButton;
    EndUserLogFragment endUserLogFragment;
    EndUserEditMealRecordFragment endUserEditMealRecordFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_view_meal_record, container, false);
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
            calories = cursor.getString(1);
            carbs = cursor.getString(2);
            fats = cursor.getString(3);
            protein = cursor.getString(4);
        }

        mealNameTextView = (TextView)view.findViewById(R.id.mealNameTextView);
        mealNameTextView.setText(mealName);

        caloriesTextView = (TextView)view.findViewById(R.id.caloriesTextView);
        caloriesTextView.setText(calories);

        carbsTextView = (TextView)view.findViewById(R.id.carbsTextView);
        carbsTextView.setText(carbs);

        fatsTextView = (TextView)view.findViewById(R.id.fatsTextView);
        fatsTextView.setText(fats);

        proteinTextView = (TextView)view.findViewById(R.id.proteinTextView);
        proteinTextView.setText(protein);

        deleteMealRecordButton = (Button)view.findViewById(R.id.deleteMealRecordButton);
        deleteMealRecordButton.setOnClickListener(this);

        editMealRecordButton = (Button)view.findViewById(R.id.editMealRecordButton);
        editMealRecordButton.setOnClickListener(this);

        endUserLogFragment = new EndUserLogFragment();
        endUserEditMealRecordFragment = new EndUserEditMealRecordFragment();

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.deleteMealRecordButton) {
            DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
            boolean success = dbHelper.deleteMealRecord(email, date, mealType, mealName);
            if (success) {
                Toast.makeText(getActivity(), "Meal record successfully deleted",
                        Toast.LENGTH_SHORT).show();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.endUserFragmentContainerView, endUserLogFragment)
                        .commit();
            } else {
                Toast.makeText(getActivity(), "Meal record not deleted",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Bundle args = new Bundle();
            args.putString("Date", date);
            args.putString("Meal Type", mealType);
            args.putString("Meal Name", mealName);
            endUserEditMealRecordFragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.endUserFragmentContainerView, endUserEditMealRecordFragment)
                    .commit();
        }
    }
}
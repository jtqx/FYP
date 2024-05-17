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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

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
        view = inflater.inflate(R.layout.fragment_end_user_edit_meal_record, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                "SharedPref", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        date = getArguments().getString("Date");
        mealType = getArguments().getString("Meal Type");
        mealName = getArguments().getString("Meal Name");

        editCaloriesEditText = (EditText)view.findViewById(R.id.editCaloriesEditText);

        editCarbsEditText = (EditText)view.findViewById(R.id.editCarbsEditText);

        editFatsEditText = (EditText)view.findViewById(R.id.editFatsEditText);

        editProteinEditText = (EditText)view.findViewById(R.id.editProteinEditText);

        confirmMealChangesButton = (Button)view.findViewById(R.id.confirmMealChangesButton);
        confirmMealChangesButton.setOnClickListener(this);

        viewMealRecordFragment = new ViewMealRecordFragment();

        MealRecord mealRecord = new MealRecord();
        mealRecord.getMealRecordByDateTypeName(email, date, "Breakfast", mealName,
                new MealRecord.MealRecordCallbackWithType<List<DocumentSnapshot>>() {
                    @Override
                    public void onSuccess(List<DocumentSnapshot> mealRecords) {
                        DocumentSnapshot mealRecord = mealRecords.get(0);
                        calories = Math.toIntExact(mealRecord.getLong("calories"));
                        carbs = Math.toIntExact(mealRecord.getLong("carbs"));
                        fats = Math.toIntExact(mealRecord.getLong("fats"));
                        protein = Math.toIntExact(mealRecord.getLong("protein"));

                        /* Since the contents of the TextView elements below will be changed after
                        the variables above are updated based on retrieved data from Firebase (an
                        asynchronous operation), the corresponding EditText elements must be
                        updated here. */
                        editCaloriesEditText.setText(String.valueOf(calories));

                        editCarbsEditText.setText(String.valueOf(carbs));

                        editFatsEditText.setText(String.valueOf(fats));

                        editProteinEditText.setText(String.valueOf(protein));
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
        calories = Integer.parseInt(editCaloriesEditText.getText().toString());
        carbs = Integer.parseInt(editCarbsEditText.getText().toString());
        fats = Integer.parseInt(editFatsEditText.getText().toString());
        protein = Integer.parseInt(editProteinEditText.getText().toString());

        MealRecord mealRecord = new MealRecord();
        mealRecord.editMealRecord(email, date, mealType, mealName, calories, carbs, fats, protein,
                new MealRecord.MealRecordCallback() {
                    @Override
                    public void onSuccess() {
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
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getActivity(), "Meal record not updated",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
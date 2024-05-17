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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewMealRecordFragment extends Fragment implements View.OnClickListener {

    View view;
    String email;
    String date;
    String mealType;
    String mealName;
    int calories;
    int carbs;
    int fats;
    int protein;
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

        mealNameTextView = (TextView)view.findViewById(R.id.mealNameTextView);
        mealNameTextView.setText(mealName);

        deleteMealRecordButton = (Button)view.findViewById(R.id.deleteMealRecordButton);
        deleteMealRecordButton.setOnClickListener(this);

        editMealRecordButton = (Button)view.findViewById(R.id.editMealRecordButton);
        editMealRecordButton.setOnClickListener(this);

        endUserLogFragment = new EndUserLogFragment();
        endUserEditMealRecordFragment = new EndUserEditMealRecordFragment();

        caloriesTextView = (TextView)view.findViewById(R.id.caloriesTextView);

        carbsTextView = (TextView)view.findViewById(R.id.carbsTextView);

        fatsTextView = (TextView)view.findViewById(R.id.fatsTextView);

        proteinTextView = (TextView)view.findViewById(R.id.proteinTextView);

        MealRecord mealRecord = new MealRecord();
        mealRecord.getMealRecordByDateTypeName(email, date, mealType, mealName,
                new MealRecord.MealRecordCallbackWithType<List<DocumentSnapshot>>() {
                    @Override
                    public void onSuccess(List<DocumentSnapshot> mealRecords) {
                        // Assuming only one document is returned
                        DocumentSnapshot mealRecord = mealRecords.get(0);
                        calories = Math.toIntExact(mealRecord.getLong("calories"));
                        carbs = Math.toIntExact(mealRecord.getLong("carbs"));
                        fats = Math.toIntExact(mealRecord.getLong("fats"));
                        protein = Math.toIntExact(mealRecord.getLong("protein"));

                        /* Since the contents of the TextView elements below will be changed after
                        the variables above are updated based on retrieved data from Firebase (an
                        asynchronous operation), they must be updated here. */
                        caloriesTextView.setText(String.valueOf(calories));

                        carbsTextView.setText(String.valueOf(carbs));

                        fatsTextView.setText(String.valueOf(fats));

                        proteinTextView.setText(String.valueOf(protein));
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
        int id = v.getId();
        if (id == R.id.deleteMealRecordButton) {
            MealRecord mealRecord = new MealRecord();
            mealRecord.deleteMealRecord(email, date, mealType, mealName,
                    new MealRecord.MealRecordCallback() {
                @Override
                public void onSuccess() {
                    mealRecord.deleteTotalCalories(date,email, calories);
                    Toast.makeText(getActivity(), "Meal record successfully deleted",
                            Toast.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.endUserFragmentContainerView, endUserLogFragment)
                            .commit();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getActivity(), "Meal record not deleted",
                            Toast.LENGTH_SHORT).show();
                }
            });

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
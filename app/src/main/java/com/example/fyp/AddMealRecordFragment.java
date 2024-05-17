package com.example.fyp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    Button favouriteButton;
    EndUserLogFragment endUserLogFragment;
    Boolean favourited;
    Spinner favouriteMealsSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_meal_record2, container, false);
        date = getArguments().getString("Date");
        // Toast.makeText(getActivity(), "Date is: " + date, Toast.LENGTH_SHORT).show();
        mealType = getArguments().getString("Type");
        mealNameEditText = (EditText)view.findViewById(R.id.mealNameEditText);
        caloriesEditText = (EditText)view.findViewById(R.id.caloriesEditText);
        carbsEditText = (EditText)view.findViewById(R.id.carbsEditText);
        fatEditText = (EditText)view.findViewById(R.id.fatEditText);
        proteinEditText = (EditText)view.findViewById(R.id.proteinEditText);
        logFoodButton = (Button)view.findViewById(R.id.logFoodButton);
        favouriteButton = (Button)view.findViewById(R.id.favouriteButton);
        favouriteMealsSpinner = (Spinner)view.findViewById(R.id.favouriteMealsSpinner);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                "SharedPref", Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        endUserLogFragment = new EndUserLogFragment();

        logFoodButton.setOnClickListener(this);

        favouriteButton.setOnClickListener(this);

        favourited = false;

        setupFavouriteMealsSpinner();

        favouriteMealsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedItem = (String)favouriteMealsSpinner.getSelectedItem();
                FavouriteMeal favouriteMeal = new FavouriteMeal();
                favouriteMeal.getFavouriteMeal(email, selectedItem, new FavouriteMeal.CallbackWithType<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        mealNameEditText.setText(result.get("mealName").toString());
                        mealNameEditText.setEnabled(false);
                        caloriesEditText.setText(result.get("calories").toString());
                        caloriesEditText.setEnabled(false);
                        carbsEditText.setText(result.get("carbs").toString());
                        carbsEditText.setEnabled(false);
                        fatEditText.setText(result.get("fats").toString());
                        fatEditText.setEnabled(false);
                        proteinEditText.setText(result.get("protein").toString());
                        proteinEditText.setEnabled(false);

                        favourited = true;
                        favouriteButton.setBackgroundColor(Color.parseColor("#CC0099"));
                        favouriteButton.setText("Remove From Favourites");
                    }

                    @Override
                    public void onFailure(Exception e) {
                    }
                });
                // Toast.makeText(getApplicationContext(), String.valueOf(timeSpinner.getSelectedItem()), Toast.LENGTH_SHORT).show();
                //selectedTimeslot = (String)timeSpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.logFoodButton) {
            if (mealNameEditText.getText().toString().isEmpty() ||
                    caloriesEditText.getText().toString().isEmpty() ||
                    carbsEditText.getText().toString().isEmpty() ||
                    fatEditText.getText().toString().isEmpty() ||
                    proteinEditText.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "All fields must be filled in", Toast.LENGTH_SHORT).show();
                return;
            }

            String mealName = mealNameEditText.getText().toString();
            int calories = Integer.parseInt(caloriesEditText.getText().toString());
            int carbs = Integer.parseInt(carbsEditText.getText().toString());
            int fats = Integer.parseInt(fatEditText.getText().toString());
            int protein = Integer.parseInt(proteinEditText.getText().toString());

            MealRecord mealRecord = new MealRecord();
            mealRecord.createMealRecord(date, mealType, mealName, calories, carbs, fats, protein,
                    email, new MealRecord.MealRecordCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getActivity(), "Meal record successfully added",
                                    Toast.LENGTH_SHORT).show();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.endUserFragmentContainerView, endUserLogFragment)
                                    .commit();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(getActivity(), "Meal record not added",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else if (id == R.id.favouriteButton) {
            if (favourited) {
                if (mealNameEditText.getText().toString().isEmpty() ||
                        caloriesEditText.getText().toString().isEmpty() ||
                        carbsEditText.getText().toString().isEmpty() ||
                        fatEditText.getText().toString().isEmpty() ||
                        proteinEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "All fields must be filled in", Toast.LENGTH_SHORT).show();
                    return;
                }

                String mealName = mealNameEditText.getText().toString();
                int calories = Integer.parseInt(caloriesEditText.getText().toString());
                int carbs = Integer.parseInt(carbsEditText.getText().toString());
                int fats = Integer.parseInt(fatEditText.getText().toString());
                int protein = Integer.parseInt(proteinEditText.getText().toString());

                FavouriteMeal favouriteMeal = new FavouriteMeal();
                favouriteMeal.deleteFavouriteMeal(email, mealName,
                        new FavouriteMeal.Callback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getActivity(),
                                        "Favourite successfully deleted",
                                        Toast.LENGTH_SHORT).show();
                                favourited = false;
                                favouriteButton.setBackgroundColor(Color.parseColor("#FFFFFF"));
                                favouriteButton.setText("Add To Favourites");
                                setupFavouriteMealsSpinner();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getActivity(),
                                        "Favourite not added",
                                        Toast.LENGTH_SHORT).show();

                            }
                        });
            } else {
                if (mealNameEditText.getText().toString().isEmpty() ||
                        caloriesEditText.getText().toString().isEmpty() ||
                        carbsEditText.getText().toString().isEmpty() ||
                        fatEditText.getText().toString().isEmpty() ||
                        proteinEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "All fields must be filled in", Toast.LENGTH_SHORT).show();
                    return;
                }

                String mealName = mealNameEditText.getText().toString();
                int calories = Integer.parseInt(caloriesEditText.getText().toString());
                int carbs = Integer.parseInt(carbsEditText.getText().toString());
                int fats = Integer.parseInt(fatEditText.getText().toString());
                int protein = Integer.parseInt(proteinEditText.getText().toString());

                FavouriteMeal favouriteMeal = new FavouriteMeal();
                favouriteMeal.createFavouriteMeal(email, mealName, calories, carbs, fats, protein,
                        new FavouriteMeal.Callback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getActivity(),
                                        "Favourite successfully added",
                                        Toast.LENGTH_SHORT).show();
                                favourited = true;
                                favouriteButton.setBackgroundColor(Color.parseColor("#CC0099"));
                                favouriteButton.setText("Remove From Favourites");
                                setupFavouriteMealsSpinner();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getActivity(),
                                        "Favourite not added",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else if (id == R.id.favouriteMealsSpinner) {
            setupFavouriteMealsSpinner();
        }
    }

    protected void setupFavouriteMealsSpinner() {
        FavouriteMeal favouriteMeal = new FavouriteMeal();
        favouriteMeal.getAllFavouriteMealsByEmail(email, new FavouriteMeal.CallbackWithType<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> result) {
                if (!result.isEmpty()) {
                    result.add(0, "");
                    // Create an ArrayAdapter using the string array and a default spinner layout
                    ArrayAdapter favouriteMealsAdapter = new ArrayAdapter(getActivity().getApplicationContext(),
                            android.R.layout.simple_spinner_item, result);
                    // Specify the layout to use when the list of choices appears
                    favouriteMealsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // Apply the adapter to the spinner.
                    favouriteMealsSpinner.setAdapter(favouriteMealsAdapter);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getActivity(),
                        "Error setting up spinner",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
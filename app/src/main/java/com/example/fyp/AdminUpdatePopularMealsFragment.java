package com.example.fyp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminUpdatePopularMealsFragment extends Fragment implements View.OnClickListener {

    View view;
    private TextView availablePopularMealsTextView;
    EditText popularMealNameEditText;
    EditText popularMealCaloriesEditText;
    EditText popularMealCarbsEditText;
    EditText popularMealFatsEditText;
    EditText popularMealProteinEditText;
    Button addPopularMealButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_admin_update_popular_meals, container, false);

        availablePopularMealsTextView = (TextView)view.findViewById(R.id.availablePopularMealsTextView);
        popularMealNameEditText = (EditText) view.findViewById(R.id.popularMealNameEditText);
        popularMealCaloriesEditText = (EditText)view.findViewById(R.id.popularMealCaloriesEditText);
        popularMealCarbsEditText = (EditText)view.findViewById(R.id.popularMealCarbsEditText);
        popularMealFatsEditText = (EditText)view.findViewById(R.id.popularMealFatsEditText);
        popularMealProteinEditText = (EditText)view.findViewById(R.id.popularMealProteinEditText);
        addPopularMealButton = (Button)view.findViewById(R.id.addPopularMealButton);

        addPopularMealButton.setOnClickListener(this);

        updateAvailablePopularMealsTextView();

        return view;
    }

    private void updateAvailablePopularMealsTextView() {
        availablePopularMealsTextView.setText("");

        SelectableMeal selectableMeal = new SelectableMeal();
        selectableMeal.getAllSelectableMeals(new SelectableMeal.CallbackWithType<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> result) {
                StringBuilder stringBuilder = new StringBuilder();
                for (Map<String, Object> mealData : result) {
                    // Assuming mealData contains fields like "name", "description", etc.
                    String mealName = mealData.get("mealName").toString();
                    int calories = Integer.parseInt(mealData.get("calories").toString());
                    int carbs = Integer.parseInt(mealData.get("carbs").toString());
                    int fats = Integer.parseInt(mealData.get("fats").toString());
                    int protein = Integer.parseInt(mealData.get("protein").toString());
                    // Append meal details to the StringBuilder
                    stringBuilder.append("Meal Name: ").append(mealName).append("\n")
                            .append("Calories: ").append(String.valueOf(calories)).append("\n")
                            .append("Carbs: ").append(String.valueOf(carbs)).append("\n")
                            .append("Fats: ").append(String.valueOf(fats)).append("\n")
                            .append("Protein: ").append(String.valueOf(protein)).append("\n\n");
                }
                // Set the concatenated string as the text of the TextView
                availablePopularMealsTextView.setText(stringBuilder.toString());
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.addPopularMealButton) {
            if (popularMealNameEditText.getText().toString().isEmpty() ||
                    popularMealCaloriesEditText.getText().toString().isEmpty() ||
                    popularMealCarbsEditText.getText().toString().isEmpty() ||
                    popularMealFatsEditText.getText().toString().isEmpty() ||
                    popularMealProteinEditText.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "All fields must be filled in", Toast.LENGTH_SHORT).show();
                return;
            }
            String mealName = popularMealNameEditText.getText().toString();
            int calories = Integer.parseInt(popularMealCaloriesEditText.getText().toString());
            int carbs = Integer.parseInt(popularMealCarbsEditText.getText().toString());
            int fats = Integer.parseInt(popularMealFatsEditText.getText().toString());
            int protein = Integer.parseInt(popularMealProteinEditText.getText().toString());

            SelectableMeal selectableMeal = new SelectableMeal();

            selectableMeal.checkIfSelectableMealExists(mealName, new SelectableMeal.CallbackWithType<Boolean>() {
                @Override
                public void onSuccess(Boolean selectableMealExists) {
                    if (selectableMealExists) {
                        Toast.makeText(getActivity(),
                                "The specified popular meal already exists",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> toAdd = new HashMap<>();
                        toAdd.put("mealName", mealName);
                        toAdd.put("calories", calories);
                        toAdd.put("carbs", carbs);
                        toAdd.put("fats", fats);
                        toAdd.put("protein", protein);
                        selectableMeal.addSelectableMeal(toAdd, new SelectableMeal.Callback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getActivity(),
                                        "Popular meal successfully added to Firebase",
                                        Toast.LENGTH_SHORT).show();
                                updateAvailablePopularMealsTextView();
                                popularMealNameEditText.setText("");
                                popularMealCaloriesEditText.setText("");
                                popularMealCarbsEditText.setText("");
                                popularMealFatsEditText.setText("");
                                popularMealProteinEditText.setText("");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getActivity(),
                                        "Popular meal not added to Firebase",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getActivity(),
                            "Error Occurred",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

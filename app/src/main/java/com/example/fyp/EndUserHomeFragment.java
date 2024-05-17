package com.example.fyp;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.example.fyp.CircularProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class EndUserHomeFragment extends Fragment {

    private Calorie calorie;
    private String email;
    private TextView goalTextView;
    private TextView currentGoalText;
    private Button setButton;
    private CircularProgressBar circularProgressBar;
    private Button exerciseButton;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_end_user_home, container, false);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        currentGoalText = view.findViewById(R.id.currentGoalText);
        goalTextView = view.findViewById(R.id.goalTextView);
        circularProgressBar = view.findViewById(R.id.circularProgressBar);
        setButton = view.findViewById(R.id.setButton);
        setButton.setOnClickListener(v -> showCalorieGoalPicker());
        exerciseButton = view.findViewById(R.id.exerciseButton);
        exerciseButton.setOnClickListener(v -> navigateToExerciseFragment());
        calorie = new Calorie();

        checkAndInitializeCalorieDocument();
        return view;
    }

    private void checkAndInitializeCalorieDocument() {
        calorie.checkCalorieForToday(email, new Calorie.CalorieCallback() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                Integer calorieGoal = data.containsKey("calorieGoal") ? getDataValue(data.get("calorieGoal")) : 0;
                Integer totalCalorie = data.containsKey("totalCalorie") ? getDataValue(data.get("totalCalorie")) : 0;
                currentGoalText.setText(String.valueOf(calorieGoal));
                String goalText = totalCalorie + " / " + calorieGoal;
                goalTextView.setText(goalText);
                circularProgressBar.setMaxProgress(calorieGoal);
                circularProgressBar.setProgress(totalCalorie);
            }

            private int getDataValue(Object value) {
                if (value instanceof Long) {
                    return ((Long) value).intValue();
                } else if (value instanceof Integer) {
                    return (Integer) value;
                }
                return 0;
            }


            @Override
            public void onFailure(Exception e) {
                Log.e("FirestoreError", "Error checking calorie document", e);
            }
        });

    }

    private void showCalorieGoalPicker() {
        NumberPicker numberPicker = new NumberPicker(requireContext());

        int minValue = 1200;
        int maxValue = 4000;
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue((maxValue - minValue) / 100);

        String[] displayValues = new String[maxValue - minValue + 1];
        for (int i = 0; i < displayValues.length; i++) {
            displayValues[i] = String.valueOf(minValue + i * 100);
        }
        numberPicker.setDisplayedValues(displayValues);
        numberPicker.setWrapSelectorWheel(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select Calorie Goal");
        builder.setView(numberPicker);
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            int selectedValuePosition = numberPicker.getValue();
            int selectedCalorieGoal = minValue + selectedValuePosition * 100;
            updateCalorieGoal(selectedCalorieGoal);
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private void updateCalorieGoal(int newGoal) {
        String currentDate = getCurrentDate();
        String userName = email;

        calorie.updateCalorieGoal(newGoal, currentDate, userName, new Calorie.UpdateCalorieCallback() {
            @Override
            public void onSuccess() {
                refreshUI();
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("FirestoreError", "Error updating calorie goal", e);
            }
        });
    }
    private void navigateToExerciseFragment() {
        Fragment exerciseFragment = new EndUserExerciseFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.endUserFragmentContainerView, exerciseFragment);
        transaction.addToBackStack(null); // Add to back stack to allow "back" navigation
        transaction.commit();
    }
    @Override
    public void onResume() {
        super.onResume();
        refreshUI();
    }
    private void refreshUI() {
        checkAndInitializeCalorieDocument();
    }
}


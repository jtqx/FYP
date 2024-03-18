package com.example.fyp;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.example.fyp.CircularProgressBar;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;

public class EndUserHomeFragment extends Fragment {

    private int mMaxValue = 0;
    private CircularProgressBar circularProgressBar;
    private TextView goalTextView;
    private TextView currentGoalText;
    private TextView noGoalText;
    private Button editButton;
    private CalorieDatabaseHelper dbHelper;
    private DatabaseHelper db;
    private String email;
    private int calorieCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_end_user_home, container, false);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        circularProgressBar = view.findViewById(R.id.circularProgressBar);
        Button setButton = view.findViewById(R.id.setButton);
        dbHelper = new CalorieDatabaseHelper(requireContext());
        currentGoalText = view.findViewById(R.id.currentGoalText);
        noGoalText = view.findViewById(R.id.noGoalText);
        editButton = view.findViewById(R.id.editButton);
        goalTextView = view.findViewById(R.id.goalTextView);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPickerDialog();
            }
        });

        Button editButton = view.findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });

        updateProgressViews();
        return view;
    }

    private void showNumberPickerDialog() {
        Log.d("Debug", "showNumberPickerDialog() called");
        final int minValue = 12;
        final int maxValue = 40;
        final int interval = 1;

        final String[] displayedValues = new String[maxValue - minValue + 1];
        for (int i = 0; i < displayedValues.length; i++) {
            displayedValues[i] = String.valueOf((minValue + i) * 100);
        }

        final NumberPicker numberPicker = new NumberPicker(requireContext());
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(displayedValues.length - 1);
        numberPicker.setDisplayedValues(displayedValues);
        numberPicker.setValue((mMaxValue / 100) - minValue);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mMaxValue = (Integer.parseInt(displayedValues[newVal]) * 100);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(numberPicker)
                .setTitle("Select Calorie Goal")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMaxValue = numberPicker.getValue();
                        int calorieGoal = (mMaxValue * 100) + 1200;
                        db = new DatabaseHelper(requireContext());
                        calorieCount = db.getTotalCaloriesForDay(email, getCurrentDate());
                        dbHelper.addCalorieGoal(email, calorieGoal, calorieCount);
                        db.close();
                        updateProgressViews();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showEditDialog() {
        int currentCalorieGoal = dbHelper.getCalorieGoalByAuthor(email);

        final NumberPicker numberPicker = new NumberPicker(requireContext());
        numberPicker.setMinValue(1200);
        numberPicker.setMaxValue(4000);
        numberPicker.setValue(currentCalorieGoal);
        numberPicker.setWrapSelectorWheel(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(numberPicker)
                .setTitle("Edit Calorie Goal")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int newCalorieGoal = numberPicker.getValue();
                        dbHelper.updateCalorieGoal(email, newCalorieGoal);
                        updateProgressViews();
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteEntry(email);
                        updateProgressViews();
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void updateProgressViews() {
        Log.d("Debug", "updateProgressViews() called");
        int calorieGoal = dbHelper.getCalorieGoalByAuthor(email);
        int currentCount = dbHelper.getCalorieCountByAuthor(email);

        if (calorieGoal > 0 && currentCount >= 0) {
            String message = currentCount + " / " + calorieGoal;
            goalTextView.setText(message);
            circularProgressBar.setMaxProgress(calorieGoal);
            circularProgressBar.setProgress(currentCount);
            currentGoalText.setVisibility(View.VISIBLE);
            currentGoalText.setText(" " + calorieGoal);
            editButton.setVisibility(View.VISIBLE);
            noGoalText.setVisibility(View.GONE);
        } else {
            goalTextView.setText("N/A");
            circularProgressBar.setMaxProgress(0);
            circularProgressBar.setProgress(0);
            currentGoalText.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
            noGoalText.setVisibility(View.VISIBLE);
        }
    }
    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
    }
    private void updateCalorieCount() {
        db = new DatabaseHelper(requireContext());
        int newCalorieCount = db.getTotalCaloriesForDay(email, getCurrentDate());
        dbHelper.updateCalorieCount(email, newCalorieCount);
        db.close();
        calorieCount = newCalorieCount;
        updateProgressViews();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
    @Override
    public void onResume() {
        super.onResume();
        updateCalorieCount();
        updateProgressViews();
    }
}

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

public class EndUserHomeFragment extends Fragment {

    private int mMaxValue = 0;
    private CircularProgressBar circularProgressBar;
    private TextView goalTextView;
    private CalorieDatabaseHelper dbHelper;
    private String email;

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

        goalTextView = view.findViewById(R.id.goalTextView);
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPickerDialog();
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
                .setTitle("Select Maximum Value")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMaxValue = numberPicker.getValue();
                        int calorieGoal = (mMaxValue * 100) + 1200;
                        dbHelper.addCalorieGoal(email, calorieGoal, 1200);
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

    private void updateProgressViews() {
        Log.d("Debug", "updateProgressViews() called");
        int calorieGoal = dbHelper.getCalorieGoalByAuthor(email);
        int currentCount = dbHelper.getCalorieCountByAuthor(email);

        if (calorieGoal > 0 && currentCount >= 0) {
            String message = currentCount + " / " + calorieGoal;
            goalTextView.setText(message);
            circularProgressBar.setMaxProgress(calorieGoal);
            circularProgressBar.setProgress(currentCount);
        } else {
            goalTextView.setText("N/A");
            circularProgressBar.setMaxProgress(0);
            circularProgressBar.setProgress(0);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }
}

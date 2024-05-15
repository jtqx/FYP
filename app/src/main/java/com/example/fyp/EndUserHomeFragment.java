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


    /*private int mMaxValue = 0;
    private CircularProgressBar circularProgressBar;
    private TextView goalTextView;
    private TextView currentGoalText;
    private TextView noGoalText;
    private Button editButton;
    private String email;
    private int calorieCount;
    private Calorie calorieManager;
    private String[] displayedValues;
    private boolean calorieGoalSet = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_end_user_home, container, false);

        // Find views by their IDs
        Button setButton = view.findViewById(R.id.setButton);
        currentGoalText = view.findViewById(R.id.currentGoalText); // Move the initialization here
        noGoalText = view.findViewById(R.id.noGoalText);
        goalTextView = view.findViewById(R.id.goalTextView);
        circularProgressBar = view.findViewById(R.id.circularProgressBar);
        // Set OnClickListener for the editButton
        editButton = view.findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditNumberPickerDialog(Integer.parseInt(currentGoalText.getText().toString()));
            }
        });

        // Set click listener for the setButton
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
        // Create a NumberPicker with the desired range and increments
        NumberPicker numberPicker = new NumberPicker(requireContext());
        numberPicker.setMinValue(12); // Minimum calorie goal (1200)
        numberPicker.setMaxValue(40); // Maximum calorie goal (4000)
        numberPicker.setValue(30); // Default value (3000)
        numberPicker.setWrapSelectorWheel(false); // Disable wrapping

        // Show the NumberPicker in an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Set Calorie Goal")
                .setView(numberPicker)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the selected calorie goal from the NumberPicker
                        int calorieGoal = numberPicker.getValue() * 100; // Convert to actual calorie value
                        String name = "Your Name"; // Replace with actual name

                        // Call the method to set the calorie goal in the Calorie class
                        calorieManager = new Calorie(); // Create an instance of the Calorie class
                        calorieManager.setCalorieGoal(calorieGoal, name, new Calorie.UserCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                                calorieGoalSet = true;
                                noGoalText.setVisibility(View.GONE);
                                currentGoalText.setVisibility(View.VISIBLE);
                                editButton.setVisibility(View.VISIBLE);
                                // Update the UI with the new calorie goal
                                currentGoalText.setText(String.valueOf(calorieGoal));
                                goalTextView.setText("0 / " + calorieGoal);

                            }

                            @Override
                            public void onFailure(Exception e) {
                                // Handle failure to set calorie goal
                                Log.e("Calorie Goal", "Failed to set calorie goal: " + e.getMessage());
                                // You can show an error message or handle the failure in other ways
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                })
                .show();
    }
    // Method to show the numberPicker dialog for editing the calorie goal
    private void showEditNumberPickerDialog(int currentCalorieGoal) {
        // Create a NumberPicker with the desired range and increments
        NumberPicker numberPicker = new NumberPicker(requireContext());
        numberPicker.setMinValue(12); // Minimum calorie goal (1200)
        numberPicker.setMaxValue(40); // Maximum calorie goal (4000)
        numberPicker.setValue(currentCalorieGoal / 100); // Set default value (convert to index)
        numberPicker.setWrapSelectorWheel(false); // Disable wrapping

        // Show the NumberPicker in an AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Calorie Goal")
                .setView(numberPicker)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get the selected calorie goal from the NumberPicker
                        int newCalorieGoal = numberPicker.getValue() * 100; // Convert to actual calorie value

                        // Update the UI with the new calorie goal
                        currentGoalText.setText(String.valueOf(newCalorieGoal));
                        goalTextView.setText("0 / " + newCalorieGoal);
                        calorieManager.setCalorieGoal(newCalorieGoal, email, new Calorie.UserCallback<Void>() {
                            @Override
                            public void onSuccess(Void result) {
                            }
                            @Override
                            public void onFailure(Exception e) {
                                // Handle failure to set calorie goal
                                Log.e("Calorie Goal", "Failed to update calorie goal: " + e.getMessage());
                                // You can show an error message or handle the failure in other ways
                            }
                        });

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                })
                .show();
        }
    private boolean calorieGoalIsSet() {
        // Check if currentGoalText has a valid integer value
        try {
            int currentGoal = Integer.parseInt(currentGoalText.getText().toString());
            return currentGoal > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }






    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_end_user_home, container, false);

        // Initialize UI elements
        Button setButton = view.findViewById(R.id.setButton);
        editButton = view.findViewById(R.id.editButton);
        currentGoalText = view.findViewById(R.id.currentGoalText);
        goalTextView = view.findViewById(R.id.goalTextView);
        circularProgressBar = view.findViewById(R.id.circularProgressBar);
        noGoalText = view.findViewById(R.id.noGoalText);
        calorieManager = new Calorie();
        // Set click listeners
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show number picker dialog to set calorie goal
                showNumberPickerDialog();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show number picker dialog with current calorie goal for editing
                showNumberPickerDialog();
            }
        });

        // Update progress views
        updateProgressViews();

        return view;
    }
    private void showNumberPickerDialog() {
        final int minValue = 12;
        final int maxValue = 40;
        final int interval = 1;

        // Calculate the displayed values for the number picker
        final String[] displayedValues = new String[maxValue - minValue + 1];
        for (int i = 0; i < displayedValues.length; i++) {
            displayedValues[i] = String.valueOf((minValue + i) * 100);
        }

        // Create and configure the NumberPicker
        final NumberPicker numberPicker = new NumberPicker(requireContext());
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(displayedValues.length - 1);
        numberPicker.setDisplayedValues(displayedValues);
        numberPicker.setWrapSelectorWheel(false);

        // Create and show the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(numberPicker)
                .setTitle("Set Calorie Goal")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Retrieve the selected value from the number picker
                        int selectedValueIndex = numberPicker.getValue();
                        int calorieGoal = Integer.parseInt(displayedValues[selectedValueIndex]);

                        // Update the calorie goal TextViews
                        currentGoalText.setText(String.valueOf(calorieGoal));
                        goalTextView.setText("0/" + calorieGoal);

                        // Set calorie goal using the Calorie class
                        calorieManager.setCalorieGoal(calorieGoal, email);

                        // Update progress views
                        updateProgressViews();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                        dialog.dismiss();
                    }
                })
                .show();
    }
    private void showEditDialog() {
        // Retrieve the current calorie goal using the Calorie class
        calorieManager.getCalorieGoal(email, new Calorie.UserCallback<Integer>() {
            @Override
            public void onSuccess(Integer currentCalorieGoal) {
                // Create a NumberPicker with the current calorie goal pre-selected
                final NumberPicker numberPicker = new NumberPicker(requireContext());
                numberPicker.setMinValue(12);
                numberPicker.setMaxValue(40); // Adjust the max value based on your requirement
                numberPicker.setValue(currentCalorieGoal / 100); // Convert to index
                numberPicker.setDisplayedValues(getDisplayedValues(12,40)); // You need to implement this method

                // Create and configure the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setView(numberPicker)
                        .setTitle("Edit Calorie Goal")
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Retrieve the new calorie goal from the NumberPicker
                                int newCalorieGoalIndex = numberPicker.getValue();
                                int newCalorieGoal = Integer.parseInt(displayedValues[newCalorieGoalIndex]) * 100;

                                // Update the calorie goal TextViews
                                currentGoalText.setText(String.valueOf(newCalorieGoal));
                                goalTextView.setText("0/" + newCalorieGoal);

                                // Set the new calorie goal using the Calorie class
                                calorieManager.setCalorieGoalForDate(getCurrentDate(), newCalorieGoal, email, new Calorie.UserCallback<Void>() {
                                    @Override
                                    public void onSuccess(Void result) {
                                        // Update progress views after setting the new calorie goal
                                        updateProgressViews();
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        // Handle failure to set the new calorie goal
                                        Log.e("Calorie Goal", "Failed to update calorie goal: " + e.getMessage());
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Dismiss the dialog
                                dialog.dismiss();
                            }
                        })
                        .show();
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure to retrieve the current calorie goal
                Log.e("Calorie Goal", "Failed to retrieve current calorie goal: " + e.getMessage());
            }
        });
    }
    private void updateProgressViews() {
        String currentDate = getCurrentDate();
        calorieManager = new Calorie();
        calorieManager.getCalorieGoal(email, currentDate, new Calorie.UserCallback<Integer>() {
            @Override
            public void onSuccess(Integer calorieGoal) {
                // Calculate total calories after getting the calorie goal
                calorieManager.calculateTotalCalories(currentDate, email); // Assuming email is the currentName

                calorieManager.getCalorieCount(email, currentDate ,new Calorie.UserCallback<Integer>() {
                    @Override
                    public void onSuccess(Integer calorieCount) {
                        // Update the progress views based on the retrieved values
                        if (calorieGoal > 0 && calorieCount >= 0) {
                            String message = calorieCount + " / " + calorieGoal;
                            goalTextView.setText(message);
                            circularProgressBar.setMaxProgress(calorieGoal);
                            circularProgressBar.setProgress(calorieCount);
                            currentGoalText.setVisibility(View.VISIBLE);
                            currentGoalText.setText(String.valueOf(calorieGoal));
                            editButton.setVisibility(View.VISIBLE);
                            noGoalText.setVisibility(View.GONE);
                        } else {
                            // Handle the case where either calorieGoal or calorieCount is invalid
                            goalTextView.setText("N/A");
                            circularProgressBar.setMaxProgress(0);
                            circularProgressBar.setProgress(0);
                            currentGoalText.setVisibility(View.GONE);
                            editButton.setVisibility(View.GONE);
                            noGoalText.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure to retrieve calorie count
                        Log.e("Debug", "Failed to get calorie count: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure to retrieve calorie goal
                Log.e("Debug", "Failed to get calorie goal: " + e.getMessage());
            }
        });
    }

    private String[] getDisplayedValues(int minValue, int maxValue) {
        int size = maxValue - minValue + 1;
        String[] displayedValues = new String[size];
        for (int i = 0; i < size; i++) {
            displayedValues[i] = String.valueOf(minValue + i);
        }
        return displayedValues;
    }


    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateProgressViews();
    }*/


package com.example.fyp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EndUserExerciseDetailsFragment extends Fragment {

    private TextView exerciseNameTextView;
    private TextView exerciseDurationTextView;
    private TextView exerciseCaloriesBurntTextView;
    private TextView exerciseHowToTextView;
    private int caloriesBurnt = 0;
    private Button doneButton;
    private String email;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_end_user_exercise_details, container, false);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        exerciseNameTextView = view.findViewById(R.id.exerciseNameTextView);
        exerciseDurationTextView = view.findViewById(R.id.exerciseDurationTextView);
        exerciseCaloriesBurntTextView = view.findViewById(R.id.exerciseCaloriesBurntTextView);
        exerciseHowToTextView = view.findViewById(R.id.exerciseHowToTextView);
        doneButton = view.findViewById(R.id.doneButton);
        Bundle args = getArguments();
        if (args != null) {
            exerciseNameTextView.setText(args.getString("name"));
            exerciseDurationTextView.setText("Duration: " + args.getString("time") + " mins");
            exerciseCaloriesBurntTextView.setText("Calories Burnt: " + args.getInt("caloriesBurnt"));
            exerciseHowToTextView.setText("How To: " + args.getString("howTo"));
            caloriesBurnt = args.getInt("caloriesBurnt");
        }

        doneButton.setOnClickListener(v -> {
            String date = getCurrentDate();
            Exercise.updateTotalCalories(date, email, caloriesBurnt);
            getParentFragmentManager().popBackStack();
        });

        return view;
    }
    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}

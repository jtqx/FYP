package com.example.fyp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class FirstRegistrationFragment extends Fragment implements View.OnClickListener {

    View view;
    ProgressBar registrationProgressBar;
    MaterialButton maleGenderButton;
    MaterialButton femaleGenderButton;
    EditText ageEditText;
    EditText firstNameEditText;
    EditText lastNameEditText;
    SecondRegistrationFragment secondRegistrationFragment;
    Button button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_first_registration, container, false);

        registrationProgressBar = (ProgressBar)view.findViewById(R.id.registrationProgressBar);
        registrationProgressBar.setProgress(25, true);

        maleGenderButton = (MaterialButton)view.findViewById(R.id.maleGenderButton);
        maleGenderButton.setOnClickListener(this);
        maleGenderButton.setChecked(true);

        femaleGenderButton = (MaterialButton)view.findViewById(R.id.femaleGenderButton);
        femaleGenderButton.setOnClickListener(this);

        ageEditText = (EditText)view.findViewById(R.id.ageEditText);

        firstNameEditText = (EditText)view.findViewById(R.id.firstNameEditText1);

        lastNameEditText = (EditText)view.findViewById(R.id.lastNameEditText1);

        button = (Button)view.findViewById(R.id.nextOnboardingFragButton);
        button.setOnClickListener(this);

        secondRegistrationFragment = new SecondRegistrationFragment();

        ConstraintLayout onBoardingConstraintLayout = (ConstraintLayout)
                view.findViewById(R.id.registrationConstraintLayout);

        String[] list = {"One", "Two", "Three", "Four", "Five"};

        ArrayList<Integer>materialButtonId = new ArrayList<Integer>();

        /*for (int i = 0; i < list.length; i++) {
            MaterialButton materialButton = new MaterialButton(view.getContext(), null,
                    com.google.android.material.R.attr.materialButtonOutlinedStyle);

            materialButton.setId(View.generateViewId());
            materialButtonId.add(materialButton.getId());

            materialButton.setCheckable(true);
            materialButton.setText(list[i]);
            materialButton.setStrokeColor(ColorStateList.valueOf(Color.parseColor(
                    "#000000")));
            materialButton.setStrokeWidth(5);
            materialButton.setCornerRadius(20);

            materialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (materialButton.isChecked()) {
                        materialButton.setStrokeColor(ColorStateList.valueOf(Color.parseColor(
                                "#9C27B0")));
                    } else {
                        materialButton.setStrokeColor(ColorStateList.valueOf(Color.parseColor(
                                "#000000")));
                    }
                }
            });

            // Add the button to the constraint layout
            onBoardingConstraintLayout.addView(materialButton);

            // Set constraints programmatically
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(onBoardingConstraintLayout);
            // Set constraints for the MaterialButton
            if (i == 0) {
                constraintSet.connect(materialButton.getId(), ConstraintSet.TOP,
                        R.id.testProgressBar, ConstraintSet.BOTTOM, 300);
            } else {
                constraintSet.connect(materialButton.getId(), ConstraintSet.TOP,
                        materialButtonId.get(i - 1), ConstraintSet.BOTTOM, 10);
            }
            constraintSet.connect(materialButton.getId(), ConstraintSet.START,
                    R.id.onboardingConstraintLayout, ConstraintSet.START, 0);
            constraintSet.connect(materialButton.getId(), ConstraintSet.END,
                    R.id.onboardingConstraintLayout, ConstraintSet.END, 0);
            constraintSet.applyTo(onBoardingConstraintLayout);
        }*/

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.nextOnboardingFragButton) {
            if (String.valueOf(ageEditText.getText()).isEmpty() ||
                    String.valueOf(firstNameEditText.getText()).isEmpty() ||
                    String.valueOf(lastNameEditText.getText()).isEmpty()) {
                Toast toast = Toast.makeText(getActivity(), "All field must be filled in",
                        Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            int age = Integer.parseInt(String.valueOf(ageEditText.getText()));

            String gender = "Male";
            if (femaleGenderButton.isChecked()) {
                gender = "Female";
            }

            String firstName = String.valueOf(firstNameEditText.getText());
            String lastName = String.valueOf(lastNameEditText.getText());

            SharedPreferences sharedPreferences = getActivity()
                    .getSharedPreferences("SharedPref",
                            Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("age", age);
            editor.putString("gender", gender);
            editor.putString("firstName", firstName);
            editor.putString("lastName", lastName);
            editor.apply();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.testFragmentContainerView, secondRegistrationFragment)
                    .commit();
        } else if (id == R.id.maleGenderButton) {
            if (femaleGenderButton.isChecked()) {
                femaleGenderButton.setChecked(false);
            }
        } else if (id == R.id.femaleGenderButton) {
            if (maleGenderButton.isChecked()) {
                maleGenderButton.setChecked(false);
            }
        }
    }
}
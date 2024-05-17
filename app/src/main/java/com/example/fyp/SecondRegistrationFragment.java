package com.example.fyp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class SecondRegistrationFragment extends Fragment implements View.OnClickListener {

    View view;
    ProgressBar progressBar;
    int age;
    String gender;
    String firstName;
    String lastName;
    String selectedDiet;
    Button nextRegistrationFragButton;
    ThirdRegistrationFragment thirdRegistrationFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_second_registration, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            age = bundle.getInt("age");
            Log.i("From 2nd Fragment", String.valueOf(age));
            gender = bundle.getString("gender");
            Log.i("From 2nd Fragment", gender);
            firstName = bundle.getString("firstName");
            Log.i("From 2nd Fragment", firstName);
            lastName = bundle.getString("lastName");
            Log.i("From 2nd Fragment", lastName);
        }

        progressBar = (ProgressBar)view.findViewById(R.id.registrationProgressBar2);
        progressBar.setProgress(40, true);

        nextRegistrationFragButton = (Button)view.findViewById(R.id.nextRegistrationFragButton);
        nextRegistrationFragButton.setOnClickListener(this);

        selectedDiet = "";

        thirdRegistrationFragment = new ThirdRegistrationFragment();

        ConstraintLayout onBoardingConstraintLayout = (ConstraintLayout)
                view.findViewById(R.id.registrationConstraintLayout2);

        ArrayList<String> list = new ArrayList<String>();

        ArrayList<Integer> materialButtonId = new ArrayList<Integer>();

        Diet diet = new Diet();

        diet.getAllDiets(new Diet.CallbackWithType<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> result) {
                list.addAll(result);

                for (int i = 0; i < list.size(); i++) {
                    MaterialButton materialButton = new MaterialButton(view.getContext(), null,
                            com.google.android.material.R.attr.materialButtonOutlinedStyle);

                    materialButton.setId(View.generateViewId());
                    materialButtonId.add(materialButton.getId());

                    materialButton.setCheckable(true);
                    materialButton.setText(list.get(i));
                    materialButton.setStrokeColor(ColorStateList.valueOf(Color.parseColor(
                            "#000000")));
                    materialButton.setStrokeWidth(5);
                    materialButton.setCornerRadius(20);
                    materialButton.setWidth(900);

                    materialButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = materialButton.getId();
                            for (int Id : materialButtonId) {
                                MaterialButton button = (MaterialButton)view.findViewById(Id);
                                if (Id == id) {
                                    button.setChecked(true);
                                    button.setStrokeColor(ColorStateList.valueOf(Color.parseColor(
                                            "#9C27B0")));
                                    selectedDiet = button.getText().toString();
                                } else {
                                    button.setChecked(false);
                                    button.setStrokeColor(ColorStateList.valueOf(Color.parseColor(
                                            "#000000")));
                                }
                            }
                        }
                    });

                    onBoardingConstraintLayout.addView(materialButton);

                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(onBoardingConstraintLayout);
                    if (i == 0) {
                        constraintSet.connect(materialButton.getId(), ConstraintSet.TOP,
                                R.id.textView55, ConstraintSet.BOTTOM, 60);
                    } else {
                        constraintSet.connect(materialButton.getId(), ConstraintSet.TOP,
                                materialButtonId.get(i - 1), ConstraintSet.BOTTOM, 10);
                    }
                    constraintSet.connect(materialButton.getId(), ConstraintSet.START,
                            R.id.registrationConstraintLayout2, ConstraintSet.START, 0);
                    constraintSet.connect(materialButton.getId(), ConstraintSet.END,
                            R.id.registrationConstraintLayout2, ConstraintSet.END, 0);
                    constraintSet.applyTo(onBoardingConstraintLayout);
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.nextRegistrationFragButton) {
            if (selectedDiet.isEmpty()) {
                Toast toast = Toast.makeText(getActivity(), "A diet must be selected",
                        Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            Bundle bundle = new Bundle();
            bundle.putInt("age", age);
            bundle.putString("gender", gender);
            bundle.putString("firstName", firstName);
            bundle.putString("lastName", lastName);
            bundle.putString("diet", selectedDiet);
            thirdRegistrationFragment.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.testFragmentContainerView, thirdRegistrationFragment)
                    .commit();
        }
    }
}
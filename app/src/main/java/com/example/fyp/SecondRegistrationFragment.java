package com.example.fyp;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class SecondRegistrationFragment extends Fragment {

    View view;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_second_registration, container, false);

        progressBar = (ProgressBar)view.findViewById(R.id.registrationProgressBar2);
        progressBar.setProgress(50, true);

        ConstraintLayout onBoardingConstraintLayout = (ConstraintLayout)
                view.findViewById(R.id.registrationConstraintLayout2);

        String[] list = {"One", "Two", "Three", "Four", "Five"};

        ArrayList<Integer> materialButtonId = new ArrayList<Integer>();

        for (int i = 0; i < list.length; i++) {
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
                        } else {
                            button.setChecked(false);
                            button.setStrokeColor(ColorStateList.valueOf(Color.parseColor(
                                    "#000000")));
                        }
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

        return view;
    }
}
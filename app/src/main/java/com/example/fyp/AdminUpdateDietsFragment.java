package com.example.fyp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class AdminUpdateDietsFragment extends Fragment implements View.OnClickListener {

    View view;
    private TextView availableDietsTextView;
    EditText addDietEditText;
    EditText deleteDietEditText;
    Button addDietButton;
    Button deleteDietButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_admin_update_diets, container, false);
        availableDietsTextView = (TextView)view.findViewById(R.id.availableDietsTextView);

        addDietEditText = (EditText)view.findViewById(R.id.addDietEditText);
        deleteDietEditText = (EditText)view.findViewById(R.id.deleteDietEditText);
        addDietButton = (Button)view.findViewById(R.id.addDietButton);
        deleteDietButton = (Button)view.findViewById(R.id.deleteDietButton);

        addDietButton.setOnClickListener(this);
        deleteDietButton.setOnClickListener(this);

        updateAvailableDietsTextView();

        return view;
    }

    private void updateAvailableDietsTextView() {
        ArrayList<String> dietList = new ArrayList<String>();

        Diet diet = new Diet();
        diet.getAllDiets(new Diet.CallbackWithType<ArrayList<String>>() {
            @Override
            public void onSuccess(ArrayList<String> result) {
                dietList.addAll(result);
                availableDietsTextView.setText("");
                for (String diet : dietList) {
                    availableDietsTextView.setText(availableDietsTextView.getText() + diet +
                            "\n");
                }
            }

            @Override
            public void onFailure(Exception e) {
                // Handle the error here
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.addDietButton) {
            String newDiet = addDietEditText.getText().toString();

            Diet diet = new Diet();
            diet.checkIfDietExists(newDiet, new Diet.CallbackWithType<Boolean>() {
                @Override
                public void onSuccess(Boolean dietExists) {
                    if (dietExists) {
                        Toast.makeText(getActivity(),
                                "The specified diet already exists",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        diet.createDiet(newDiet, new Diet.Callback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getActivity(),
                                        "Diet successfully added to Firebase",
                                        Toast.LENGTH_SHORT).show();
                                updateAvailableDietsTextView();
                                addDietEditText.setText("");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getActivity(),
                                        "Diet not added to Firebase",
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

        } else if (id == R.id.deleteDietButton) {
            String dietToDelete = deleteDietEditText.getText().toString();

            Diet diet = new Diet();
            diet.checkIfDietExists(dietToDelete, new Diet.CallbackWithType<Boolean>() {
                @Override
                public void onSuccess(Boolean dietExists) {
                    if (!dietExists) {
                        Toast.makeText(getActivity(),
                                "The specified diet does not exist",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        diet.deleteDiet(dietToDelete, new Diet.Callback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getActivity(),
                                        "Diet successfully deleted from Firebase",
                                        Toast.LENGTH_SHORT).show();
                                updateAvailableDietsTextView();
                                deleteDietEditText.setText("");
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getActivity(),
                                        "Diet not deleted from Firebase",
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
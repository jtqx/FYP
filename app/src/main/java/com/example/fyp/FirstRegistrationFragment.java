package com.example.fyp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

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
        view = inflater.inflate(R.layout.fragment_first_registration, container, false);

        registrationProgressBar = (ProgressBar)view.findViewById(R.id.registrationProgressBar);
        registrationProgressBar.setProgress(20, true);

        maleGenderButton = (MaterialButton)view.findViewById(R.id.maleGenderButton);
        maleGenderButton.setOnClickListener(this);
        maleGenderButton.setChecked(true);

        femaleGenderButton = (MaterialButton)view.findViewById(R.id.femaleGenderButton);
        femaleGenderButton.setOnClickListener(this);

        ageEditText = (EditText)view.findViewById(R.id.ageEditText);

        firstNameEditText = (EditText)view.findViewById(R.id.firstNameEditText1);

        lastNameEditText = (EditText)view.findViewById(R.id.lastNameEditText1);

        button = (Button)view.findViewById(R.id.nextRegistrationFragButton);
        button.setOnClickListener(this);

        secondRegistrationFragment = new SecondRegistrationFragment();

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.nextRegistrationFragButton) {
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

            Bundle bundle = new Bundle();
            bundle.putInt("age", age);
            bundle.putString("gender", gender);
            bundle.putString("firstName", firstName);
            bundle.putString("lastName", lastName);
            secondRegistrationFragment.setArguments(bundle);

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
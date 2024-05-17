package com.example.fyp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.DecimalFormat;

public class ThirdRegistrationFragment extends Fragment implements View.OnClickListener {

    View view;
    ProgressBar progressBar;
    int age;
    String gender;
    String firstName;
    String lastName;
    String diet;
    EditText indicateHeightEditText;
    EditText indicateWeightEditText;
    Button nextRegistrationFragButton;
    FourthRegistrationFragment fourthRegistrationFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_third_registration, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            age = bundle.getInt("age");
            Log.i("From 3rd Fragment", String.valueOf(age));
            gender = bundle.getString("gender");
            Log.i("From 3rd Fragment", gender);
            firstName = bundle.getString("firstName");
            Log.i("From 3rd Fragment", firstName);
            lastName = bundle.getString("lastName");
            Log.i("From 3rd Fragment", lastName);
            diet = bundle.getString("diet");
            Log.i("From 3rd Fragment", diet);
        }

        progressBar = (ProgressBar)view.findViewById(R.id.registrationProgressBar3);
        progressBar.setProgress(60, true);

        indicateHeightEditText = (EditText)view.findViewById(R.id.indicateHeightEditText);
        indicateWeightEditText = (EditText)view.findViewById(R.id.indicateWeightEditText);

        nextRegistrationFragButton = (Button)view.findViewById(R.id.nextRegistrationFragButton);
        nextRegistrationFragButton.setOnClickListener(this);

        fourthRegistrationFragment = new FourthRegistrationFragment();

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.nextRegistrationFragButton) {
            if (String.valueOf(indicateHeightEditText.getText()).isEmpty() ||
                    String.valueOf(indicateWeightEditText.getText()).isEmpty()) {
                Toast toast = Toast.makeText(getActivity(), "All field must be filled in",
                        Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            int height = Integer.parseInt(String.valueOf(indicateHeightEditText.getText()));
            int weight = Integer.parseInt(String.valueOf(indicateWeightEditText.getText()));
            double bmi = (double) (100 * 100 * weight) / (height * height);
            DecimalFormat df = new DecimalFormat("#.#");
            String formattedBmi = df.format(bmi);
            double roundedBmi = Double.parseDouble(formattedBmi);
            Log.i("info", "Rounded BMI = " + roundedBmi);

            Bundle bundle = new Bundle();
            bundle.putInt("age", age);
            bundle.putString("gender", gender);
            bundle.putString("firstName", firstName);
            bundle.putString("lastName", lastName);
            bundle.putString("diet", diet);
            bundle.putInt("height", height);
            bundle.putInt("weight", weight);
            bundle.putDouble("bmi", roundedBmi);
            fourthRegistrationFragment.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.testFragmentContainerView, fourthRegistrationFragment)
                    .commit();
        }
    }
}
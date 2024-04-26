package com.example.fyp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;

public class EndUserEditBodyProfileFragment extends Fragment implements View.OnClickListener {

    View view;
    String email;
    EditText heightEditText;
    EditText weightEditText;
    Button confirmBodyProfileChangesButton;
    EndUserBodyProfileFragment endUserBodyProfileFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_end_user_edit_body_profile, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SharedPref",
                Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

        heightEditText = (EditText)view.findViewById(R.id.heightEditText);
        weightEditText = (EditText)view.findViewById(R.id.weightEditText);
        confirmBodyProfileChangesButton = (Button)
                view.findViewById(R.id.confirmBodyProfileChangesButton);
        endUserBodyProfileFragment = new EndUserBodyProfileFragment();

        confirmBodyProfileChangesButton.setOnClickListener(this);

        /*DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        Cursor c = dbHelper.getBodyProfile(email);

        if (!c.isClosed()) {
            c.moveToFirst();
            heightEditText.setText(c.getString(1));
            weightEditText.setText(c.getString(2));
        }*/

        User user = new User();
        user.getUser(email, new User.UserCallbackWithType<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                heightEditText.setText(result.get("height").toString());
                weightEditText.setText(result.get("weight").toString());
            }

            @Override
            public void onFailure(Exception e) {
                Toast toast = Toast.makeText(getActivity(), "Error",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        int height = Integer.parseInt(heightEditText.getText().toString());
        int weight = Integer.parseInt(weightEditText.getText().toString());
        double bmi = (double) (100 * 100 * weight) / (height * height);
        DecimalFormat df = new DecimalFormat("#.#");
        String formattedBmi = df.format(bmi);
        double roundedBmi = Double.parseDouble(formattedBmi);

        Log.i("info", "Rounded BMI = " + roundedBmi);

        /*DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        boolean success = dbHelper.createBodyProfile(height, weight, bmi, email);
        if (success) {
            Toast.makeText(getActivity(), "Body profile successfully added or updated",
                    Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.endUserFragmentContainerView, endUserBodyProfileFragment)
                    .commit();
        } else {
            Toast.makeText(getActivity(), "Unsuccessful",
                    Toast.LENGTH_SHORT).show();
        }*/

        User user = new User();
        user.updateUserBodyProfile(email, height, weight, roundedBmi, new User.UserCallback() {
            @Override
            public void onSuccess() {
                Toast toast = Toast.makeText(getActivity(), "Changes Saved",
                        Toast.LENGTH_SHORT);
                toast.show();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.endUserFragmentContainerView, endUserBodyProfileFragment)
                        .commit();
            }

            @Override
            public void onFailure(Exception e) {
                Toast toast = Toast.makeText(getActivity(), "Changes Not Saved",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
package com.example.fyp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class EndUserBodyProfileFragment extends Fragment implements View.OnClickListener {

    View view;
    String email;
    TextView heightTextView;
    TextView weightTextView;
    TextView bmiTextView;
    Button editBodyProfileButton;
    EndUserEditBodyProfileFragment endUserEditBodyProfileFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_end_user_body_profile, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SharedPref",
                Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

        heightTextView = (TextView)view.findViewById(R.id.heightTextView);
        weightTextView = (TextView)view.findViewById(R.id.weightTextView);
        bmiTextView = (TextView)view.findViewById(R.id.bmiTextView);
        editBodyProfileButton = (Button)view.findViewById(R.id.editBodyProfileButton);
        endUserEditBodyProfileFragment = new EndUserEditBodyProfileFragment();

        editBodyProfileButton.setOnClickListener(this);

        User user = new User();
        user.getUser(email, new User.UserCallbackWithType<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                heightTextView.setText(result.get("height").toString());
                weightTextView.setText(result.get("weight").toString());
                bmiTextView.setText(result.get("bmi").toString());
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
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.endUserFragmentContainerView, endUserEditBodyProfileFragment)
                .commit();
    }
}
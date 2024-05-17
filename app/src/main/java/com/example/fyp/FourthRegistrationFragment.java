package com.example.fyp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class FourthRegistrationFragment extends Fragment implements View.OnClickListener {

    View view;
    ProgressBar progressBar;
    int age;
    String gender;
    String firstName;
    String lastName;
    String diet;
    int height;
    int weight;
    double bmi;
    String email;
    String password;
    EditText indicateEmailEditText;
    EditText indicatePasswordEditText;
    Button registerAccountButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fourth_registration, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            age = bundle.getInt("age");
            Log.i("From 4th Fragment", String.valueOf(age));
            gender = bundle.getString("gender");
            Log.i("From 4th Fragment", gender);
            firstName = bundle.getString("firstName");
            Log.i("From 4th Fragment", firstName);
            lastName = bundle.getString("lastName");
            Log.i("From 4th Fragment", lastName);
            diet = bundle.getString("diet");
            Log.i("From 4th Fragment", diet);
            height = bundle.getInt("height");
            Log.i("From 4th Fragment", String.valueOf(height));
            weight = bundle.getInt("height");
            Log.i("From 4th Fragment", String.valueOf(weight));
            bmi = bundle.getDouble("bmi");
            Log.i("From 4th Fragment", String.valueOf(bmi));
        }

        progressBar = (ProgressBar)view.findViewById(R.id.registrationProgressBar4);
        progressBar.setProgress(80, true);

        indicateEmailEditText = (EditText)view.findViewById(R.id.indicateEmailEditText);
        indicatePasswordEditText = (EditText)view.findViewById(R.id.indicatePasswordEditText);

        registerAccountButton = (Button)view.findViewById(R.id.registerAccountButton);
        registerAccountButton.setOnClickListener(this);

        String privacyPolicyMessage = getResources().getString(R.string.lorem_ipsum);
        SpannableString agree = new SpannableString(getResources().getString(R.string.join_agree));
        ClickableSpan privacyPolicySpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage(privacyPolicyMessage);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        };
        int green = ContextCompat.getColor(requireContext(), R.color.purple);
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(green);
        agree.setSpan(privacyPolicySpan, 87, 101, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        agree.setSpan(colorSpan, 87, 101, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView agreeTextView = (TextView)view.findViewById(R.id.agreeTextView);
        agreeTextView.setText(agree);
        agreeTextView.setMovementMethod(LinkMovementMethod.getInstance());
        agreeTextView.setHighlightColor(Color.TRANSPARENT);

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.registerAccountButton) {
            if (String.valueOf(indicateEmailEditText.getText()).isEmpty() ||
                    String.valueOf(indicatePasswordEditText.getText()).isEmpty()) {
                Toast toast = Toast.makeText(getActivity(), "All field must be filled in",
                        Toast.LENGTH_SHORT);
                toast.show();
                return;
            }

            email = indicateEmailEditText.getText().toString();
            password = indicatePasswordEditText.getText().toString();

            Map<String, Object> registerUser = new HashMap<>();
            registerUser.put("age", age);
            registerUser.put("gender", gender);
            registerUser.put("firstName", firstName);
            registerUser.put("lastName", lastName);
            registerUser.put("diet", diet);
            registerUser.put("height", height);
            registerUser.put("weight", weight);
            registerUser.put("bmi", bmi);
            registerUser.put("email", email);
            registerUser.put("password", password);

            User user = new User();
            user.checkIfUserExists(email, new User.UserCallbackWithType<Boolean>() {
                @Override
                public void onSuccess(Boolean userExists) {
                    if (userExists) {
                        Toast.makeText(getActivity(),
                                "A user account with this email address already exists!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        user.createUser(registerUser, new User.UserCallback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(getActivity(),
                                        "User successfully added to Firebase",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), JoinSuccessActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                Toast.makeText(getActivity(),
                                        "User not added to Firebase",
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
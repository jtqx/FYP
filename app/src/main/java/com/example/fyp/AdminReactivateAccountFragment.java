package com.example.fyp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

public class AdminReactivateAccountFragment extends Fragment implements View.OnClickListener {

    View view;
    TextView deactivatedAccountsTextView;
    EditText reactivateAccountEditText;
    Button reactivateButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_admin_reactivate_account, container, false);

        deactivatedAccountsTextView = (TextView)view.findViewById(R.id.deactivatedAccountsTextView);
        reactivateAccountEditText = (EditText)view.findViewById(R.id.reactivateAccountEditText);
        reactivateButton = (Button)view.findViewById(R.id.reactivateButton);
        reactivateButton.setOnClickListener(this);

        updateDeactivatedAccountsTextView();

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.reactivateButton) {
            if (reactivateAccountEditText.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "All fields must be filled in", Toast.LENGTH_SHORT).show();
                return;
            }
            String emailToReactivate = reactivateAccountEditText.getText().toString();
            User user = new User();
            user.activateUser(emailToReactivate, new User.UserCallback() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getActivity(),
                            "User successfully reactivated",
                            Toast.LENGTH_SHORT).show();
                    updateDeactivatedAccountsTextView();
                    reactivateAccountEditText.setText("");
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getActivity(),
                            "User notreactivated",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateDeactivatedAccountsTextView() {
        deactivatedAccountsTextView.setText("");

        User user = new User();

        user.getAllDeactivatedUsers(new User.UserCallbackWithType<List<Map<String, String>>>() {
            @Override
            public void onSuccess(List<Map<String, String>> result) {
                StringBuilder stringBuilder = new StringBuilder();
                for (Map<String, String> deactivatedUser : result) {
                    String deactivatedEmail = deactivatedUser.get("email").toString();
                    String deactivatedUserType = deactivatedUser.get("userType").toString();
                    stringBuilder.append("Email: ").append(deactivatedEmail).append("\n")
                            .append("User Type: ").append(deactivatedUserType).append("\n\n");
                }
                // Set the concatenated string as the text of the TextView
                deactivatedAccountsTextView.setText(stringBuilder.toString());
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }
}
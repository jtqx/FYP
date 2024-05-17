package com.example.fyp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class JoinFragment extends Fragment implements View.OnClickListener {

    View view;
    Button logInButton;
    EditText emailEditText;
    EditText passwordEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_join, container, false);

        emailEditText = (EditText)view.findViewById(R.id.emailEditText);
        passwordEditText = (EditText)view.findViewById(R.id.passwordEditText);
        logInButton = (Button)view.findViewById(R.id.logInButton);
        logInButton.setOnClickListener(this);

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
        int green = ContextCompat.getColor(requireContext(), R.color.green);
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
    public void onClick(View view) {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
    }
}
package com.example.fyp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class User {
    FirebaseFirestore db;

    public User() {
        db = FirebaseFirestore.getInstance();
    }

    public interface UserCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void checkIfUserExists(String email, UserCallback callback) {
        // Query the database to check if a user with the given email exists
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // User with the given email exists
                        callback.onSuccess();
                    } else {
                        // User with the given email does not exist
                        callback.onFailure(new Exception("User not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void createUser(String email, String password, UserCallback callback) {
        // AtomicBoolean result = new AtomicBoolean(false);
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("password", password);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                        Log.i("info", "User created successfully");
                        callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void logInUser(String email, String password, UserCallback callback) {
        // Query the database to check if a user with the given email exists
        db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // User with the given email exists
                        callback.onSuccess();
                    } else {
                        // User with the given email does not exist
                        callback.onFailure(new Exception("User not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }
}

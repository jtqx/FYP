package com.example.fyp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

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

    public interface UserCallbackWithType<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public void checkIfUserExists(String email, UserCallbackWithType<Boolean> callback) {
        // Get the document reference for the specified email
        DocumentReference docRef = db.collection("users").document(email);

        // Get the document snapshot
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Document with the specified email exists
                    callback.onSuccess(true);
                } else {
                    // Document does not exist
                    callback.onSuccess(false);
                }
            } else {
                // Error occurred while fetching document
                callback.onFailure(task.getException());
            }
        });
    }

    public void createUser(String email, String password, UserCallback callback) {
        // AtomicBoolean result = new AtomicBoolean(false);
        Map<String, Object> user = new HashMap<>();
        user.put("userType", "End User");
        user.put("email", email);
        user.put("password", password);
        user.put("firstName", "");
        user.put("lastName", "");
        user.put("height", "");
        user.put("weight", "");
        user.put("bmi", "");
        user.put("allergies", "");
        user.put("chronicConditions", "");
        user.put("medication", "");

        db.collection("users").document(email)
                .set(user)
                .addOnSuccessListener(documentReference -> {
                        Log.i("info", "User created successfully");
                        callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void logInUser(String email, String password, UserCallbackWithType<String> callback) {
        // Query the database to check if a user with the given email exists
        db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // User with the given email and password exists
                        // Assuming there's only one user with the given email
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String userType = document.getString("userType");
                        if (userType != null) {
                            callback.onSuccess(userType);
                        } else {
                            callback.onFailure(new Exception("User type not found"));
                        }
                    } else {
                        // User with the given email does not exist
                        callback.onFailure(new Exception("User not found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void updateUserGeneralProfile(String email, String firstName, String lastName,
                           UserCallback callback) {
        // Get the document reference for the specified documentId
        DocumentReference docRef = db.collection("users").document(email);

        // Create a map to store the first name and last name
        Map<String, Object> data = new HashMap<>();
        data.put("firstName", firstName);
        data.put("lastName", lastName);

        // Update the document with the provided updates
        docRef.update(data)
                .addOnSuccessListener(aVoid -> {
                    // User information updated successfully
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void updateUserBodyProfile(String email, int height, int weight, double bmi,
                                         UserCallback callback) {
        // Get the document reference for the specified documentId
        DocumentReference docRef = db.collection("users").document(email);

        // Create a map to store the first name and last name
        Map<String, Object> data = new HashMap<>();
        data.put("height", height);
        data.put("weight", weight);
        data.put("bmi", bmi);

        // Update the document with the provided updates
        docRef.update(data)
                .addOnSuccessListener(aVoid -> {
                    // User information updated successfully
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void updateUserMedicalHistory(String email, String allergies, String chronicConditions,
                                         String medication, UserCallback callback) {
        // Get the document reference for the specified documentId
        DocumentReference docRef = db.collection("users").document(email);

        // Create a map to store the first name and last name
        Map<String, Object> data = new HashMap<>();
        data.put("allergies", allergies);
        data.put("chronicConditions", chronicConditions);
        data.put("medication", medication);

        // Update the document with the provided updates
        docRef.update(data)
                .addOnSuccessListener(aVoid -> {
                    // User information updated successfully
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getUser(String email, UserCallbackWithType <Map<String, Object>> callback) {
        // Get the document reference for the specified email
        DocumentReference docRef = db.collection("users").document(email);

        // Get the document snapshot
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Document with the specified email exists, retrieve the data
                    Map<String, Object> userData = document.getData();
                    callback.onSuccess(userData);
                } else {
                    // Document does not exist
                    callback.onFailure(new Exception("User not found"));
                }
            } else {
                // Error occurred while fetching document
                callback.onFailure(task.getException());
            }
        });
    }

    public void updateBusinessPartnerAccountInformation(String email, String newCompanyName,
                                                        String newCompanyAddress,
                                                        int newContactNumber,
                                                        UserCallback callback) {
        // Get the document reference for the specified documentId
        DocumentReference docRef = db.collection("users").document(email);

        // Create a map to store the first name and last name
        Map<String, Object> data = new HashMap<>();
        data.put("companyName", newCompanyName);
        data.put("companyAddress", newCompanyAddress);
        data.put("contactNumber", newContactNumber);

        // Update the document with the provided updates
        docRef.update(data)
                .addOnSuccessListener(aVoid -> {
                    // User information updated successfully
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void updateAdminAccountInformation(String email, String newAdminFirstName,
                                                        String newAdminLastName,
                                                        UserCallback callback) {
        // Get the document reference for the specified documentId
        DocumentReference docRef = db.collection("users").document(email);

        // Create a map to store the first name and last name
        Map<String, Object> data = new HashMap<>();
        data.put("firstName", newAdminFirstName);
        data.put("lastName", newAdminLastName);

        // Update the document with the provided updates
        docRef.update(data)
                .addOnSuccessListener(aVoid -> {
                    // User information updated successfully
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }
}

package com.example.fyp;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Diet {
    FirebaseFirestore db;

    public Diet() {
        db = FirebaseFirestore.getInstance();
    }

    public interface Callback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface CallbackWithType<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public void checkIfDietExists(String type, CallbackWithType<Boolean> callback) {
        // Get the document reference for the specified email
        DocumentReference docRef = db.collection("diet").document(type);

        // Get the document snapshot
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Document with the specified type exists
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

    public void createDiet(String newDiet, Callback callback) {
        Map<String, Object> diet = new HashMap<>();
        diet.put("type", newDiet);

        db.collection("diet").document(newDiet)
                .set(diet)
                .addOnSuccessListener(documentReference -> {
                    Log.i("info", "Diet created successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void deleteDiet(String diet, Callback callback) {
        db.collection("diet").document(diet)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.i("info", "Diet deleted successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e("error", "Error deleting diet", e);
                    callback.onFailure(e);
                });
    }


    public void getAllDiets(final CallbackWithType<ArrayList<String>> callback) {
        db.collection("diet").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<String> diets = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String diet = document.getString("type");
                        diets.add(diet);
                    }
                    callback.onSuccess(diets);
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                });
    }
}

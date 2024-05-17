package com.example.fyp;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectableMeal {
    FirebaseFirestore db;

    public SelectableMeal() {
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

    public void checkIfSelectableMealExists(String mealName, CallbackWithType<Boolean> callback) {
        // Get the document reference for the specified email
        DocumentReference docRef = db.collection("selectableMeals").document(mealName);

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

    public void getAllSelectableMealNames(CallbackWithType<ArrayList<String>> callback) {
        db.collection("selectableMeals")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<String> mealNames = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String mealName = document.getString("mealName");
                        if (mealName != null && !mealName.isEmpty()) {
                            mealNames.add(mealName);
                        }
                    }
                    callback.onSuccess(mealNames);
                })
                .addOnFailureListener(e -> {
                    Log.e("error", "Error getting favourite meals by email", e);
                    callback.onFailure(e);
                });
    }

    public void getSelectableMealByName(String mealName, CallbackWithType<Map<String, Object>> callback) {
        db.collection("selectableMeals")
                .whereEqualTo("mealName", mealName)
                .limit(1) // Limit to 1 document
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        Map<String, Object> mealData = document.getData();
                        callback.onSuccess(mealData);
                    } else {
                        Log.i("info", "No matching selectable meal found");
                        callback.onFailure(new Exception("No matching selectable meal found"));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("error", "Error getting selectable meal", e);
                    callback.onFailure(e);
                });
    }

    public void getAllSelectableMeals(CallbackWithType<List<Map<String, Object>>> callback) {
        db.collection("selectableMeals")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> mealsData = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Map<String, Object> mealData = document.getData();
                        mealsData.add(mealData);
                    }
                    callback.onSuccess(mealsData);
                })
                .addOnFailureListener(e -> {
                    Log.e("error", "Error getting all selectable meals", e);
                    callback.onFailure(e);
                });
    }

    public void addSelectableMeal(Map<String, Object> selectableMeal, Callback callback) {
        db.collection("selectableMeals").document(selectableMeal.get("mealName").toString())
                .set(selectableMeal)
                .addOnSuccessListener(documentReference -> {
                    Log.i("info", "Popular meal created successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }
}

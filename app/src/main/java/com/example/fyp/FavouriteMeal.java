package com.example.fyp;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FavouriteMeal {
    FirebaseFirestore db;

    public FavouriteMeal() {
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

    public void createFavouriteMeal(String email, String mealName, int calories, int carbs, int fats,
                                    int protein, Callback callback) {
        Map<String, Object> favouriteMeal = new HashMap<>();
        favouriteMeal.put("email", email);
        favouriteMeal.put("mealName", mealName);
        favouriteMeal.put("calories", calories);
        favouriteMeal.put("carbs", carbs);
        favouriteMeal.put("fats", fats);
        favouriteMeal.put("protein", protein);

        db.collection("favouriteMeals")
                .add(favouriteMeal)
                .addOnSuccessListener(documentReference -> {
                    Log.i("info", "Favourite meal created successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void deleteFavouriteMeal(String email, String mealName, Callback callback) {
        db.collection("favouriteMeals")
                .whereEqualTo("email", email)
                .whereEqualTo("mealName", mealName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Log.i("info", "Favourite meal deleted successfully");
                                        callback.onSuccess();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("error", "Error deleting favourite meal", e);
                                        callback.onFailure(e);
                                    });
                        }
                    } else {
                        Log.i("info", "No matching favourite meal found");
                        callback.onFailure(new Exception("No matching favourite meal found"));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("error", "Error finding favourite meal", e);
                    callback.onFailure(e);
                });
    }

    public void getAllFavouriteMealsByEmail(String email, CallbackWithType<ArrayList<String>> callback) {
        db.collection("favouriteMeals")
                .whereEqualTo("email", email)
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

    public void getFavouriteMeal(String email, String mealName, CallbackWithType<Map<String, Object>> callback) {
        db.collection("favouriteMeals")
                .whereEqualTo("email", email)
                .whereEqualTo("mealName", mealName)
                .limit(1) // Limit to 1 document
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        Map<String, Object> mealData = document.getData();
                        callback.onSuccess(mealData);
                    } else {
                        Log.i("info", "No matching favourite meal found");
                        callback.onFailure(new Exception("No matching favourite meal found"));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("error", "Error getting favourite meal", e);
                    callback.onFailure(e);
                });
    }
}

package com.example.fyp;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Exercise {
    private String howTo;
    private String name;
    private String time; // assuming time is in minutes
    private int caloriesBurnt; // assuming caloriesBurnt is an integer

    public Exercise() {
        // Default constructor required for calls to DataSnapshot.getValue(Exercise.class)
    }

    public Exercise(String howTo, String name, String time, int caloriesBurnt) {
        this.howTo = howTo;
        this.name = name;
        this.time = time;
        this.caloriesBurnt = caloriesBurnt;
    }

    // Getters and setters
    public String getHowTo() { return howTo; }
    public void setHowTo(String howTo) { this.howTo = howTo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public int getCaloriesBurnt() { return caloriesBurnt; }
    public void setCaloriesBurnt(int caloriesBurnt) { this.caloriesBurnt = caloriesBurnt; }

    // Method to fetch random exercises from Firestore
    public static void fetchRandomExercises(FirestoreCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("exercise").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<DocumentSnapshot> documents = task.getResult().getDocuments();
                Collections.shuffle(documents);
                List<DocumentSnapshot> randomDocuments = documents.subList(0, Math.min(documents.size(), 5));
                List<Exercise> exercises = new ArrayList<>();
                for (DocumentSnapshot document : randomDocuments) {
                    Exercise exercise = document.toObject(Exercise.class);
                    exercises.add(exercise);
                }
                callback.onSuccess(exercises);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    // Firestore callback interface
    public interface FirestoreCallback {
        void onSuccess(List<Exercise> exercises);
        void onFailure(Exception e);
    }
    public static void updateTotalCalories(String date, String email, int caloriesBurnt) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("calorieByDay")
                .whereEqualTo("date", date)
                .whereEqualTo("name", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DocumentSnapshot document : task.getResult()) {
                            int totalCalories = document.getLong("totalCalorie") != null ? document.getLong("totalCalorie").intValue() : 0;
                            int newTotalCalories = totalCalories - caloriesBurnt;
                            DocumentReference docRef = db.collection("calorieByDay").document(document.getId());
                            docRef.update("totalCalorie", newTotalCalories)
                                    .addOnSuccessListener(aVoid -> Log.d("Calorie", "Total calories updated successfully"))
                                    .addOnFailureListener(e -> Log.e("Calorie", "Error updating total calories", e));
                        }
                    } else {
                        Log.e("Calorie", "Error getting documents: ", task.getException());
                    }
                });
    }
}


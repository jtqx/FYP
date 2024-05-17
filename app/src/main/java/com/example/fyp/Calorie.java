package com.example.fyp;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Calorie {
    private final FirebaseFirestore db;
    private final SimpleDateFormat dateFormat;

    public Calorie() {
        db = FirebaseFirestore.getInstance();
        dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
    }

    public interface CalorieCallback {
        void onSuccess(Map<String, Object> data);
        void onFailure(Exception e);
    }

    public void checkCalorieForToday(String userName, CalorieCallback callback) {
        String currentDate = getCurrentDate();

        db.collection("calorieByDay")
                .whereEqualTo("date", currentDate)
                .whereEqualTo("name", userName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        Map<String, Object> data = document.getData();
                        if (data != null) {
                            Long calorieGoal = (Long) data.get("calorieGoal");
                            Long totalCalorie = (Long) data.get("totalCalorie");
                            int calorieGoalValue = (calorieGoal != null) ? calorieGoal.intValue() : 0;
                            int totalCalorieValue = (totalCalorie != null) ? totalCalorie.intValue() : 0;
                            Map<String, Object> callbackData = new HashMap<>();
                            callbackData.put("calorieGoal", calorieGoalValue);
                            callbackData.put("totalCalorie", totalCalorieValue);
                            callback.onSuccess(callbackData);
                        } else {
                            callback.onFailure(new Exception("Document data is null"));
                        }
                    } else {
                        checkPreviousDayCalorie(userName, callback);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }


    private void checkPreviousDayCalorie(String userName, CalorieCallback callback) {
        String previousDate = getPreviousDate();
        db.collection("calorieByDay")
                .whereEqualTo("date", previousDate)
                .whereEqualTo("name", userName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        int calorieGoal = document.getLong("calorieGoal") != null ? document.getLong("calorieGoal").intValue() : 0;
                        createOrUpdateCalorieDocument(userName, calorieGoal, 0, callback);
                    } else {
                        createOrUpdateCalorieDocument(userName, 0, 0, callback);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    private void createOrUpdateCalorieDocument(String userName, int calorieGoal, int totalCalories, CalorieCallback callback) {
        String currentDate = getCurrentDate();
        Map<String, Object> data = new HashMap<>();
        data.put("date", currentDate);
        data.put("name", userName);
        data.put("calorieGoal", calorieGoal);
        data.put("totalCalorie", totalCalories);

        db.collection("calorieByDay")
                .add(data)
                .addOnSuccessListener(documentReference -> callback.onSuccess(data))
                .addOnFailureListener(callback::onFailure);
    }

    private String getCurrentDate() {
        return dateFormat.format(new Date());
    }

    private String getPreviousDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return dateFormat.format(calendar.getTime());
    }
    public interface UpdateCalorieCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void updateCalorieGoal(int newGoal, String currentDate, String userName, UpdateCalorieCallback callback) {
        db.collection("calorieByDay")
                .whereEqualTo("date", currentDate)
                .whereEqualTo("name", userName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        document.getReference().update("calorieGoal", newGoal)
                                .addOnSuccessListener(aVoid -> {
                                    if (callback != null) {
                                        callback.onSuccess();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    if (callback != null) {
                                        callback.onFailure(e);
                                    }
                                });
                    } else {
                        Log.e("Calorie", "No matching document found for current date and user");
                        if (callback != null) {
                            callback.onFailure(new Exception("No matching document found"));
                        }
                    }
                });
    }
}
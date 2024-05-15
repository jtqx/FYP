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

        // Check if there is a document for today
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
                            // Handle null cases
                            int calorieGoalValue = (calorieGoal != null) ? calorieGoal.intValue() : 0;
                            int totalCalorieValue = (totalCalorie != null) ? totalCalorie.intValue() : 0;
                            // Prepare the data to be sent to the callback
                            Map<String, Object> callbackData = new HashMap<>();
                            callbackData.put("calorieGoal", calorieGoalValue);
                            callbackData.put("totalCalorie", totalCalorieValue);
                            // Invoke the callback with the retrieved data
                            callback.onSuccess(callbackData);
                        } else {
                            // Handle the case where data is null
                            callback.onFailure(new Exception("Document data is null"));
                        }
                    } else {
                        // No document for today, check previous day
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
                        // Document for the previous day exists
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        int calorieGoal = document.getLong("calorieGoal") != null ? document.getLong("calorieGoal").intValue() : 0;
                        createOrUpdateCalorieDocument(userName, calorieGoal, 0, callback); // Create or update document for current day
                    } else {
                        // No document for the previous day, create a new document for the current day with default values
                        createOrUpdateCalorieDocument(userName, 0, 0, callback);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    private void createOrUpdateCalorieDocument(String userName, int calorieGoal, int totalCalories, CalorieCallback callback) {
        String currentDate = getCurrentDate();

        // Create or update the document for the current day
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

    public void updateCalorieGoal(int newGoal, String currentDate, String userName) {
        db.collection("calorieByDay")
                .whereEqualTo("date", currentDate)
                .whereEqualTo("name", userName)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        document.getReference().update("calorieGoal", newGoal);
                    } else {
                        // No matching document found
                        // Handle the situation accordingly
                        Log.e("Calorie", "No matching document found for current date and user");
                    }
                });
    }
}



    /*private FirebaseFirestore db;
    private String name;

    public Calorie() {
        db = FirebaseFirestore.getInstance();
    }
    interface UserCallback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public void calculateTotalCalories(final String currentDate, final String currentName) {
        db.collection("mealRecord")
                .whereEqualTo("date", currentDate)
                .whereEqualTo("email", currentName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int totalCalories = 0;
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            int calories = document.getLong("calories").intValue(); // Assuming calories is an integer
                            // Accumulate total calories
                            totalCalories += calories;
                        }

                        // Save the total calories for the specified date and name
                        saveTotalCalories(currentDate, currentName, totalCalories);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure to retrieve meal records
                        Log.e("Meal Record", "Failed to retrieve meal records: " + e.getMessage());
                    }
                });
    }



    private void saveTotalCalories(String date, String name, int totalCalories) {
        // Create a map to hold the calorie data
        Map<String, Object> calorieData = new HashMap<>();
        calorieData.put("date", date);
        calorieData.put("name", name);
        calorieData.put("calorieCount", totalCalories);

        // Add the document to the "calorieByDay" collection with an auto-generated document ID
        db.collection("calorieByDay")
                .add(calorieData)
                .addOnSuccessListener(documentReference -> {
                    // Successfully saved calorie data
                    Log.d("Calorie Data", "Calorie count for " + name + " on " + date + " saved successfully.");
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e("Calorie Data", "Failed to save calorie count for " + name + " on " + date + ": " + e.getMessage());
                });
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
        return dateFormat.format(new Date());
    }
    public void calculateCaloriesForCurrentDate(UserCallback<Integer> callback) {
        String currentDate = getCurrentDate();

        db.collection("mealRecord")
                .whereEqualTo("date", currentDate)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalCalories = 0; // Declare totalCalories here
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        int calories = document.getLong("calories").intValue();
                        totalCalories += calories;
                    }
                    callback.onSuccess(totalCalories);
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                });
    }

    public void setCalorieGoal(int calorieGoal, String name, UserCallback<Void> callback) {
        // Get the current date
        String currentDate = getCurrentDate();

        // Check if a calorie goal document already exists for the current date and name
        db.collection("calorieByDay")
                .whereEqualTo("date", currentDate)
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        // If no document exists, add a new one
                        Map<String, Object> calorieGoalData = new HashMap<>();
                        calorieGoalData.put("calorieGoal", calorieGoal);
                        calorieGoalData.put("name", name);
                        calorieGoalData.put("date", currentDate);

                        db.collection("calorieByDay")
                                .add(calorieGoalData)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d("Calorie Goal", "Calorie goal set successfully for " + name + " on " + currentDate + ".");
                                    callback.onSuccess(null); // Invoke the callback
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Calorie Goal", "Failed to set calorie goal for " + name + " on " + currentDate + ": " + e.getMessage());
                                    callback.onFailure(e); // Invoke the callback with failure
                                });
                    } else {
                        // If a document exists, update the existing one
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        document.getReference().update("calorieGoal", calorieGoal)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Calorie Goal", "Calorie goal updated successfully for " + name + " on " + currentDate + ".");
                                    callback.onSuccess(null); // Invoke the callback
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Calorie Goal", "Failed to update calorie goal for " + name + " on " + currentDate + ": " + e.getMessage());
                                    callback.onFailure(e); // Invoke the callback with failure
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Calorie Goal", "Failed to query calorie goal for " + name + " on " + currentDate + ": " + e.getMessage());
                    callback.onFailure(e); // Invoke the callback with failure
                });
    }



    private void updateSubsequentDatesWithCalorieGoal(String startDate, String name, int calorieGoal) {
        // Query for documents with dates after the startDate and matching the specified name
        db.collection("calorieByDay")
                .whereGreaterThan("date", startDate)
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Update each document with the same calorie goal for the specified name
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().update("calorieGoal", calorieGoal)
                                .addOnSuccessListener(aVoid -> {
                                    // Successfully updated calorie goal for subsequent dates for the specified name
                                    Log.d("Calorie Goal", "Calorie goal updated successfully for subsequent dates for " + name + ".");
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure
                                    Log.e("Calorie Goal", "Failed to update calorie goal for subsequent dates for " + name + ": " + e.getMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Log.e("Calorie Goal", "Failed to query subsequent dates for " + name + ": " + e.getMessage());
                });
    }

    public void setCalorieGoalForDate(String date, int calorieGoal, String name, UserCallback<Void> callback) {
        // Update the calorie goal for the specified date and name
        Map<String, Object> calorieData = new HashMap<>();
        calorieData.put("calorieGoal", calorieGoal);
        calorieData.put("name", name);

        db.collection("calorieByDay")
                .whereEqualTo("date", date)
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        documentSnapshot.getReference().set(calorieData)
                                .addOnSuccessListener(aVoid -> {
                                    // Update subsequent dates with the new calorie goal for the specified name
                                    updateSubsequentDatesWithCalorieGoal(date, name, calorieGoal);
                                })
                                .addOnFailureListener(e -> {
                                    callback.onFailure(e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                });
    }

    public void getCalorieCount(String email, String date, UserCallback<Integer> callback) {
        db.collection("calorieByDay")
                .whereEqualTo("date", date)
                .whereEqualTo("name", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        Integer calorieCount = document.getLong("calorieCount") != null ?
                                document.getLong("calorieCount").intValue() : 0; // Set calorieCount to 0 if not found
                        callback.onSuccess(calorieCount);
                    } else {
                        // Document not found, set calorieCount to 0
                        callback.onSuccess(0);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure to retrieve calorie count
                    callback.onFailure(e);
                });
    }


    public void getCalorieGoal(String email, String date, UserCallback<Integer> callback) {
        db.collection("calorieByDay")
                .whereEqualTo("name", email)
                .whereEqualTo("date", date)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        Long calorieGoalLong = document.getLong("calorieGoal");
                        if (calorieGoalLong != null) {
                            int calorieGoal = calorieGoalLong.intValue();
                            callback.onSuccess(calorieGoal);
                        } else {
                            callback.onSuccess(0); // Return 0 if no calorie goal entry found
                        }
                    } else {
                        callback.onSuccess(0); // Return 0 if no documents found
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                });
    }


    public void updateCalorieCount(String email, String date, int newCalorieCount, UserCallback<Void> callback) {
        db.collection("calorieByDay")
                .whereEqualTo("email", email)
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        documentSnapshot.getReference().update("calorieCount", newCalorieCount)
                                .addOnSuccessListener(aVoid -> {
                                    callback.onSuccess(null);
                                })
                                .addOnFailureListener(e -> {
                                    callback.onFailure(e);
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                });
    }
    public void calculateCaloriesForDate(String date, UserCallback<Integer> callback) {

        db.collection("mealRecord")
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalCalories = 0;
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        int calories = document.getLong("calories").intValue();
                        totalCalories += calories;
                    }
                    callback.onSuccess(totalCalories);
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                });
    }*/


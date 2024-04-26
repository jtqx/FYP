package com.example.fyp;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MealRecord {
    FirebaseFirestore db;

    public MealRecord() {
        db = FirebaseFirestore.getInstance();
    }

    public interface MealRecordCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface MealRecordCallbackWithType<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public void createMealRecord(String date, String mealType, String mealName,
                                 int calories, int carbs, int fats, int protein, String email,
                                 MealRecordCallback callback) {
        Map<String, Object> mealRecord = new HashMap<>();
        mealRecord.put("date", date);
        mealRecord.put("mealType", mealType);
        mealRecord.put("mealName", mealName);
        mealRecord.put("calories", calories);
        mealRecord.put("carbs", carbs);
        mealRecord.put("fats", fats);
        mealRecord.put("protein", protein);
        mealRecord.put("email", email);

        db.collection("mealRecords")
                .add(mealRecord)
                .addOnSuccessListener(documentReference -> {
                    Log.i("info", "Meal record created successfully");
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getMealRecordsByMealType(String email, String date, String mealType,
                              MealRecordCallbackWithType<List<DocumentSnapshot>> callback) {
        db.collection("mealRecords")
                .whereEqualTo("email", email)
                .whereEqualTo("date", date)
                .whereEqualTo("mealType", mealType)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> mealRecords = queryDocumentSnapshots.getDocuments();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        callback.onSuccess(mealRecords);
                    } else {
                        callback.onFailure(new Exception("No meal records found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void getMealRecordByDateTypeName(String email, String date, String mealType,
                                            String mealName,
                                            MealRecordCallbackWithType<List<DocumentSnapshot>> callback) {
        db.collection("mealRecords")
                .whereEqualTo("email", email)
                .whereEqualTo("date", date)
                .whereEqualTo("mealType", mealType)
                .whereEqualTo("mealName", mealName)
                .limit(1) // Limit the query to return only one document
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> mealRecords = queryDocumentSnapshots.getDocuments();
                    if (!mealRecords.isEmpty()) {
                        callback.onSuccess(mealRecords); // Pass the list of documents to the callback
                    } else {
                        callback.onFailure(new Exception("No meal record found"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void editMealRecord(String email, String date, String mealType, String mealName,
                               int calories, int carbs, int fats, int protein,
                               MealRecordCallback callback) {
        getMealRecordByDateTypeName(email, date, mealType, mealName,
                new MealRecordCallbackWithType<List<DocumentSnapshot>>() {
            @Override
            public void onSuccess(List<DocumentSnapshot> mealRecords) {
                // Assuming only one document is returned
                DocumentSnapshot mealRecord = mealRecords.get(0);
                String documentId = mealRecord.getId();
                Map<String, Object> updatedData = new HashMap<>();
                updatedData.put("calories", calories);
                updatedData.put("carbs", carbs);
                updatedData.put("fats", fats);
                updatedData.put("protein", protein);
                db.collection("mealRecords")
                        .document(documentId)
                        .update(updatedData)
                        .addOnSuccessListener(aVoid -> {
                            callback.onSuccess();
                        })
                        .addOnFailureListener(callback::onFailure);
            }

            @Override
            public void onFailure(Exception e) {
                Log.i("info", "Error in updating meal record");
            }
        });
    }

    public void deleteMealRecord(String email, String date, String mealType, String mealName,
                                 MealRecordCallback callback) {
        getMealRecordByDateTypeName(email, date, mealType, mealName,
                new MealRecordCallbackWithType<List<DocumentSnapshot>>() {
                    @Override
                    public void onSuccess(List<DocumentSnapshot> mealRecords) {
                        // Assuming only one document is returned
                        DocumentSnapshot mealRecord = mealRecords.get(0);
                        String documentId = mealRecord.getId();
                        db.collection("mealRecords")
                                .document(documentId)
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    callback.onSuccess();
                                })
                                .addOnFailureListener(callback::onFailure);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.i("info", "Error in deleting meal record");
                    }
                });
    }

    public void getMealRecord(String email, String date, String mealType,
                              MealRecordCallback callback) {
        // Query the database to check if a user with the given email and password exists
        db.collection("mealRecords")
                .whereEqualTo("email", email)
                .whereEqualTo("date", date)
                .whereEqualTo("mealType", mealType)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        callback.onSuccess();
                    } else {
                        callback.onFailure(new Exception("Error"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }
}

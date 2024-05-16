package com.example.fyp;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

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

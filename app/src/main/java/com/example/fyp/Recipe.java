package com.example.fyp;

import android.provider.BaseColumns;


import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class Recipe {

        private final FirebaseFirestore db;
        private final CollectionReference recipesCollection;

        public Recipe() {
            db = FirebaseFirestore.getInstance();
            recipesCollection = db.collection("recipes");
        }
        public interface UserCallback {
            void onSuccess();
            void onFailure(Exception e);
        }

        public interface UserCallbackWithType<T> {
            void onSuccess(T result);
            void onFailure(Exception e);
        }

    public interface RecipeDocumentIdCallback {
        void onSuccess(String documentId);
        void onFailure(Exception e);
    }

        public void addRecipe(String author, String name, String ingredients, String steps) {
            Map<String, Object> recipe = new HashMap<>();
            recipe.put("author", author);
            recipe.put("name", name);
            recipe.put("ingredients", ingredients);
            recipe.put("steps", steps);

            recipesCollection.add(recipe)
                    .addOnSuccessListener(documentReference -> {
                        // Recipe added successfully
                    })
                    .addOnFailureListener(e -> {
                        // Handle failure
                    });
        }

        public void getRecipesByAuthor(String author, UserCallbackWithType<List<Map<String, Object>>> callback) {
            recipesCollection.whereEqualTo("author", author)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<Map<String, Object>> recipes = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                recipes.add(document.getData());
                            }
                            callback.onSuccess(recipes);
                        } else {
                            callback.onFailure(task.getException());
                        }
                    });
        }
        public void getAllRecipes(UserCallbackWithType<List<Map<String, Object>>> callback) {
            recipesCollection.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<Map<String, Object>> recipes = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                recipes.add(document.getData());
                            }
                            callback.onSuccess(recipes);
                        } else {
                            callback.onFailure(task.getException());
                        }
                    });
        }

    public void updateRecipe(String documentId, Map<String, Object> updates, UserCallback callback) {
        recipesCollection.document(documentId)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Recipe updated successfully
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    callback.onFailure(e);
                });
    }


    public void searchRecipesByName(String author, String query, UserCallbackWithType<List<Map<String, Object>>> callback) {
            recipesCollection.whereEqualTo("author", author)
                    .whereGreaterThanOrEqualTo("name", query)
                    .whereLessThanOrEqualTo("name", query + "\uf8ff")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<Map<String, Object>> recipes = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                recipes.add(document.getData());
                            }
                            callback.onSuccess(recipes);
                        } else {
                            callback.onFailure(task.getException());
                        }
                    });
        }

        public void searchRecipesByNameOrAuthor(String query, UserCallbackWithType<List<Map<String, Object>>> callback) {
            // Create separate queries for searching in "name" and "author" fields
            Query nameQuery = recipesCollection.whereGreaterThanOrEqualTo("name", query)
                    .whereLessThanOrEqualTo("name", query + "\uf8ff");
            Query authorQuery = recipesCollection.whereGreaterThanOrEqualTo("author", query)
                    .whereLessThanOrEqualTo("author", query + "\uf8ff");

            // Perform both queries asynchronously
            Task<QuerySnapshot> nameQueryTask = nameQuery.get();
            Task<QuerySnapshot> authorQueryTask = authorQuery.get();

            Tasks.whenAllSuccess(nameQueryTask, authorQueryTask)
                    .addOnSuccessListener(querySnapshots -> {
                        List<Map<String, Object>> recipes = new ArrayList<>();
                        for (Object result : querySnapshots) {
                            QuerySnapshot snapshot = (QuerySnapshot) result;
                            for (DocumentSnapshot document : snapshot.getDocuments()) {
                                recipes.add(document.getData());
                            }
                        }
                        callback.onSuccess(recipes);
                    })
                    .addOnFailureListener(callback::onFailure);
        }

    public void deleteRecipe(String documentId, UserCallback callback) {
        recipesCollection.document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Recipe deleted successfully
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    callback.onFailure(e);
                });
    }

    public void getRecipeDocumentId(String name, String author, RecipeDocumentIdCallback callback) {
        recipesCollection.whereEqualTo("name", name)
                .whereEqualTo("author", author)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0); // Assuming there's only one document
                        String documentId = documentSnapshot.getId();
                        callback.onSuccess(documentId);
                    } else {
                        callback.onFailure(new Exception("Document not found"));
                    }
                });
    }


}

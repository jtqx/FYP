package com.example.fyp;

import android.graphics.Bitmap;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
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

    public void addRecipe(String author, String name, String ingredients, String steps, Bitmap recipeImage, String type) {
        boolean checked = false;
        uploadImageToStorage(recipeImage, new UploadImageCallback() {
            @Override
            public void onSuccess(Uri imageUrl) {
                Map<String, Object> recipe = new HashMap<>();
                recipe.put("author", author);
                recipe.put("name", name);
                recipe.put("ingredients", ingredients);
                recipe.put("steps", steps);
                recipe.put("imageUrl", imageUrl.toString());
                recipe.put("type", type);
                recipe.put("adminCheck", checked);

                recipesCollection.add(recipe)
                        .addOnSuccessListener(documentReference -> {
                        })
                        .addOnFailureListener(e -> {
                        });
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    interface UploadImageCallback {
        void onSuccess(Uri imageUrl);
        void onFailure(Exception e);
    }
    private void uploadImageToStorage(Bitmap imageBitmap, UploadImageCallback callback) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageData = baos.toByteArray();

        String filename = UUID.randomUUID().toString() + ".jpg";

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("images/" + filename);

        imageRef.putBytes(imageData)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        callback.onSuccess(uri);
                    }).addOnFailureListener(e -> {
                        callback.onFailure(e);
                    });
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
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

    public void updateRecipe(String documentId, String author, String name, String ingredients, String steps, Bitmap updatedImage,String selectedType, UserCallback callback) {
        uploadImageToStorage(updatedImage, new UploadImageCallback() {
            @Override
            public void onSuccess(Uri imageUrl) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("name", name);
                updates.put("ingredients", ingredients);
                updates.put("steps", steps);
                updates.put("imageUrl", imageUrl.toString());
                updates.put("type", selectedType);
                recipesCollection.document(documentId)
                        .update(updates)
                        .addOnSuccessListener(aVoid -> {
                            callback.onSuccess();
                        })
                        .addOnFailureListener(e -> {
                            callback.onFailure(e);
                        });
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
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

    public void searchRecipesByType(String type, UserCallbackWithType<List<Map<String, Object>>> callback) {
        recipesCollection.whereEqualTo("type", type)
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
            Query nameQuery = recipesCollection.whereGreaterThanOrEqualTo("name", query)
                    .whereLessThanOrEqualTo("name", query + "\uf8ff");
            Query authorQuery = recipesCollection.whereGreaterThanOrEqualTo("author", query)
                    .whereLessThanOrEqualTo("author", query + "\uf8ff");
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
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                });
    }

    public void getRecipeDocumentId(String name, String author, RecipeDocumentIdCallback callback) {
        recipesCollection.whereEqualTo("name", name)
                .whereEqualTo("author", author)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String documentId = documentSnapshot.getId();
                        callback.onSuccess(documentId);
                    } else {
                        callback.onFailure(new Exception("Document not found"));
                    }
                });
    }

    public void addCategory(String category){
        Map<String, Object> newType = new HashMap<>();
        newType.put("type", category);
        db.collection("recipeCategories").add(newType)
                .addOnSuccessListener(documentReference -> {
                })
                .addOnFailureListener(e -> {
                });
    }
    public void getCompany(String name,final companyDetailsCallback callback) {
        db.collection("users")
                .whereEqualTo("email", name)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String companyName = document.getString("companyName");
                        callback.onSuccess(companyName);
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public interface companyDetailsCallback {
        void onSuccess(String companyName);
        void onFailure(Exception e);
    }
}

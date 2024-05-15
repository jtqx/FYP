package com.example.fyp;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Admin {
    private final FirebaseFirestore db;

    public Admin() {
        db = FirebaseFirestore.getInstance();
    }

    public interface UserCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface UserCallbackWithType<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public void adminCheck(Admin.UserCallbackWithType<List<Map<String, Object>>> callback){
        Query recipes = db.collection("recipes").whereEqualTo("adminCheck",false);
        Query products = db.collection("products").whereEqualTo("adminCheck",false);

        Task<QuerySnapshot> recipesTask = recipes.get();
        Task<QuerySnapshot> productsTask = products.get();

        Tasks.whenAllSuccess(recipesTask, productsTask)
                .addOnSuccessListener(querySnapshots -> {
                    List<Map<String, Object>> uploads = new ArrayList<>();
                    for (Object result : querySnapshots) {
                        QuerySnapshot snapshot = (QuerySnapshot) result;
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            uploads.add(document.getData());
                        }
                    }
                    callback.onSuccess(uploads);
                })
                .addOnFailureListener(callback::onFailure);
    }
    public void deleteProduct(String documentId, Product.UserCallback callback) {
        db.collection("product").document(documentId)
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

    public void getProductDocumentId(String name, String author, Product.ProductDocumentIdCallback callback) {
        db.collection("products").whereEqualTo("name", name)
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

    public void deleteRecipe(String documentId, Recipe.UserCallback callback) {
        db.collection("recipes").document(documentId)
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

    public void getRecipeDocumentId(String name, String author, Recipe.RecipeDocumentIdCallback callback) {
        db.collection("recipes").whereEqualTo("name", name)
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

    public void updateAdminCheckStatus(String collection, String documentId, UserCallback callback) {
        db.collection(collection).document(documentId)
                .update("adminCheck", true)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

}

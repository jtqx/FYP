package com.example.fyp;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
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

    public void searchUsers(String query, UserCallbackWithType<List<Map<String, Object>>> callback) {
        db.collection("users")
                .orderBy("email")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> userList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Map<String, Object> user = document.getData();
                            userList.add(user);
                        }
                        callback.onSuccess(userList);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void getAdminDetails(String name, String userType, final AdminDetailsCallback callback) {
        db.collection("users")
                .whereEqualTo("email", name)
                .whereEqualTo("userType", userType)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String firstName = document.getString("firstName");
                        String lastName = document.getString("lastName");
                        callback.onSuccess(firstName, lastName);
                    } else {
                        callback.onSuccess(null, null); // No matching document found
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public interface AdminDetailsCallback {
        void onSuccess(String firstName, String lastName);
        void onFailure(Exception e);
    }

    public void getBusinessDetails(String name, String userType, final BusinessDetailsCallback callback) {
        db.collection("users")
                .whereEqualTo("email", name)
                .whereEqualTo("userType", userType)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String companyName = document.getString("companyName");
                        String companyAddress = document.getString("companyAddress");
                        String email = document.getString("email");
                        Long contactNumber = document.getLong("contactNumber");
                        int contact = contactNumber != null ? contactNumber.intValue() : 0;
                        callback.onSuccess(companyName, companyAddress,contact,email);
                    } else {
                        callback.onSuccess(null, null,0,null); // No matching document found
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public interface BusinessDetailsCallback {
        void onSuccess(String companyName, String companyAddress, int contactNumber, String email);
        void onFailure(Exception e);
    }

    public void getEndUserDetails(String name, String userType, final EndUserDetailsCallback callback) {
        db.collection("users")
                .whereEqualTo("email", name)
                .whereEqualTo("userType", userType)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String firstName = document.getString("firstName");
                        String lastName = document.getString("lastName");
                        String email = document.getString("email");
                        callback.onSuccess(firstName, lastName,email);
                    } else {
                        callback.onSuccess(null, null,null); // No matching document found
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public interface EndUserDetailsCallback {
        void onSuccess(String firstName, String lastName, String email);
        void onFailure(Exception e);
    }

    public void getAllRequests(UserCallbackWithType<List<Map<String, Object>>> callback) {
        db.collection("conversionRequest").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> requests = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            requests.add(document.getData());
                        }
                        callback.onSuccess(requests);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void getRequestByUserEmail(String userEmail, UserCallbackWithType<Map<String, Object>> callback) {
        db.collection("conversionRequest")
                .whereEqualTo("email", userEmail)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        Map<String, Object> request = document.getData();
                        callback.onSuccess(request);
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }
    public void getRequestDetails(String name,final RequestDetailsCallback callback) {
        db.collection("conversionRequest")
                .whereEqualTo("email", name)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String companyName = document.getString("companyName");
                        String companyAddress = document.getString("companyAddress");
                        String email = document.getString("email");
                        Long contactNumber = document.getLong("contactNumber");
                        int contact = contactNumber != null ? contactNumber.intValue() : 0;
                        String certUrl = document.getString("certificateUrl");
                        callback.onSuccess(companyName, companyAddress,contact, email, certUrl);
                    } else {
                        callback.onSuccess(null, null,0,null, null); // No matching document found
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public interface RequestDetailsCallback {
        void onSuccess(String companyName, String companyAddress, int contactNumber, String email, String certUrl);
        void onFailure(Exception e);
    }

    public interface DocumentIdCallback {
        void onSuccess(String documentId);
        void onFailure(Exception e);
    }

    public void getRequestDocumentId(String name, DocumentIdCallback callback) {
        db.collection("conversionRequest").whereEqualTo("email", name)
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
    public void deleteRequest(String documentId, UserCallback callback) {
        db.collection("conversionRequest").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e);
                });
    }

    public void updateUserType(String email, String newUserType, UserCallback callback) {
        db.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        db.collection("users")
                                .document(document.getId())
                                .update("userType", newUserType)
                                .addOnSuccessListener(aVoid -> callback.onSuccess())
                                .addOnFailureListener(callback::onFailure);
                    } else {
                        callback.onFailure(new Exception("No user found with the provided email"));
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

}

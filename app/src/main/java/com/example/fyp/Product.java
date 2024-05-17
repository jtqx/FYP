package com.example.fyp;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Product {
    private final FirebaseFirestore db;
    private final CollectionReference productCollection;

    public Product() {
        db = FirebaseFirestore.getInstance();
        productCollection = db.collection("products");
    }
    public interface UserCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface UserCallbackWithType<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public interface ProductDocumentIdCallback {
        void onSuccess(String documentId);
        void onFailure(Exception e);
    }

    public void addProduct(String author, String name, String description,String nutriVal, double price, Bitmap productImage, String type, String range) {
        boolean checked = false;
        uploadImageToStorage(productImage, new UploadImageCallback() {
            @Override
            public void onSuccess(Uri imageUrl) {
                Map<String, Object> product = new HashMap<>();
                product.put("author", author);
                product.put("name", name);
                product.put("description", description);
                product.put("price", price);
                product.put("nutriVal",nutriVal);
                product.put("imageUrl", imageUrl.toString());
                product.put("type", type);
                product.put("range",range);
                product.put("adminCheck", checked);

                productCollection.add(product)
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
    private void uploadImageToStorage(Bitmap imageBitmap, Product.UploadImageCallback callback) {
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

    public void getProductsByAuthor(String author, UserCallbackWithType<List<Map<String, Object>>> callback) {
        productCollection.whereEqualTo("author", author)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> products = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            products.add(document.getData());
                        }
                        callback.onSuccess(products);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }
    public void getAllProducts(UserCallbackWithType<List<Map<String, Object>>> callback) {
        productCollection.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> products = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            products.add(document.getData());
                        }
                        callback.onSuccess(products);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void updateProduct(String documentId,String author, String name, String description,String nutriVal, double price, Bitmap updatedImage, String selectedType,String range, UserCallback callback) {
        uploadImageToStorage(updatedImage, new UploadImageCallback() {
            @Override
            public void onSuccess(Uri imageUrl) {
                Map<String, Object> updates = new HashMap<>();
                updates.put("name", name);
                updates.put("description", description);
                updates.put("price", price);
                updates.put("nutriVal",nutriVal);
                updates.put("imageUrl", imageUrl.toString());
                updates.put("type", selectedType);
                updates.put("range", range);
                productCollection.document(documentId)
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



    public void searchProductByName(String author, String query, UserCallbackWithType<List<Map<String, Object>>> callback) {
        productCollection.whereEqualTo("author", author)
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> products = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            products.add(document.getData());
                        }
                        callback.onSuccess(products);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }
    public void getProductByNameAndByAuthor(String author, String name, UserCallbackWithType<List<Map<String, Object>>> callback) {
        productCollection.whereEqualTo("author", author)
                .whereEqualTo("name", name)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> products = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            products.add(document.getData());
                        }
                        callback.onSuccess(products);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void searchProductByType(String type, UserCallbackWithType<List<Map<String, Object>>> callback) {
        productCollection.whereEqualTo("type", type)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> products = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            products.add(document.getData());
                        }
                        callback.onSuccess(products);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }
    public void searchProductByRange(String range, UserCallbackWithType<List<Map<String, Object>>> callback) {
        productCollection.whereEqualTo("range", range)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Map<String, Object>> products = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            products.add(document.getData());
                        }
                        callback.onSuccess(products);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    public void searchProductByNameOrAuthor(String query, UserCallbackWithType<List<Map<String, Object>>> callback) {
        Query nameQuery = productCollection.whereGreaterThanOrEqualTo("name", query)
                .whereLessThanOrEqualTo("name", query + "\uf8ff");
        Query authorQuery = productCollection.whereGreaterThanOrEqualTo("author", query)
                .whereLessThanOrEqualTo("author", query + "\uf8ff");

        Task<QuerySnapshot> nameQueryTask = nameQuery.get();
        Task<QuerySnapshot> authorQueryTask = authorQuery.get();

        Tasks.whenAllSuccess(nameQueryTask, authorQueryTask)
                .addOnSuccessListener(querySnapshots -> {
                    List<Map<String, Object>> products = new ArrayList<>();
                    for (Object result : querySnapshots) {
                        QuerySnapshot snapshot = (QuerySnapshot) result;
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            products.add(document.getData());
                        }
                    }
                    callback.onSuccess(products);
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void deleteProduct(String documentId, UserCallback callback) {
        productCollection.document(documentId)
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

    public void getProductDocumentId(String name, String author, ProductDocumentIdCallback callback) {
        productCollection.whereEqualTo("name", name)
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
}

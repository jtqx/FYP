package com.example.fyp;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order {

    private final FirebaseFirestore db;
    private final CollectionReference ordersCollection;
    private final DocumentReference counterDocument;

    public Order() {
        db = FirebaseFirestore.getInstance();
        ordersCollection = db.collection("orders");
        counterDocument = db.collection("counters").document("orderCounter");
    }
    public interface UserCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface UserCallbackWithType<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    public interface OrderDocumentIdCallback {
        void onSuccess(String documentId);
        void onFailure(Exception e);
    }

    public void addOrder(String email, String author, String name, String address, double total, String status) {

        counterDocument.update("value", FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> {
                    counterDocument.get().addOnSuccessListener(documentSnapshot -> {
                        long orderId = documentSnapshot.getLong("value");
                        String formattedOrderId = String.format("%05d", orderId);
                        String paddedOrderId = "#" + formattedOrderId;
                        Map<String, Object> order = new HashMap<>();
                        order.put("orderId", paddedOrderId);
                        order.put("email", email);
                        order.put("author", author);
                        order.put("name", name);
                        order.put("address", address);
                        order.put("total", total);
                        order.put("status", status);
                        ordersCollection.add(order)
                                .addOnSuccessListener(documentReference -> {
                                    // Order added successfully
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    public void getOrdersByEmail(String email, UserCallbackWithType<List<Map<String, Object>>> callback) {
        ordersCollection.whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> orders = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        orders.add(document.getData());
                    }
                    callback.onSuccess(orders);
                })
                .addOnFailureListener(callback::onFailure);
    }
    public void getOrdersByAuthor(String author,String status, UserCallbackWithType<List<Map<String, Object>>> callback) {
        ordersCollection.whereEqualTo("author", author)
                .whereEqualTo("status", status)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> orders = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        orders.add(document.getData());
                    }
                    callback.onSuccess(orders);
                })
                .addOnFailureListener(callback::onFailure);
    }
    public void getOrdersByAuthorAndStatus(String author, UserCallbackWithType<List<Map<String, Object>>> callback) {
        ordersCollection.whereEqualTo("author", author)
                .whereIn("status", Arrays.asList("fulfilled", "canceled"))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Map<String, Object>> orders = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        orders.add(document.getData());
                    }
                    callback.onSuccess(orders);
                })
                .addOnFailureListener(callback::onFailure);
    }

}

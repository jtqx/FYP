package com.example.fyp;

import android.graphics.Bitmap;
import android.net.Uri;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Order {
    private final FirebaseFirestore db;
    private final CollectionReference ordersCollection;

    public Order() {
        db = FirebaseFirestore.getInstance();
        ordersCollection = db.collection("orders");
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

        Map<String, Object> order = new HashMap<>();
        order.put("email", email);
        order.put("author", author);
        order.put("name", name);
        order.put("address", address);
        order.put("total", total);
        order.put("status", status);

        ordersCollection.add(order)
                .addOnSuccessListener(documentReference -> {
                })
                .addOnFailureListener(e -> {
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
    public void getOrdersByAuthor(String author, UserCallbackWithType<List<Map<String, Object>>> callback) {
        ordersCollection.whereEqualTo("author", author)
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

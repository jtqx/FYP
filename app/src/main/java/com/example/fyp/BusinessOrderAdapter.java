package com.example.fyp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.Map;

public class BusinessOrderAdapter extends RecyclerView.Adapter<BusinessOrderAdapter.OrderViewHolder> {

    private List<Map<String, Object>> currentOrders;
    private OnItemClickListener mListener;
    private BusinessHomeFragment fragment;

    public interface OnItemClickListener {
        void onItemClick(Map<String, Object> order);
    }
    public BusinessOrderAdapter(BusinessHomeFragment fragment) {
        this.fragment = fragment;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void setCurrentOrders(List<Map<String, Object>> orders) {
        currentOrders = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_card_view, parent, false);
        return new OrderViewHolder(view,fragment);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Map<String, Object> order = currentOrders.get(position);
        holder.bind(order, mListener);
    }

    @Override
    public int getItemCount() {
        return currentOrders != null ? currentOrders.size() : 0;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView orderIdTextView;
        Button declineButton;
        Button acceptButton;
        FirebaseFirestore db;
        CollectionReference ordersCollection;
        BusinessHomeFragment fragment;

        OrderViewHolder(View itemView, BusinessHomeFragment fragment) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            orderIdTextView = itemView.findViewById(R.id.orderIdView);
            declineButton = itemView.findViewById(R.id.declineButton);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            db = FirebaseFirestore.getInstance();
            ordersCollection = db.collection("orders");
            this.fragment = fragment;
        }

        void bind(Map<String, Object> order,OnItemClickListener listener) {
            String name = order.get("name").toString();
            String orderId = order.get("orderId").toString();
            nameTextView.setText(name);
            orderIdTextView.setText(orderId);

            declineButton.setOnClickListener(v -> {
                String orderID = order.get("orderId").toString();
                updateOrderStatus(orderID, "canceled");
            });

            acceptButton.setOnClickListener(v -> {
                String orderID = order.get("orderId").toString();
                updateOrderStatus(orderID, "fulfilled");
            });
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(order);
                }
            });
        }
        private void updateOrderStatus(String orderId, String status) {
            Query query = ordersCollection.whereEqualTo("orderId", orderId);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ordersCollection.document(document.getId())
                                .update("status", status)
                                .addOnSuccessListener(aVoid -> {
                                    // Successfully updated status
                                    Toast.makeText(itemView.getContext(), "Order status updated successfully", Toast.LENGTH_SHORT).show();
                                    fragment.fetchCurrentOrders();
                                })
                                .addOnFailureListener(e -> {
                                    // Handle failure
                                    Toast.makeText(itemView.getContext(), "Failed to update order status", Toast.LENGTH_SHORT).show();
                                });
                    }
                } else {
                    // Handle failure
                    Toast.makeText(itemView.getContext(), "Failed to find order with orderId: " + orderId, Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}

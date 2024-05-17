package com.example.fyp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class BusinessPastOrdersAdapter extends RecyclerView.Adapter<BusinessPastOrdersAdapter.PastOrderViewHolder> {
    private List<Map<String, Object>> pastOrdersList;
    private BusinessOrderAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(Map<String, Object> order);
    }

    public void setOnItemClickListener(BusinessOrderAdapter.OnItemClickListener listener) {
        mListener = listener;
    }
    public void setCurrentOrders(List<Map<String, Object>> orders) {
        pastOrdersList = orders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PastOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_order_card_view, parent, false);
        return new PastOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PastOrderViewHolder holder, int position) {
        Map<String, Object> order = pastOrdersList.get(position);
        holder.bind(order, mListener);
    }

    @Override
    public int getItemCount() {
        return pastOrdersList != null ? pastOrdersList.size() : 0;
    }
    static class PastOrderViewHolder extends RecyclerView.ViewHolder {
        TextView tagTextView;
        TextView nameTextView;
        TextView orderIdTextView;

        PastOrderViewHolder(View itemView) {
            super(itemView);
            tagTextView = itemView.findViewById(R.id.tagTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            orderIdTextView = itemView.findViewById(R.id.orderIdView);
        }
        void bind(Map<String, Object> order, BusinessOrderAdapter.OnItemClickListener listener) {
            String name = order.get("name").toString();
            String orderId = order.get("orderId").toString();
            String tag = order.get("status").toString();
            nameTextView.setText(name);
            orderIdTextView.setText(orderId);
            tagTextView.setText(tag);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(order);
                }
            });
        }
    }
}


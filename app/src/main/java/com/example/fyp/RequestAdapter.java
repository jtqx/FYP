package com.example.fyp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder>{
    private List<Map<String, Object>> requests;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Map<String, Object> item);
    }

    public RequestAdapter(List<Map<String, Object>> requests) {
        this.requests = requests;
    }

    public RequestAdapter(List<Map<String, Object>> requests, OnItemClickListener onItemClickListener) {
        this.requests = requests;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Map<String, Object> request = requests.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView requestName;

        RequestViewHolder(View itemView) {
            super(itemView);
            requestName = itemView.findViewById(R.id.requestName);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Map<String, Object> item = requests.get(position);
                    onItemClickListener.onItemClick(item);
                }
            });
        }

        void bind(Map<String, Object> upload) {
            requestName.setText((String) upload.get("email"));
        }
    }
    public void updateData(List<Map<String, Object>> newData) {
        this.requests = newData;
        notifyDataSetChanged();
    }
}

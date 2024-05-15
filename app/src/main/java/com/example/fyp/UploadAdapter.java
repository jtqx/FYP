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

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.UploadViewHolder> {
    private List<Map<String, Object>> uploads;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Map<String, Object> item);
    }

    public UploadAdapter(List<Map<String, Object>> uploads, OnItemClickListener onItemClickListener) {
        this.uploads = uploads;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public UploadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_card_view, parent, false);
        return new UploadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UploadViewHolder holder, int position) {
        Map<String, Object> upload = uploads.get(position);
        holder.bind(upload);
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    class UploadViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        private ImageView uploadImageView;

        UploadViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.nameTextView);
            descriptionTextView = itemView.findViewById(R.id.authorTextView);
            uploadImageView = itemView.findViewById(R.id.recipeImageView);

            itemView.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Map<String, Object> item = uploads.get(position);
                    onItemClickListener.onItemClick(item);
                }
            });
        }

        void bind(Map<String, Object> upload) {
            titleTextView.setText((String) upload.get("name"));
            descriptionTextView.setText((String) upload.get("author"));
            if (upload.containsKey("imageUrl")) {
                String recipeImageUrl = upload.get("imageUrl").toString();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(recipeImageUrl);
                Picasso.get().load(recipeImageUrl).into(uploadImageView);
            } else {
                uploadImageView.setImageResource(R.drawable.food);
            }
        }
    }
}


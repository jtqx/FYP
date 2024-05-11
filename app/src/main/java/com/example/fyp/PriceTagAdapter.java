package com.example.fyp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import java.util.List;

public class PriceTagAdapter extends RecyclerView.Adapter<PriceTagAdapter.TagViewHolder> {
    private Context context;
    private List<String> priceRanges;
    private OnTagClickListener listener;

    public interface OnTagClickListener {
        void onTagClick(String range);
    }

    public PriceTagAdapter(Context context, List<String> priceRanges, OnTagClickListener listener) {
        this.context = context;
        this.priceRanges = priceRanges;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe_type_tag, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        String range = priceRanges.get(position);
        holder.tagTextView.setText(range);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTagClick(range);
            }
        });
    }

    @Override
    public int getItemCount() {
        return priceRanges.size();
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {
        TextView tagTextView;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tagTextView = itemView.findViewById(R.id.tagTextView);
        }
    }
}


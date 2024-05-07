package com.example.fyp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeTypeTagAdapter extends RecyclerView.Adapter<RecipeTypeTagAdapter.TagViewHolder> {
    private Context context;
    private List<String> recipeTypes;
    private OnTagClickListener listener;

    public interface OnTagClickListener {
        void onTagClick(String type);
    }

    public RecipeTypeTagAdapter(Context context, List<String> recipeTypes, OnTagClickListener listener) {
        this.context = context;
        this.recipeTypes = recipeTypes;
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
        String type = recipeTypes.get(position);
        holder.tagTextView.setText(type);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTagClick(type);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeTypes.size();
    }

    public static class TagViewHolder extends RecyclerView.ViewHolder {
        TextView tagTextView;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tagTextView = itemView.findViewById(R.id.tagTextView);
        }
    }
    public void setRecipeTypes(List<String> recipeTypes) {
        this.recipeTypes.clear();
        this.recipeTypes.addAll(recipeTypes);
        notifyDataSetChanged();
    }
}


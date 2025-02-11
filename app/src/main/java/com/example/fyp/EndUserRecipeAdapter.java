package com.example.fyp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class EndUserRecipeAdapter extends RecyclerView.Adapter<EndUserRecipeAdapter.RecipeViewHolder> {

    private Context context;
    private List<Map<String, Object>> recipeList;

    public EndUserRecipeAdapter(Context context,List<Map<String, Object>> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_card_view, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Map<String, Object> recipeData = recipeList.get(position);
        holder.bind(recipeData);
    }
    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        private TextView authorTextView;
        private TextView nameTextView;
        private ImageView recipeImageView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            recipeImageView = itemView.findViewById(R.id.recipeImageView);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Map<String, Object> recipeData = recipeList.get(position);
                    showRecipeDialog(recipeData);
                }
            });
        }

        public void bind(Map<String, Object> recipeData) {
            authorTextView.setText(recipeData.get("author").toString());
            nameTextView.setText(recipeData.get("name").toString());
            if (recipeData.containsKey("imageUrl")) {
                String recipeImageUrl = recipeData.get("imageUrl").toString();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(recipeImageUrl);
                Picasso.get().load(recipeImageUrl).into(recipeImageView);
            } else {
                recipeImageView.setImageResource(R.drawable.food);
            }
        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    private void showRecipeDialog(Map<String, Object> recipeData) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.recipe_dialog_content, null);

        TextView textView1 = dialogView.findViewById(R.id.textView);
        TextView textView2 = dialogView.findViewById(R.id.textView2);
        TextView textView3 = dialogView.findViewById(R.id.textView5);
        TextView textView4 = dialogView.findViewById(R.id.textView4);
        ImageView imageView = dialogView.findViewById(R.id.imageView4);

        textView1.setText(recipeData.get("name").toString());
        textView2.setText(recipeData.get("author").toString());
        textView3.setText(recipeData.get("ingredients").toString());
        textView4.setText(recipeData.get("steps").toString());
        if (recipeData.containsKey("imageUrl")) {
            String recipeImageUrl = recipeData.get("imageUrl").toString();
            Picasso.get().load(recipeImageUrl).into(imageView);}
        else {
            imageView.setImageResource(R.drawable.food);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void updateData(List<Map<String, Object>> newRecipeList) {
        this.recipeList.clear();
        this.recipeList.addAll(newRecipeList);
        notifyDataSetChanged();
    }
}


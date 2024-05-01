package com.example.fyp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessRecipeAdapter extends RecyclerView.Adapter<BusinessRecipeAdapter.RecipeViewHolder> {

    private Context context;
    private List<Map<String, Object>> recipeList;

    public BusinessRecipeAdapter(Context context, List<Map<String, Object>> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Map<String, Object> recipeData = recipeList.get(position);
        holder.bind(recipeData);
    }

    private void showRecipeDialog(Map<String, Object> recipeData) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.recipe_dialog_content, null);

        TextView textView1 = dialogView.findViewById(R.id.textView);
        TextView textView2 = dialogView.findViewById(R.id.textView2);
        TextView textView3 = dialogView.findViewById(R.id.textView5);
        TextView textView4 = dialogView.findViewById(R.id.textView4);

        textView1.setText(recipeData.get("name").toString());
        textView2.setText(recipeData.get("author").toString());
        textView3.setText(recipeData.get("ingredients").toString());
        textView4.setText(recipeData.get("steps").toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.setNeutralButton("Update", (dialog, which) -> showUpdateDialog(recipeData));
        builder.setNegativeButton("Delete", (dialog, which) -> deleteRecipe(recipeData));
        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder {

        private TextView authorTextView;
        private TextView nameTextView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
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
        }
    }

    private void showUpdateDialog(Map<String, Object> recipeData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_update_recipe, null);

        EditText newNameEditText = dialogView.findViewById(R.id.editTextNewName);
        EditText newIngredientsEditText = dialogView.findViewById(R.id.editTextNewIngredients);
        EditText newStepsEditText = dialogView.findViewById(R.id.editTextNewSteps);
        String name = recipeData.get("name") != null ? recipeData.get("name").toString() : "";
        String author = recipeData.get("author") != null ? recipeData.get("author").toString() : "";
        String ingredients = recipeData.get("ingredients") != null ? recipeData.get("ingredients").toString() : "";
        String steps = recipeData.get("steps") != null ? recipeData.get("steps").toString() : "";
        newNameEditText.setText(name);
        newIngredientsEditText.setText(ingredients);
        newStepsEditText.setText(steps);
        builder.setView(dialogView);
        builder.setPositiveButton("Update", (dialog, which) -> {
            String newName = newNameEditText.getText().toString();
            String newIngredients = newIngredientsEditText.getText().toString();
            String newSteps = newStepsEditText.getText().toString();

            // Update recipe in Firestore
            Recipe recipe = new Recipe();
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", newName);
            updates.put("ingredients", newIngredients);
            updates.put("steps", newSteps);
            recipe.getRecipeDocumentId(name, author, new Recipe.RecipeDocumentIdCallback() {
                @Override
                public void onSuccess(String documentId) {
                    Log.d("DocumentId", "DocumentId: " + documentId);
                    recipe.updateRecipe(documentId, updates, new Recipe.UserCallback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailure(Exception e) {
                            // Handle failure
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    // Handle failure
                }
            });
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog updateDialog = builder.create();
        updateDialog.show();
    }

    private void deleteRecipe(Map<String, Object> recipeData) {
        // Delete recipe from Firestore
        Recipe recipe = new Recipe();
        String name = recipeData.get("name") != null ? recipeData.get("name").toString() : "";
        String author = recipeData.get("author") != null ? recipeData.get("author").toString() : "";
        recipe.getRecipeDocumentId(name, author, new Recipe.RecipeDocumentIdCallback() {
            @Override
            public void onSuccess(String documentId) {
                recipe.deleteRecipe(documentId, new Recipe.UserCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Handle failure
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure
            }
        });
    }

    public void updateData(List<Map<String, Object>> newRecipeList) {
        this.recipeList.clear();
        this.recipeList.addAll(newRecipeList);
        notifyDataSetChanged();
    }
}


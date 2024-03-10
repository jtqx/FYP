package com.example.fyp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BusinessRecipeAdapter extends RecyclerView.Adapter<BusinessRecipeAdapter.RecipeViewHolder> {

    private Context context;
    private List<Recipe> recipeList;

    public BusinessRecipeAdapter(Context context, List<Recipe> recipeList) {
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
        Recipe recipe = recipeList.get(position);
        holder.bind(recipe);
    }

    private void showRecipeDialog(Recipe recipe) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.recipe_dialog_content, null);

        TextView textView1 = dialogView.findViewById(R.id.textView);
        TextView textView2 = dialogView.findViewById(R.id.textView2);
        TextView textView3 = dialogView.findViewById(R.id.textView5);
        TextView textView4 = dialogView.findViewById(R.id.textView4);

        textView1.setText(recipe.getName());
        textView2.setText(recipe.getAuthor());
        textView3.setText(recipe.getIngredients());
        textView4.setText(recipe.getSteps());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNeutralButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showUpdateDialog(recipe);
            }
        });
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Recipe recipe = recipeList.get(position);
                        showRecipeDialog(recipe);
                    }
                }
            });
        }

        public void bind(Recipe recipe) {
            authorTextView.setText(recipe.getAuthor());
            nameTextView.setText(recipe.getName());
        }
    }
    private void showUpdateDialog(Recipe recipe) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_update_recipe, null);

        EditText newNameEditText = dialogView.findViewById(R.id.editTextNewName);
        EditText newIngredientsEditText = dialogView.findViewById(R.id.editTextNewIngredients);
        EditText newStepsEditText = dialogView.findViewById(R.id.editTextNewSteps);
        newNameEditText.setText(recipe.getName());
        newIngredientsEditText.setText(recipe.getIngredients());
        newStepsEditText.setText(recipe.getSteps());
        builder.setView(dialogView);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = newNameEditText.getText().toString();
                String newIngredients = newIngredientsEditText.getText().toString();
                String newSteps = newStepsEditText.getText().toString();

                RecipeDatabaseHelper dbHelper = new RecipeDatabaseHelper(context);
                dbHelper.updateRecipe(recipe.getId(), newName, newIngredients, newSteps);
            }
        });
        builder.setNegativeButton("Cancel", null);

        AlertDialog updateDialog = builder.create();
        updateDialog.show();
    }
}


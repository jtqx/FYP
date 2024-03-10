package com.example.fyp;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class BusinessRecipeFragment extends Fragment {
    private ImageButton addButton;
    private String email;
    private RecyclerView recipeRecyclerView;
    private TextView emptyTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_recipe, container, false);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

        addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        recipeRecyclerView = view.findViewById(R.id.recipeRecyclerView);
        emptyTextView = view.findViewById(R.id.emptyTextView);
        RecipeDatabaseHelper dbHelper = new RecipeDatabaseHelper(getContext());
        List<Recipe> recipes = dbHelper.getRecipesByAuthor(email);
        if (recipes.isEmpty()) {
            recipeRecyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText("No current recipes");
        } else {
            recipeRecyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);

            BusinessRecipeAdapter adapter = new BusinessRecipeAdapter(getContext(),recipes);
            recipeRecyclerView.setAdapter(adapter);
            recipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }


        return view;
    }

    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_recipe, null);
        builder.setView(dialogView);

        final EditText editTextRecipeName = dialogView.findViewById(R.id.editTextRecipeName);
        final EditText editTextIngredients = dialogView.findViewById(R.id.editTextIngredients);
        final EditText editTextSteps = dialogView.findViewById(R.id.editTextSteps);
        Button buttonCreate = dialogView.findViewById(R.id.buttonCreate);

        final AlertDialog dialog = builder.create();

        buttonCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextRecipeName.getText().toString().trim();
                String ingredients = editTextIngredients.getText().toString().trim();
                String steps = editTextSteps.getText().toString().trim();

                if (!name.isEmpty() && !ingredients.isEmpty() && !steps.isEmpty()) {
                    addRecipe(email, name, ingredients, steps);
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialog.show();
    }

    private void addRecipe(String email, String name, String ingredients, String steps) {
        RecipeDatabaseHelper dbHelper = new RecipeDatabaseHelper(getContext());
        dbHelper.addRecipe(email, name, ingredients, steps);
    }
}
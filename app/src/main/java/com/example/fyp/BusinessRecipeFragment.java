package com.example.fyp;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessRecipeFragment extends Fragment implements BusinessRecipeAdapter.OnUpdateClickListener{
    private ImageButton addButton;
    private String email;
    private RecyclerView recipeRecyclerView;
    private TextView emptyTextView;

    private BusinessRecipeAdapter adapter;
    private SearchView searchView;
    private String company;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_recipe, container, false);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        company = sharedPreferences.getString("Company", "");
        adapter = new BusinessRecipeAdapter(getContext(), new ArrayList<>());
        adapter.setOnUpdateClickListener(this);

        addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> openAddRecipeFragment());

        recipeRecyclerView = view.findViewById(R.id.recipeRecyclerView);
        emptyTextView = view.findViewById(R.id.emptyTextView);

        searchView = view.findViewById(R.id.searchView);
        updateList();

        return view;
    }

    private void loadRecipes(String query) {
        Recipe recipe = new Recipe();
        if (TextUtils.isEmpty(query)) {
            recipe.getRecipesByAuthor(company, new Recipe.UserCallbackWithType<List<Map<String, Object>>>() {
                @Override
                public void onSuccess(List<Map<String, Object>> recipes) {
                    if (recipes.isEmpty()) {
                        recipeRecyclerView.setVisibility(View.GONE);
                        emptyTextView.setVisibility(View.VISIBLE);
                        emptyTextView.setText("No current recipes");
                    } else {
                        recipeRecyclerView.setVisibility(View.VISIBLE);
                        emptyTextView.setVisibility(View.GONE);
                        adapter.updateData(recipes);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("BusinessRecipeFragment", "Error loading recipes: " + e.getMessage());
                }
            });
        } else {
            recipe.searchRecipesByName(company, query, new Recipe.UserCallbackWithType<List<Map<String, Object>>>() {
                @Override
                public void onSuccess(List<Map<String, Object>> recipes) {
                    if (recipes.isEmpty()) {
                        recipeRecyclerView.setVisibility(View.GONE);
                        emptyTextView.setVisibility(View.VISIBLE);
                        emptyTextView.setText("No current recipes");
                    } else {
                        recipeRecyclerView.setVisibility(View.VISIBLE);
                        emptyTextView.setVisibility(View.GONE);
                        adapter.updateData(recipes);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    // Handle failure
                    Log.e("BusinessRecipeFragment", "Error searching recipes: " + e.getMessage());
                }
            });
        }
    }

    private void openAddRecipeFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        BusinessRecipeAddFragment addFragment = new BusinessRecipeAddFragment();

        fragmentTransaction.replace(R.id.businessFragmentContainerView, addFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    /*private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_recipe, null);
        builder.setView(dialogView);

        final EditText editTextRecipeName = dialogView.findViewById(R.id.editTextRecipeName);
        final EditText editTextIngredients = dialogView.findViewById(R.id.editTextIngredients);
        final EditText editTextSteps = dialogView.findViewById(R.id.editTextSteps);
        Button buttonCreate = dialogView.findViewById(R.id.buttonCreate);

        final AlertDialog dialog = builder.create();

        buttonCreate.setOnClickListener(v -> {
            String name = editTextRecipeName.getText().toString().trim();
            String ingredients = editTextIngredients.getText().toString().trim();
            String steps = editTextSteps.getText().toString().trim();

            if (!name.isEmpty() && !ingredients.isEmpty() && !steps.isEmpty()) {
                addRecipe(email, name, ingredients, steps);
                updateList();
                dialog.dismiss();
            } else {
                Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }*/

    private void updateList() {
        adapter.setOnUpdateClickListener(this);
        recipeRecyclerView.setAdapter(adapter);
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadRecipes("");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadRecipes(newText);
                return true;
            }
        });
    }

    @Override
    public void onUpdateClick(Map<String, Object> recipeData) {

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        BusinessRecipeUpdateFragment updateFragment = new BusinessRecipeUpdateFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("recipeData", (Serializable) recipeData);
        updateFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.businessFragmentContainerView, updateFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}

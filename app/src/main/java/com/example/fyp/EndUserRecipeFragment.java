package com.example.fyp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndUserRecipeFragment extends Fragment {
    private String email;
    private RecyclerView recipeRecyclerView;
    private TextView emptyTextView;
    private RecyclerView tagRecyclerView;
    private RecipeTypeTagAdapter tagAdapter;

    private EndUserRecipeAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_end_user_recipe, container, false);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        recipeRecyclerView = view.findViewById(R.id.recipeRecyclerView);
        emptyTextView = view.findViewById(R.id.emptyTextView);

        adapter = new EndUserRecipeAdapter(getContext(), new ArrayList<>());
        recipeRecyclerView.setAdapter(adapter);
        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadRecipes("");
        SearchView searchView = view.findViewById(R.id.searchView);
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

        tagRecyclerView = view.findViewById(R.id.tagRecyclerView);
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tagAdapter = new RecipeTypeTagAdapter(getContext(), new ArrayList<>(), this::onTagClick);
        tagRecyclerView.setAdapter(tagAdapter);

        loadRecipeTypes();

        TextView clearFilterTextView = view.findViewById(R.id.clearFilterTextView);
        clearFilterTextView.setOnClickListener(v -> clearFiltersAndReloadRecipes());

        return view;
    }
    private void loadRecipes(String query) {
        Recipe recipe = new Recipe();
        if (TextUtils.isEmpty(query)) {
            recipe.getAllRecipes(new Recipe.UserCallbackWithType<List<Map<String, Object>>>() {
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
                    Log.e("BusinessRecipeFragment", "Error loading recipes: " + e.getMessage());
                }
            });
        } else {
            recipe.searchRecipesByNameOrAuthor(query, new Recipe.UserCallbackWithType<List<Map<String, Object>>>() {
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
    private void loadRecipeTypes() {
        // Fetch recipe types from the recipeCategories collection
        FirebaseFirestore.getInstance().collection("recipeCategories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> types = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String type = document.getString("type");
                        types.add(type);
                    }
                    tagAdapter.setRecipeTypes(types);
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }

    private void onTagClick(String type) {
        // Perform search for recipes by type
        loadRecipesByType(type);
    }

    private void loadRecipesByType(String type) {
        Recipe recipe = new Recipe();
        recipe.searchRecipesByType(type, new Recipe.UserCallbackWithType<List<Map<String, Object>>>() {
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
            }
        });
    }
    private void clearFiltersAndReloadRecipes() {
        // Clear any applied filters (if any)
        String query = "";
        loadRecipes(query);
    }
}

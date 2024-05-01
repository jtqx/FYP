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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndUserRecipeFragment extends Fragment {
    private String email;
    private RecyclerView recipeRecyclerView;
    private TextView emptyTextView;

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
}
package com.example.fyp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class EndUserRecipeFragment extends Fragment {
    private String email;
    private RecyclerView recipeRecyclerView;
    private TextView emptyTextView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_end_user_recipe, container, false);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        recipeRecyclerView = view.findViewById(R.id.recipeRecyclerView);
        emptyTextView = view.findViewById(R.id.emptyTextView);
        RecipeDatabaseHelper dbHelper = new RecipeDatabaseHelper(getContext());
        List<Recipe> recipes = dbHelper.getAllRecipes();
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
}
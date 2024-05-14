package com.example.fyp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Map;

public class AdminRecipeDetailsFragment extends Fragment {

    private Map<String, Object> item;
    private TextView nameText;
    private TextView authorText;
    private ImageView recipeImage;
    private TextView ingredientText;
    private TextView stepText;
    private Button approveButton;
    private Button rejectButton;
    private Admin admin;
    private String name;
    private String author;

    public AdminRecipeDetailsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_recipe_details, container, false);

        nameText = view.findViewById(R.id.nameView);
        authorText = view.findViewById(R.id.authorView);
        recipeImage = view.findViewById(R.id.recipeImageView);
        ingredientText = view.findViewById(R.id.ingredientsView);
        stepText = view.findViewById(R.id.stepView);
        approveButton = view.findViewById(R.id.approveButton);
        rejectButton = view.findViewById(R.id.rejectButton);

        if (getArguments() != null) {
            item = (Map<String, Object>) getArguments().getSerializable("item");
            if (item != null) {
                author = item.get("author") != null ? item.get("author").toString() : "";
                name = item.get("name") != null ? item.get("name").toString() : "";
                String ingredient = item.get("ingredients") != null ? item.get("ingredients").toString() : "";
                String step = item.get("steps") != null ? item.get("steps").toString() : "";
                nameText.setText(name);
                authorText.setText(author);
                ingredientText.setText(ingredient);
                stepText.setText(step);
                if (item.containsKey("imageUrl")) {
                    String recipeImageUrl = item.get("imageUrl").toString();
                    Log.d("ProductImageUrl", "Recipe Image URL: " + recipeImageUrl);

                    Picasso.get().load(recipeImageUrl).into(recipeImage);
                }
            }
        }

        approveButton.setOnClickListener(v -> checkUpload());
        rejectButton.setOnClickListener(v -> deleteUpload());

        return view;
    }

    private void checkUpload(){
        admin = new Admin();
        String collection = "recipes";
        admin.getRecipeDocumentId(name, author, new Recipe.RecipeDocumentIdCallback() {
            @Override
            public void onSuccess(String documentId) {
                // Call the updateRecipe method with the obtained document ID
                admin.updateAdminCheckStatus(collection, documentId, new Admin.UserCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getContext(), "adminCheck updated successfully", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        // Handle update failure
                        Toast.makeText(getContext(), "Failed to update adminCheck", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure to get document ID
                Toast.makeText(getContext(), "Failed to get document ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteUpload(){
        admin = new Admin();
        admin.getRecipeDocumentId(name, author, new Recipe.RecipeDocumentIdCallback() {
            @Override
            public void onSuccess(String documentId) {
                // Call the updateRecipe method with the obtained document ID
                admin.deleteRecipe( documentId, new Recipe.UserCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getContext(), "recipe deleted successfully", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();

                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), "Failed to delete recipe", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onFailure(Exception e) {
                // Handle failure to get document ID
                Toast.makeText(getContext(), "Failed to get document ID", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
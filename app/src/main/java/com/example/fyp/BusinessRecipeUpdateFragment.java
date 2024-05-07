package com.example.fyp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class BusinessRecipeUpdateFragment extends Fragment {

    private EditText newNameEditText;
    private EditText newIngredientsEditText;
    private EditText newStepsEditText;
    private ImageView imageViewRecipeImage;

    private Map<String, Object> recipeData;
    private static final int PICK_IMAGE_REQUEST = 1;

    public BusinessRecipeUpdateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_recipe_update, container, false);

        newNameEditText = view.findViewById(R.id.editTextNewName);
        newIngredientsEditText = view.findViewById(R.id.editTextNewIngredients);
        newStepsEditText = view.findViewById(R.id.editTextNewSteps);
        imageViewRecipeImage = view.findViewById(R.id.imageViewRecipeImage);
        imageViewRecipeImage.setOnClickListener(v -> selectImageFromGalleryOrCamera());

        Button updateButton = view.findViewById(R.id.updateButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        updateButton.setOnClickListener(v -> onUpdateButtonClick());
        cancelButton.setOnClickListener(v -> onCancelButtonClick());

        // Populate EditText fields with recipe data
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("recipeData")) {
            recipeData = (Map<String, Object>) bundle.getSerializable("recipeData");
            if (recipeData != null) {
                String name = recipeData.get("name") != null ? recipeData.get("name").toString() : "";
                String ingredients = recipeData.get("ingredients") != null ? recipeData.get("ingredients").toString() : "";
                String steps = recipeData.get("steps") != null ? recipeData.get("steps").toString() : "";
                newNameEditText.setText(name);
                newIngredientsEditText.setText(ingredients);
                newStepsEditText.setText(steps);
                if (recipeData.containsKey("imageUrl")) {
                    String recipeImageUrl = recipeData.get("imageUrl").toString();
                    Log.d("RecipeImageUrl", "Recipe Image URL: " + recipeImageUrl);
                    // Load image from Firebase Storage directly into ImageView
                    Picasso.get().load(recipeImageUrl).into(imageViewRecipeImage);
                }
            }
        }

        return view;
    }

    private void selectImageFromGalleryOrCamera() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Gallery", "Camera"}, (dialog, which) -> {
            switch (which) {
                case 0: // Gallery
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
                    break;
                case 1: // Camera
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(requireContext().getPackageManager()) != null) {
                        startActivityForResult(cameraIntent, PICK_IMAGE_REQUEST);
                    } else {
                        Toast.makeText(requireContext(), "Camera not available", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            if (data.getData() != null) {
                // Image selected from gallery
                Uri selectedImageUri = data.getData();
                imageViewRecipeImage.setImageURI(selectedImageUri);
            } else if (data.getExtras() != null && data.getExtras().get("data") != null) {
                // Image captured from camera
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageViewRecipeImage.setImageBitmap(photo);
            }
        }
    }

    private void onUpdateButtonClick() {
        String newName = newNameEditText.getText().toString();
        String newIngredients = newIngredientsEditText.getText().toString();
        String newSteps = newStepsEditText.getText().toString();
        Bitmap updatedImage = ((BitmapDrawable) imageViewRecipeImage.getDrawable()).getBitmap();

        // Update recipe in Firestore
        if (recipeData != null) {
            String name = recipeData.get("name") != null ? recipeData.get("name").toString() : "";
            String author = recipeData.get("author") != null ? recipeData.get("author").toString() : "";

            // Obtain the document ID asynchronously
            Recipe recipe = new Recipe();
            recipe.getRecipeDocumentId(name, author, new Recipe.RecipeDocumentIdCallback() {
                @Override
                public void onSuccess(String documentId) {
                    // Call the updateRecipe method with the obtained document ID
                    recipe.updateRecipe(documentId, author, newName, newIngredients, newSteps, updatedImage, new Recipe.UserCallback() {
                        @Override
                        public void onSuccess() {
                            // Handle update success
                            Toast.makeText(getContext(), "Recipe updated successfully", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            // Handle update failure
                            Toast.makeText(getContext(), "Failed to update recipe", Toast.LENGTH_SHORT).show();
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



    private void onCancelButtonClick() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}

package com.example.fyp;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BusinessRecipeAddFragment extends Fragment {

    private EditText editTextRecipeName;
    private EditText editTextIngredients;
    private EditText editTextSteps;
    private ImageView imageViewRecipeImage;
    private Button buttonCreate;
    private Spinner spinnerRecipeType;
    private String selectedType;
    private FirebaseFirestore db;
    private CollectionReference recipeCategoriesCollection;
    private ArrayList<String> recipeTypesList = new ArrayList<>();
    private String email;

    private static final int PICK_IMAGE_REQUEST = 1;

    public BusinessRecipeAddFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_business_recipe_add, container, false);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

        db = FirebaseFirestore.getInstance();
        recipeCategoriesCollection = db.collection("recipeCategories");

        // Initialize UI components
        editTextRecipeName = view.findViewById(R.id.editTextRecipeName);
        editTextIngredients = view.findViewById(R.id.editTextIngredients);
        editTextSteps = view.findViewById(R.id.editTextSteps);
        imageViewRecipeImage = view.findViewById(R.id.imageViewRecipeImage);
        buttonCreate = view.findViewById(R.id.buttonCreate);
        spinnerRecipeType = view.findViewById(R.id.spinnerRecipeType);

        // Set click listener for create button
        buttonCreate.setOnClickListener(v -> createRecipe());
        imageViewRecipeImage.setOnClickListener(v -> openImagePicker());
        getRecipeTypes();

        return view;
    }

    private void createRecipe() {
        // Get input values
        String name = editTextRecipeName.getText().toString().trim();
        String ingredients = editTextIngredients.getText().toString().trim();
        String steps = editTextSteps.getText().toString().trim();

        // Validate input
        if (!name.isEmpty() && !ingredients.isEmpty() && !steps.isEmpty() && selectedType != null) {
            Bitmap recipeImage = getRecipeImage();
            addRecipe(email, name, ingredients, steps, recipeImage, selectedType);
            navigateBack();
        } else {
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }
    private void getRecipeTypes() {
        recipeCategoriesCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                String type = document.getString("type");
                if (type != null) {
                    recipeTypesList.add(type);
                }
            }
            // Populate Spinner with recipe types
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, recipeTypesList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerRecipeType.setAdapter(adapter);

            // Set Spinner listener to capture selected recipe type
            spinnerRecipeType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedType = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Do nothing
                }
            });
        }).addOnFailureListener(e -> {
            // Handle failure
            Log.e("Firestore", "Error getting recipe types: ", e);
        });
    }
    private void openImagePicker() {
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

    private Bitmap getRecipeImage() {
        // Convert ImageView drawable to Bitmap
        BitmapDrawable drawable = (BitmapDrawable) imageViewRecipeImage.getDrawable();
        if (drawable != null) {
            return drawable.getBitmap();
        }
        return null;
    }

    private void addRecipe(String email, String name, String ingredients, String steps, Bitmap recipeImage, String type) {
        Recipe recipe = new Recipe();
        recipe.addRecipe(email, name, ingredients, steps, recipeImage, type);
    }

    private void navigateBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}

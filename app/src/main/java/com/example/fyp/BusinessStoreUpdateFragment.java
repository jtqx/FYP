package com.example.fyp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BusinessStoreUpdateFragment extends Fragment {

    private EditText newNameEditText;
    private EditText newDescriptionEditText;
    private EditText newPriceEditText;
    private ImageView imageViewProductImage;

    private Map<String, Object> productData;
    private Spinner spinnerProductType;
    private String selectedType;
    private FirebaseFirestore db;
    private CollectionReference productCategoriesCollection;
    private ArrayList<String> productTypesList = new ArrayList<>();
    private static final int PICK_IMAGE_REQUEST = 1;

    public BusinessStoreUpdateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_store_update, container, false);
        db = FirebaseFirestore.getInstance();
        productCategoriesCollection = db.collection("recipeCategories");

        newNameEditText = view.findViewById(R.id.editTextNewName);
        newDescriptionEditText = view.findViewById(R.id.editTextNewDescription);
        newPriceEditText = view.findViewById(R.id.editTextNewPrice);
        imageViewProductImage = view.findViewById(R.id.imageViewProductImage);
        imageViewProductImage.setOnClickListener(v -> selectImageFromGalleryOrCamera());
        spinnerProductType = view.findViewById(R.id.spinnerProductType);

        Button updateButton = view.findViewById(R.id.updateButton);
        Button cancelButton = view.findViewById(R.id.cancelButton);

        updateButton.setOnClickListener(v -> onUpdateButtonClick());
        cancelButton.setOnClickListener(v -> onCancelButtonClick());

        // Populate EditText fields with recipe data
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("productData")) {
            productData = (Map<String, Object>) bundle.getSerializable("productData");
            if (productData != null) {
                String name = productData.get("name") != null ? productData.get("name").toString() : "";
                String description = productData.get("description") != null ? productData.get("description").toString() : "";
                String price = productData.get("price") != null ? productData.get("price").toString() : "";
                newNameEditText.setText(name);
                newDescriptionEditText.setText(description);
                newPriceEditText.setText(price);
                if (productData.containsKey("imageUrl")) {
                    String productImageUrl = productData.get("imageUrl").toString();
                    Log.d("ProductImageUrl", "Recipe Image URL: " + productImageUrl);
                    // Load image from Firebase Storage directly into ImageView
                    Picasso.get().load(productImageUrl).into(imageViewProductImage);
                }
                if (productData.containsKey("type")) {
                    String productType = productData.get("type").toString();
                    fetchProductTypesAndPopulateSpinner(productType);
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
                imageViewProductImage.setImageURI(selectedImageUri);
            } else if (data.getExtras() != null && data.getExtras().get("data") != null) {
                // Image captured from camera
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageViewProductImage.setImageBitmap(photo);
            }
        }
    }

    private void onUpdateButtonClick() {
        String newName = newNameEditText.getText().toString();
        String newDescription = newDescriptionEditText.getText().toString();
        String newPrice = newPriceEditText.getText().toString();
        Double priceInt = Double.parseDouble(newPrice);
        String range = "";
        try {
            if (priceInt < 5) {
                range = "Low";
            } else if (priceInt >= 5 && priceInt < 20) {
                range = "Medium";
            } else {
                range = "High";
            }
        }catch (NumberFormatException e) {
            e.printStackTrace();
        }
        Bitmap updatedImage = ((BitmapDrawable) imageViewProductImage.getDrawable()).getBitmap();
        selectedType = spinnerProductType.getSelectedItem().toString();

        if (productData != null) {
            String name = productData.get("name") != null ? productData.get("name").toString() : "";
            String author = productData.get("author") != null ? productData.get("author").toString() : "";

            // Obtain the document ID asynchronously
            Product product = new Product();
            final String newRange = range;
            product.getProductDocumentId(name, author, new Product.ProductDocumentIdCallback() {
                @Override
                public void onSuccess(String documentId) {
                    // Call the updateRecipe method with the obtained document ID
                    product.updateProduct(documentId, author, newName, newDescription, priceInt, updatedImage, selectedType, newRange, new Product.UserCallback() {
                        @Override
                        public void onSuccess() {
                            // Handle update success
                            Toast.makeText(getContext(), "Product updated successfully", Toast.LENGTH_SHORT).show();
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
    private void fetchProductTypesAndPopulateSpinner(String selectedType) {
        productCategoriesCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<String> types = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                String type = document.getString("type");
                types.add(type);
            }
            // Populate the spinner with fetched recipe types
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, types);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProductType.setAdapter(adapter);

            // Set the selected recipe type
            int position = adapter.getPosition(selectedType);
            spinnerProductType.setSelection(position);
        }).addOnFailureListener(e -> {
            // Handle failure
            Log.e("Fetch Product Types", "Failed to fetch recipe types: " + e.getMessage());
        });
    }



    private void onCancelButtonClick() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
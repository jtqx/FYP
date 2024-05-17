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

public class BusinessStoreAddFragment extends Fragment {

    private EditText editTextProductName;
    private EditText editTextDescription;
    private EditText editTextPrice;
    private ImageView imageViewProductImage;
    private Button buttonCreate;
    private Spinner spinnerProductType;
    private String selectedType;
    private EditText editTextNutrition;
    private FirebaseFirestore db;
    private CollectionReference productCategoriesCollection;
    private ArrayList<String> productTypesList = new ArrayList<>();
    private String email;
    private String company;

    private static final int PICK_IMAGE_REQUEST = 1;

    public BusinessStoreAddFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_store_add, container, false);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        company = sharedPreferences.getString("Company", "");

        db = FirebaseFirestore.getInstance();
        productCategoriesCollection = db.collection("recipeCategories");

        editTextProductName = view.findViewById(R.id.editTextProductName);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        editTextNutrition = view.findViewById(R.id.editTextNutrition);
        imageViewProductImage = view.findViewById(R.id.imageViewProductImage);
        buttonCreate = view.findViewById(R.id.buttonCreate);
        spinnerProductType = view.findViewById(R.id.spinnerProductType);

        buttonCreate.setOnClickListener(v -> createProduct());
        imageViewProductImage.setOnClickListener(v -> openImagePicker());
        getProductTypes();

        return view;
    }

    private void createProduct() {
        // Get input values
        String name = editTextProductName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String price = editTextPrice.getText().toString().trim();
        String nutriVal = editTextNutrition.getText().toString().trim();
        Double priceInt = Double.parseDouble(price);
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


        if (!name.isEmpty() && !description.isEmpty() && !price.isEmpty() && selectedType != null) {
            Bitmap productImage = getProductImage();
            addProduct(company, name, description, nutriVal, priceInt, productImage, selectedType, range);
            navigateBack();
        } else {
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }
    private void getProductTypes() {
        productCategoriesCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                String type = document.getString("type");
                if (type != null) {
                    productTypesList.add(type);
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, productTypesList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProductType.setAdapter(adapter);

            spinnerProductType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedType = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "Error getting product types: ", e);
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
                Uri selectedImageUri = data.getData();
                imageViewProductImage.setImageURI(selectedImageUri);
            } else if (data.getExtras() != null && data.getExtras().get("data") != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imageViewProductImage.setImageBitmap(photo);
            }
        }
    }

    private Bitmap getProductImage() {
        BitmapDrawable drawable = (BitmapDrawable) imageViewProductImage.getDrawable();
        if (drawable != null) {
            return drawable.getBitmap();
        }
        return null;
    }

    private void addProduct(String email, String name, String description, String nutriVal, double price, Bitmap productImage, String type, String range) {
        Product product = new Product();
        product.addProduct(email, name, description, nutriVal,price, productImage, type, range);
    }

    private void navigateBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
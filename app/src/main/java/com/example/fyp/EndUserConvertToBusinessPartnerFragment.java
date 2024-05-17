package com.example.fyp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EndUserConvertToBusinessPartnerFragment extends Fragment implements View.OnClickListener {
    String email;
    View view;
    ImageView uploadCertImageView;
    EditText newCompanyNameEditText;
    EditText newCompanyAddressEditText;
    EditText newContactNumberEditText;
    Button pickImageButton;
    Button submitButton;
    ActivityResultLauncher<Intent> resultLauncher;
    StorageReference storageReference;
    Uri imageUri;
    String downloadUrl;
    EndUserAccountFragment endUserAccountFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_end_user_convert_to_business_partner, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SharedPref",
                Context.MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

        newCompanyNameEditText = view.findViewById(R.id.newCompanyNameEditText);
        newCompanyAddressEditText = view.findViewById(R.id.newCompanyAddressEditText);
        newContactNumberEditText = view.findViewById(R.id.newContactNumberEditText);
        uploadCertImageView = view.findViewById(R.id.uploadCertImageView);
        pickImageButton = view.findViewById(R.id.pickImageButton);
        submitButton = view.findViewById(R.id.submitButton);
        registerResult();

        storageReference = FirebaseStorage.getInstance().getReference();

        endUserAccountFragment = new EndUserAccountFragment();

        pickImageButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);

        return view;
    }

    private void registerResult() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            imageUri = result.getData().getData();
                            if (imageUri != null) {
                                uploadCertImageView.setImageURI(imageUri);
                            } else {
                                Toast.makeText(getActivity(), "No Image Selected", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.pickImageButton) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            resultLauncher.launch(intent);
        } else if (id == R.id.submitButton) {
            if (newCompanyNameEditText.getText().toString().isEmpty() ||
                    newCompanyAddressEditText.getText().toString().isEmpty() ||
                    newContactNumberEditText.getText().toString().isEmpty()) {
                Toast toast = Toast.makeText(getActivity(), "No field must be empty",
                        Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
            if (imageUri != null) {
                uploadImageToFirebase(imageUri);
            } else {
                Toast.makeText(getActivity(), "No Image Selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadImageToFirebase(Uri uri) {
        StorageReference fileReference = storageReference.child("images/" + System.currentTimeMillis() + ".jpg");
        fileReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                downloadUrl = downloadUri.toString();
                                saveUserDataToFirestore();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserDataToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String companyName = newCompanyNameEditText.getText().toString();
        String companyAddress = newCompanyAddressEditText.getText().toString();
        int contactNumber = Integer.parseInt(newContactNumberEditText.getText().toString());

        Map<String, Object> conversionRequest = new HashMap<>();
        conversionRequest.put("email", email);
        conversionRequest.put("companyName", companyName);
        conversionRequest.put("companyAddress", companyAddress);
        conversionRequest.put("contactNumber", contactNumber);
        conversionRequest.put("certificateUrl", downloadUrl);

        db.collection("conversionRequest").document(email)
                .set(conversionRequest)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Request Submitted", Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.endUserFragmentContainerView, endUserAccountFragment)
                                .commit();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

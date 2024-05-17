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
public class AdminRequestDetailsFragment extends Fragment {

    private Map<String, Object> item;
    private TextView companyNameText;
    private TextView companyAddressText;
    private TextView emailText;
    private TextView contactNumberText;
    private ImageView certImageView;
    private Button approveButton;
    private Button rejectButton;
    private String email;

    private Admin admin;

    public AdminRequestDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_request_details, container, false);
        companyNameText = view.findViewById(R.id.companyNameText);
        companyAddressText = view.findViewById(R.id.companyAddressText);
        emailText = view.findViewById(R.id.emailText);
        contactNumberText = view.findViewById(R.id.contactNumberText);
        certImageView = view.findViewById(R.id.certImageView);
        approveButton = view.findViewById(R.id.approveButton);
        rejectButton = view.findViewById(R.id.rejectButton);
        admin = new Admin();
        Bundle args = getArguments();
        if (args != null) {
            item = (Map<String, Object>) args.getSerializable("item");
            if (item != null) {
                email = item.get("email").toString();
                emailText.setText(email);
                Log.d("AdminAdminDetails", "here is " + email);
                getRequestDetails(email);
            }
        }
        approveButton.setOnClickListener(v -> approveRequest());
        rejectButton.setOnClickListener(v -> deleteRequest());

        return view;
    }

    private void getRequestDetails(String email) {
        admin.getRequestDetails(email, new Admin.RequestDetailsCallback() {
            @Override
            public void onSuccess(String companyName, String companyAddress,int contactNumber, String email, String certUrl) {
                if (companyName != null && companyAddress != null&& contactNumber != 0 && email != null) {
                    companyNameText.setText(companyName);
                    companyAddressText.setText(companyAddress);
                    emailText.setText(email);
                    contactNumberText.setText(String.valueOf(contactNumber));
                    if (certUrl != null) {
                        Picasso.get().load(certUrl).into(certImageView);
                    }
                } else {
                    Log.d("AdminAdminDetails", "No matching document found.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("AdminAdminDetails", "Error getting documents: ", e);
            }
        });
    }

        private void deleteRequest(){
            admin = new Admin();
            admin.getRequestDocumentId(email, new Admin.DocumentIdCallback() {
                @Override
                public void onSuccess(String documentId) {
                    // Call the updateRecipe method with the obtained document ID
                    admin.deleteRequest( documentId, new Admin.UserCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getContext(), "request deleted successfully", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(getContext(), "Failed to delete request", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(getContext(), "Failed to get document ID", Toast.LENGTH_SHORT).show();
                }
            });
        }

    private void approveRequest() {
        admin.updateUserType(email, "Business Partner", new Admin.UserCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "User type updated to Business Partner", Toast.LENGTH_SHORT).show();
                deleteRequest();
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to update user type", Toast.LENGTH_SHORT).show();
            }
        });
    }
    }


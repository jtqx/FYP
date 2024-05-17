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

public class AdminStoreDetailsFragment extends Fragment {
    private Map<String, Object> item;
    private TextView nameText;
    private TextView authorText;
    private TextView descText;
    private TextView priceText;
    private ImageView productImageView;
    private Button approveButton;
    private Button rejectButton;
    private String name;
    private String author;

    private Admin admin;

    public AdminStoreDetailsFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_store_details, container, false);
        nameText = view.findViewById(R.id.nameText);
        authorText = view.findViewById(R.id.authorText);
        descText = view.findViewById(R.id.descText);
        priceText = view.findViewById(R.id.priceText);
        productImageView = view.findViewById(R.id.productImageView);
        approveButton = view.findViewById(R.id.approveButton);
        rejectButton = view.findViewById(R.id.rejectButton);
        if (getArguments() != null) {
            item = (Map<String, Object>) getArguments().getSerializable("item");
            if (item != null) {
                author = item.get("author") != null ? item.get("author").toString() : "";
                name = item.get("name") != null ? item.get("name").toString() : "";
                String description = item.get("description") != null ? item.get("description").toString() : "";
                String price = item.get("price") != null ? item.get("price").toString() : "";
                nameText.setText(name);
                authorText.setText(author);
                descText.setText(description);
                priceText.setText(price);
                if (item.containsKey("imageUrl")) {
                    String productImageUrl = item.get("imageUrl").toString();
                    Log.d("ProductImageUrl", "Recipe Image URL: " + productImageUrl);

                    Picasso.get().load(productImageUrl).into(productImageView);
                }
            }
        }
        approveButton.setOnClickListener(v -> checkUpload());
        rejectButton.setOnClickListener(v -> deleteUpload());

        return view;
    }

    private void checkUpload(){
        admin = new Admin();
        String collection = "products";
        admin.getProductDocumentId(name, author, new Product.ProductDocumentIdCallback() {
            @Override
            public void onSuccess(String documentId) {
                admin.updateAdminCheckStatus(collection, documentId, new Admin.UserCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getContext(), "adminCheck updated successfully", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), "Failed to update adminCheck", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to get document ID", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void deleteUpload(){
        admin = new Admin();
        admin.getProductDocumentId(name, author, new Product.ProductDocumentIdCallback() {
            @Override
            public void onSuccess(String documentId) {
                admin.deleteProduct( documentId, new Product.UserCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getContext(), "product deleted successfully", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();

                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(getContext(), "Failed to delete product", Toast.LENGTH_SHORT).show();
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

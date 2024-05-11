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

import com.squareup.picasso.Picasso;

import java.util.Map;

public class EndUserStoreDetailFragment extends Fragment {

    private Map<String, Object> productData;
    private TextView nameText;
    private TextView authorText;
    private TextView descText;
    private TextView priceText;
    private ImageView productImageView;

    public EndUserStoreDetailFragment(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_end_user_store_detail, container, false);

        nameText = view.findViewById(R.id.nameText);
        authorText = view.findViewById(R.id.authorText);
        descText = view.findViewById(R.id.descText);
        priceText = view.findViewById(R.id.priceText);
        productImageView = view.findViewById(R.id.productImageView);
        Button backButton = view.findViewById(R.id.backButton);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("productData")) {
            productData = (Map<String, Object>) bundle.getSerializable("productData");
            if (productData != null) {
                String author = productData.get("author") != null ? productData.get("author").toString() : "";
                String name = productData.get("name") != null ? productData.get("name").toString() : "";
                String description = productData.get("description") != null ? productData.get("description").toString() : "";
                String price = productData.get("price") != null ? productData.get("price").toString() : "";
                nameText.setText(name);
                authorText.setText(author);
                descText.setText(description);
                priceText.setText(price);
                if (productData.containsKey("imageUrl")) {
                    String productImageUrl = productData.get("imageUrl").toString();
                    Log.d("ProductImageUrl", "Recipe Image URL: " + productImageUrl);

                    Picasso.get().load(productImageUrl).into(productImageView);
                }
            }
        }
        backButton.setOnClickListener(v -> onBackButtonClick());
        return view;
    }
    private void onBackButtonClick() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
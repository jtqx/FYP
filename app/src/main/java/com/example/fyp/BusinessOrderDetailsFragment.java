package com.example.fyp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class BusinessOrderDetailsFragment extends Fragment {

    private TextView orderText;
    private TextView priceText;
    private TextView totalPriceText;
    private TextView addressText;
    private TextView deliveryPriceText;
    private String company;

    private Product product;
    private String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_order_details, container, false);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        company = sharedPreferences.getString("Company", "");
        orderText = view.findViewById(R.id.orderText);
        priceText = view.findViewById(R.id.priceText);
        totalPriceText = view.findViewById(R.id.totalPriceText);
        addressText = view.findViewById(R.id.addressText);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String name = bundle.getString("name");
            double total = bundle.getDouble("total");
            String address = bundle.getString("address");

            orderText.setText(name);
            totalPriceText.setText(String.valueOf(total));
            addressText.setText(address);
            product = new Product();

            product.getProductByNameAndByAuthor(company, name, new Product.UserCallbackWithType<List<Map<String, Object>>>() {
                @Override
                public void onSuccess(List<Map<String, Object>> result) {
                    if (!result.isEmpty()) {
                        Map<String, Object> productData = result.get(0);
                        double price = (double) productData.get("price");
                        priceText.setText(String.valueOf(price));
                    } else {
                    }
                }

                @Override
                public void onFailure(Exception e) {
                }
            });
        }

        return view;
    }
}

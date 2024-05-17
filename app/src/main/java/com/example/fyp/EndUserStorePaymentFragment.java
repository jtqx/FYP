package com.example.fyp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

public class EndUserStorePaymentFragment extends Fragment {

    private Map<String, Object> productData;
    private TextView orderText;
    private TextView priceText;
    private double price;
    private TextView deliveryPriceText;
    private double fee;
    private TextView totalPriceText;
    private double total;
    private EditText addressEditText;
    private Button orderButton;
    private String email;
    private String author;
    private String name;
    private String address;
    private String status;

    public EndUserStorePaymentFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_end_user_store_payment, container, false);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        orderText = view.findViewById(R.id.orderText);
        priceText = view.findViewById(R.id.priceText);
        deliveryPriceText = view.findViewById(R.id.deliveryPriceText);
        totalPriceText = view.findViewById(R.id.totalPriceText);
        addressEditText = view.findViewById(R.id.addressEditText);
        orderButton = view.findViewById(R.id.orderButton);
        String delivery = getString(R.string.deliveryFee);
        deliveryPriceText.setText(delivery);
        fee = Double.parseDouble(delivery);
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("productData")) {
            productData = (Map<String, Object>) bundle.getSerializable("productData");
            if (productData != null) {
                name = productData.get("name") != null ? productData.get("name").toString() : "";
                String priceVal = productData.get("price") != null ? productData.get("price").toString() : "";
                author = productData.get("author") != null ? productData.get("author").toString() : "";
                orderText.setText(name);
                priceText.setText(priceVal);
                price = Double.parseDouble(priceVal);
            }
        }
        total = fee + price;
        String totalVal = Double.toString(total);
        totalPriceText.setText(totalVal);
        orderButton.setOnClickListener(v -> onOrderButtonClick());
        return view;
    }
    private void onOrderButtonClick(){
        address = addressEditText.getText().toString().trim();
        if (!address.isEmpty()) {
            addOrder(email, author, name, address, price);
            navigateBack();
        } else {
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }
    private void addOrder(String email,String author, String name, String address, Double total) {
        Order order = new Order();
        status = "pending";
        order.addOrder(email, author, name, address, total, status);
    }
    private void navigateBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
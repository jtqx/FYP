package com.example.fyp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BusinessHomeFragment extends Fragment {
    private RecyclerView currentOrderRecyclerView;
    private BusinessOrderAdapter orderAdapter;
    private List<Map<String, Object>> currentOrders;
    private String email;
    private Button pastButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_home, container, false);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        currentOrderRecyclerView = view.findViewById(R.id.currentOrderRecyclerView);
        pastButton = view.findViewById(R.id.pastButton);
        currentOrderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new BusinessOrderAdapter();
        currentOrderRecyclerView.setAdapter(orderAdapter);
        orderAdapter.setOnItemClickListener(new BusinessOrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Map<String, Object> order) {
                // Handle item click here, such as opening the BusinessOrderDetailsFragment
                BusinessOrderDetailsFragment fragment = new BusinessOrderDetailsFragment();
                // Pass the order data to the fragment using a bundle
                Bundle bundle = new Bundle();
                bundle.putString("orderId", order.get("orderId").toString());
                bundle.putString("name", order.get("name").toString());
                bundle.putDouble("total", (double) order.get("total"));
                bundle.putString("address", order.get("address").toString());
                fragment.setArguments(bundle);

                // Start fragment transaction to replace the current fragment with BusinessOrderDetailsFragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager(); // Use requireActivity().getSupportFragmentManager() if inside a fragment
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.businessFragmentContainerView, fragment);
                transaction.addToBackStack(null); // Optional: Add fragment to back stack
                transaction.commit();
            }
        });
        pastButton.setOnClickListener(v -> {
            // Create an instance of BusinessPastOrderFragment
            BusinessPastOrdersFragment businessPastOrderFragment = new BusinessPastOrdersFragment();

            // Replace the current fragment with BusinessPastOrderFragment
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.businessFragmentContainerView, businessPastOrderFragment);
            transaction.addToBackStack(null);  // Add the transaction to the back stack
            transaction.commit();
        });

// Set the adapter to your RecyclerView
        currentOrderRecyclerView.setAdapter(orderAdapter);

        fetchCurrentOrders();

        return view;
    }

    private void fetchCurrentOrders() {
        Order order = new Order();
        order.getOrdersByAuthor(email, "pending", new Order.UserCallbackWithType<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> orders) {
                currentOrders = orders;
                orderAdapter.setCurrentOrders(currentOrders);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure
            }
        });
    }
}
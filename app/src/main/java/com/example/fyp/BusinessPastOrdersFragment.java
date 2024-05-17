package com.example.fyp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

public class BusinessPastOrdersFragment extends Fragment {
    private RecyclerView recyclerView;
    private BusinessPastOrdersAdapter adapter;
    private String email;
    private String company;
    private List<Map<String, Object>> pastOrdersList;


    public BusinessPastOrdersFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_past_orders, container, false);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        company = sharedPreferences.getString("Company","");
        recyclerView = view.findViewById(R.id.pastOrderRecyclerView);
        adapter = new BusinessPastOrdersAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BusinessOrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Map<String, Object> order) {
                BusinessOrderDetailsFragment fragment = new BusinessOrderDetailsFragment();
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

        fetchPastOrders();

        return view;
    }
    private void fetchPastOrders() {
        Order order = new Order();
        order.getOrdersByAuthorAndStatus(company,  new Order.UserCallbackWithType<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> orders) {
                pastOrdersList = orders;
                adapter.setCurrentOrders(pastOrdersList);
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }
}
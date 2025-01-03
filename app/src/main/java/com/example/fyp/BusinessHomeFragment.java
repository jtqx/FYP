package com.example.fyp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BusinessHomeFragment extends Fragment {
    private RecyclerView currentOrderRecyclerView;
    private BusinessOrderAdapter orderAdapter;
    private List<Map<String, Object>> currentOrders;
    private String email;
    private Button pastButton;
    private String company;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_home, container, false);
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        getCompanyName(email, companyName -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Company", companyName);
            editor.apply();
        });
        company = sharedPreferences.getString("Company","");
        currentOrderRecyclerView = view.findViewById(R.id.currentOrderRecyclerView);
        pastButton = view.findViewById(R.id.pastButton);
        currentOrderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderAdapter = new BusinessOrderAdapter(this);
        currentOrderRecyclerView.setAdapter(orderAdapter);
        orderAdapter.setOnItemClickListener(new BusinessOrderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Map<String, Object> order) {
                BusinessOrderDetailsFragment fragment = new BusinessOrderDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("orderId", order.get("orderId").toString());
                bundle.putString("name", order.get("name").toString());
                bundle.putDouble("total", (double) order.get("total"));
                bundle.putString("address", order.get("address").toString());
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.businessFragmentContainerView, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        pastButton.setOnClickListener(v -> {
            BusinessPastOrdersFragment businessPastOrderFragment = new BusinessPastOrdersFragment();

            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.businessFragmentContainerView, businessPastOrderFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        currentOrderRecyclerView.setAdapter(orderAdapter);

        fetchCurrentOrders();

        return view;
    }

    public void fetchCurrentOrders() {
        Order order = new Order();
        order.getOrdersByAuthor(company, "pending", new Order.UserCallbackWithType<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> orders) {
                currentOrders = orders;
                orderAdapter.setCurrentOrders(currentOrders);
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    private void getCompanyName(String email, CompanyNameCallback callback) {
        Recipe recipe = new Recipe();
        recipe.getCompany(email, new Recipe.companyDetailsCallback() {
            @Override
            public void onSuccess(String company) {
                if (company != null) {
                    callback.onCompanyNameReceived(company);
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

    interface CompanyNameCallback {
        void onCompanyNameReceived(String companyName);
    }
}
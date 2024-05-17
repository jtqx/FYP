package com.example.fyp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BusinessStoreFragment extends Fragment implements BusinessStoreAdapter.OnUpdateClickListener {

    ImageButton addButton;
    private String email;
    private String company;
    private RecyclerView storeRecyclerView;
    private TextView emptyTextView;

    private BusinessStoreAdapter adapter;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_business_store, container, false);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SharedPref", MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        company = sharedPreferences.getString("Company", "");

        addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> openAddProductFragment());

        storeRecyclerView = view.findViewById(R.id.storeRecyclerView);
        emptyTextView = view.findViewById(R.id.emptyTextView);
        searchView = view.findViewById(R.id.searchView);

        adapter = new BusinessStoreAdapter(getContext(), new ArrayList<>());
        adapter.setOnUpdateClickListener(this);
        storeRecyclerView.setAdapter(adapter);
        storeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Log.d("BusinessStoreFragment", "Initializing SearchView and loading products");
        updateList();
        return view;
    }

    private void loadProducts(String query) {
        Product product = new Product();
        if (TextUtils.isEmpty(query)) {
            Log.d("BusinessStoreFragment", "Loading products for company: " + company);
            product.getProductsByAuthor(company, new Product.UserCallbackWithType<List<Map<String, Object>>>() {
                @Override
                public void onSuccess(List<Map<String, Object>> products) {
                    handleProductLoadSuccess(products);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("BusinessStoreFragment", "Error loading products: " + e.getMessage());
                }
            });
        } else {
            Log.d("BusinessStoreFragment", "Searching products with query: " + query);
            product.searchProductByName(company, query, new Product.UserCallbackWithType<List<Map<String, Object>>>() {
                @Override
                public void onSuccess(List<Map<String, Object>> products) {
                    handleProductLoadSuccess(products);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("BusinessStoreFragment", "Error searching products: " + e.getMessage());
                }
            });
        }
    }

    private void handleProductLoadSuccess(List<Map<String, Object>> products) {
        if (products.isEmpty()) {
            storeRecyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText("No current products");
        } else {
            storeRecyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
            adapter.updateData(products);
        }
    }

    private void openAddProductFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        BusinessStoreAddFragment addFragment = new BusinessStoreAddFragment();
        fragmentTransaction.replace(R.id.businessFragmentContainerView, addFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void updateList() {
        loadProducts("");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("BusinessStoreFragment", "Search query changed: " + newText);
                loadProducts(newText); // Load products based on the search query
                return true;
            }
        });
    }

    @Override
    public void onUpdateClick(Map<String, Object> productData) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        BusinessStoreUpdateFragment updateFragment = new BusinessStoreUpdateFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("productData", (Serializable) productData);
        updateFragment.setArguments(bundle);

        fragmentTransaction.replace(R.id.businessFragmentContainerView, updateFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}

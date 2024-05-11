package com.example.fyp;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class EndUserStoreFragment extends Fragment {

    private String email;
    private RecyclerView storeRecyclerView;
    private TextView emptyTextView;
    private RecyclerView tagRecyclerView;
    private RecyclerView priceRecyclerView;
    private RecipeTypeTagAdapter tagAdapter;
    private PriceTagAdapter priceTagAdapter;
    private ImageButton ordersButton;

    private EndUserStoreAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_end_user_store, container, false);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
        storeRecyclerView = view.findViewById(R.id.storeRecyclerView);
        emptyTextView = view.findViewById(R.id.emptyTextView);

        adapter = new EndUserStoreAdapter(getContext(), new ArrayList<>());
        storeRecyclerView.setAdapter(adapter);
        storeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadProducts("");
        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadProducts(newText);
                return true;
            }
        });

        tagRecyclerView = view.findViewById(R.id.tagRecyclerView);
        tagRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        tagAdapter = new RecipeTypeTagAdapter(getContext(), new ArrayList<>(), this::onTagClick);
        tagRecyclerView.setAdapter(tagAdapter);

        priceRecyclerView = view.findViewById(R.id.priceRecyclerView);
        priceRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        priceTagAdapter = new PriceTagAdapter(getContext(), getPriceRanges(), this::onPriceTagClick);
        priceRecyclerView.setAdapter(priceTagAdapter);

        loadProductTypes();

        TextView clearFilterTextView = view.findViewById(R.id.clearFilterTextView);
        clearFilterTextView.setOnClickListener(v -> clearFiltersAndReloadProducts());
        TextView clearPriceTextView = view.findViewById(R.id.clearPriceFilterTextView);
        clearPriceTextView.setOnClickListener(v -> clearFiltersAndReloadProducts());

        return view;
    }

    private List<String> getPriceRanges() {
        List<String> priceRanges = new ArrayList<>();
        priceRanges.add("Under $5");
        priceRanges.add("$5 to $20");
        priceRanges.add("Over $20");
        return priceRanges;
    }
    private void loadProducts(String query) {
        Product product = new Product();
        if (TextUtils.isEmpty(query)) {
            product.getAllProducts(new Product.UserCallbackWithType<List<Map<String, Object>>>() {
                @Override
                public void onSuccess(List<Map<String, Object>> product) {
                    if (product.isEmpty()) {
                        storeRecyclerView.setVisibility(View.GONE);
                        emptyTextView.setVisibility(View.VISIBLE);
                        emptyTextView.setText("No current recipes");
                    } else {
                        storeRecyclerView.setVisibility(View.VISIBLE);
                        emptyTextView.setVisibility(View.GONE);
                        adapter.updateData(product);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("EndUserStoreFragment", "Error loading products: " + e.getMessage());
                }
            });
        } else {
            product.searchProductByNameOrAuthor(query, new Product.UserCallbackWithType<List<Map<String, Object>>>() {
                @Override
                public void onSuccess(List<Map<String, Object>> product) {
                    if (product.isEmpty()) {
                        storeRecyclerView.setVisibility(View.GONE);
                        emptyTextView.setVisibility(View.VISIBLE);
                        emptyTextView.setText("No current products");
                    } else {
                        storeRecyclerView.setVisibility(View.VISIBLE);
                        emptyTextView.setVisibility(View.GONE);
                        adapter.updateData(product);
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e("EndUserStoreFragment", "Error searching products: " + e.getMessage());
                }
            });
        }
    }
    private void loadProductTypes() {
        FirebaseFirestore.getInstance().collection("recipeCategories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> types = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String type = document.getString("type");
                        types.add(type);
                    }
                    tagAdapter.setRecipeTypes(types);
                })
                .addOnFailureListener(e -> {
                });
    }

    private void onTagClick(String type) {
        loadProductByType(type);
    }
    private void onPriceTagClick(String price) {
        String range = mapTagToRange(price);
        loadProductsByRange(range);
    }

    private void loadProductByType(String type) {
        Product product = new Product();
        product.searchProductByType(type, new Product.UserCallbackWithType<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> product) {
                if (product.isEmpty()) {
                    storeRecyclerView.setVisibility(View.GONE);
                    emptyTextView.setVisibility(View.VISIBLE);
                    emptyTextView.setText("No current products");
                } else {
                    storeRecyclerView.setVisibility(View.VISIBLE);
                    emptyTextView.setVisibility(View.GONE);
                    adapter.updateData(product);
                }
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }
    private void loadProductsByRange(String range) {
        Product product = new Product();
        product.searchProductByRange(range, new Product.UserCallbackWithType<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> product) {
                if (product.isEmpty()) {
                    storeRecyclerView.setVisibility(View.GONE);
                    emptyTextView.setVisibility(View.VISIBLE);
                    emptyTextView.setText("No current products");
                } else {
                    storeRecyclerView.setVisibility(View.VISIBLE);
                    emptyTextView.setVisibility(View.GONE);
                    adapter.updateData(product);
                }
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }
    private String mapTagToRange(String tag) {
        switch (tag) {
            case "Under $5":
                return "Low";
            case "$5 to $20":
                return "Medium";
            case "Over $20":
                return "High";
            default:
                return "";
        }
    }
    private void clearFiltersAndReloadProducts() {
        String query = "";
        loadProducts(query);
    }
}
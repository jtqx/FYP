package com.example.fyp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AdminCheckUploadFragment extends Fragment {

    private RecyclerView uploadRecyclerView;
    private UploadAdapter uploadAdapter;
    private Admin admin;
    public AdminCheckUploadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_check_upload, container, false);
        uploadRecyclerView = view.findViewById(R.id.uploadRecyclerView);
        uploadRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        admin = new Admin();
        admin.adminCheck(new Admin.UserCallbackWithType<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> result) {
                uploadAdapter = new UploadAdapter(result, item -> {
                    Fragment fragment;
                    if (item.containsKey("ingredients")) {
                        fragment = new AdminRecipeDetailsFragment();
                    } else {
                        fragment = new AdminStoreDetailsFragment();
                    }

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("item", (Serializable) item);
                    fragment.setArguments(bundle);

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.adminFragmentContainerView, fragment)
                            .addToBackStack(null)
                            .commit();
                });
                uploadRecyclerView.setAdapter(uploadAdapter);
                uploadAdapter.notifyDataSetChanged();
            }


            @Override
            public void onFailure(Exception e) {
            }
        });

        return view;
    }
}
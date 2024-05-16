package com.example.fyp;

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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class AdminAccountConversionFragment extends Fragment{

    private RecyclerView requestRecyclerView;
    private RequestAdapter requestAdapter;
    private Admin admin;
    public AdminAccountConversionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_account_conversion, container, false);
        requestRecyclerView = view.findViewById(R.id.requestRecyclerView);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        admin = new Admin();
        admin.getAllRequests(new Admin.UserCallbackWithType<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> result) {
                if (requestAdapter == null) {
                    requestAdapter = new RequestAdapter(result, item ->{
                        Fragment fragment;
                        fragment = new AdminRequestDetailsFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("item", (Serializable) item);
                        fragment.setArguments(bundle);

                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.adminFragmentContainerView, fragment)
                                .addToBackStack(null)
                                .commit();
                    });
                }
                requestRecyclerView.setAdapter(requestAdapter);
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure
                Log.e("AdminAccountConversion", "Error loading requests: ", e);
            }
        });

        return view;
    }
}

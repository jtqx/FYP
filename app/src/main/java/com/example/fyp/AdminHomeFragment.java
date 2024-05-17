package com.example.fyp;

import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminHomeFragment extends Fragment {

    private SearchView searchView;
    private RecyclerView searchRecyclerView;
    private Admin admin;
    private UserAdapter userAdapter;
    private List<Map<String, Object>> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        searchView = view.findViewById(R.id.searchView);
        searchRecyclerView = view.findViewById(R.id.searchRecyclerView);

        admin = new Admin();
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);

        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchRecyclerView.setAdapter(userAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return false;
            }
        });
        userAdapter.setOnItemClickListener(user -> {
            String userType = (String) user.get("userType");
            if (userType != null) {
                switch (userType) {
                    case "End User":
                        navigateToFragment(new AdminEndUserDetailsFragment(), user);
                        break;
                    case "Business Partner":
                        navigateToFragment(new AdminBusinessDetailsFragment(), user);
                        break;
                    case "Admin":
                        navigateToFragment(new AdminAdminDetailsFragment(), user);
                        break;
                    default:
                        Log.e("AdminHomeFragment", "Unknown user type: " + userType);
                }
            }
        });

        return view;
    }

    private void performSearch(String query) {
        admin.searchUsers(query, new Admin.UserCallbackWithType<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> result) {
                userList.clear();
                userList.addAll(result);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                Log.d("AdminHomeFragment", "Error getting documents: ", e);
            }
        });
    }

    private void navigateToFragment(Fragment fragment, Map<String, Object> user) {
        Bundle args = new Bundle();
        args.putString("email", (String) user.get("email"));
        args.putString("userType", (String) user.get("userType"));
        fragment.setArguments(args);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.adminFragmentContainerView, fragment)
                .addToBackStack(null)
                .commit();
    }
}
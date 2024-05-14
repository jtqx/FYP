package com.example.fyp;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class AdminAddCategoriesFragment extends Fragment {

    private Button addCatButton;
    private EditText addCatEditText;


    public AdminAddCategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_add_categories, container, false);
        addCatButton = view.findViewById(R.id.addCatButton);
        addCatEditText = view.findViewById(R.id.addCatEditText);
        addCatButton.setOnClickListener(v -> createCategory());
        return view;
    }

    private void createCategory() {
        String type = addCatEditText.getText().toString().trim();

        // Validate input
        if (!type.isEmpty()) {
            addCategory(type);
            navigateBack();
        } else {
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }
    private void addCategory(String type) {
        Recipe recipe = new Recipe();
        recipe.addCategory(type);
    }
    private void navigateBack() {
        requireActivity().getSupportFragmentManager().popBackStack();
    }
}
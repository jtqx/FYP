package com.example.fyp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AdminTasksFragment extends Fragment {

    private Button updateCategoriesButton;
    private Button recentUploadButton;
    private Button accountConvertButton;
    private Button updateDietsButton;
    private Button updatePopularMealsButton;
    private Button reactivateAccountButton;

    public AdminTasksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_tasks, container, false);
        updateCategoriesButton = view.findViewById(R.id.updateCategoriesButton);
        recentUploadButton = view.findViewById(R.id.recentUploadButton);
        accountConvertButton = view.findViewById(R.id.accountConvertButton);
        updateDietsButton = view.findViewById(R.id.updateDietsButton);
        updatePopularMealsButton = view.findViewById(R.id.updatePopularMealsButton);
        reactivateAccountButton = view.findViewById(R.id.reactivateAccountButton);
        updateCategoriesButton.setOnClickListener(v -> openAddCatFragment());
        recentUploadButton.setOnClickListener(v -> openCheckUploadFragment());
        accountConvertButton.setOnClickListener(v -> openAccountConvertFragment());
        updateDietsButton.setOnClickListener(v -> openUpdateDietsFragment());
        updatePopularMealsButton.setOnClickListener(v -> openUpdatePopularMealsFragment());
        reactivateAccountButton.setOnClickListener(v -> openReactivateAccountFragment());

        return view;
    }

    private void openAddCatFragment() {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        AdminAddCategoriesFragment adminAddCategoriesFragment = new AdminAddCategoriesFragment();

        fragmentTransaction.replace(R.id.adminFragmentContainerView, adminAddCategoriesFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    private void openCheckUploadFragment(){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        AdminCheckUploadFragment adminCheckUploadFragment = new AdminCheckUploadFragment();

        fragmentTransaction.replace(R.id.adminFragmentContainerView, adminCheckUploadFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    private void openAccountConvertFragment(){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        AdminAccountConversionFragment adminAccountConversionFragment = new AdminAccountConversionFragment();

        fragmentTransaction.replace(R.id.adminFragmentContainerView, adminAccountConversionFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    private void openUpdateDietsFragment(){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        AdminUpdateDietsFragment adminUpdateDietsFragment = new AdminUpdateDietsFragment();

        fragmentTransaction.replace(R.id.adminFragmentContainerView, adminUpdateDietsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openUpdatePopularMealsFragment(){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        AdminUpdatePopularMealsFragment adminUpdatePopularMealsFragment = new AdminUpdatePopularMealsFragment();

        fragmentTransaction.replace(R.id.adminFragmentContainerView, adminUpdatePopularMealsFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void openReactivateAccountFragment(){
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        AdminReactivateAccountFragment adminReactivateAccountFragment = new AdminReactivateAccountFragment();

        fragmentTransaction.replace(R.id.adminFragmentContainerView, adminReactivateAccountFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class BusinessHomeActivity extends AppCompatActivity {

    String email;

    BusinessRecipeFragment BusinessRecipeFragment;
    BusinessHomeFragment BusinessHomeFragment;
    BusinessStoreFragment BusinessStoreFragment;
    BottomNavigationView businessBottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_home);
        BusinessHomeFragment = new BusinessHomeFragment();
        BusinessRecipeFragment = new BusinessRecipeFragment();
        BusinessStoreFragment = new BusinessStoreFragment();

        businessBottomNavigationView = (BottomNavigationView)
                findViewById(R.id.businessBottomNavigationView);
        getSupportFragmentManager().beginTransaction().replace(R.id.businessFragmentContainerView,
                BusinessHomeFragment).commit();

        businessBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.homeItem) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.businessFragmentContainerView,
                            BusinessHomeFragment).commit();
                    return true;
                }else if (itemId == R.id.recipeItem) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.businessFragmentContainerView,
                            BusinessRecipeFragment).commit();
                    return true;
                } else if (itemId == R.id.storeItem) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.businessFragmentContainerView,
                            BusinessStoreFragment).commit();
                    return true;
                } else if (itemId == R.id.accountItem) {
                    return true;
                } else if (itemId == R.id.logOutItem) {
                    return true;
                }
                return false;
            }});

        SharedPreferences sharedPreferences = getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

    }
}
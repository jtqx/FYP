package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class EndUserHomeActivity extends AppCompatActivity {

    String email;
    EndUserHomeFragment endUserHomeFragment;
    EndUserLogFragment endUserLogFragment;
    EndUserRecipeFragment endUserRecipeFragment;
    EndUserAccountFragment endUserAccountFragment;
    BottomNavigationView endUserBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_user_home);
        endUserHomeFragment = new EndUserHomeFragment();
        endUserLogFragment = new EndUserLogFragment();
        endUserRecipeFragment = new EndUserRecipeFragment();
        endUserAccountFragment = new EndUserAccountFragment();
        endUserBottomNavigationView = (BottomNavigationView)
                findViewById(R.id.endUserBottomNavigationView);
        getSupportFragmentManager().beginTransaction().replace(R.id.endUserFragmentContainerView,
                        endUserHomeFragment).commit();

        endUserBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.homeItem) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.endUserFragmentContainerView,
                            endUserHomeFragment).commit();
                    return true;
                } else if (itemId == R.id.logItem) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.endUserFragmentContainerView,
                            endUserLogFragment).commit();
                    return true;
                } else if (itemId == R.id.recipeItem) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.endUserFragmentContainerView,
                            endUserRecipeFragment).commit();
                    return true;
                } else if (itemId == R.id.accountItem) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.endUserFragmentContainerView,
                            endUserAccountFragment).commit();
                    return true;
                } else if (itemId == R.id.logOutItem) {
                    return true;
                }
                return false;
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");
    }
}
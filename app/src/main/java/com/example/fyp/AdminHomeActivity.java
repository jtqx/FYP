package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AdminHomeActivity extends AppCompatActivity {

    String email;
    BottomNavigationView adminBottomNavigationView;
    AdminHomeFragment adminHomeFragment;
    AdminAccountFragment adminAccountFragment;
    AdminTasksFragment adminTasksFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        adminHomeFragment = new AdminHomeFragment();
        adminAccountFragment = new AdminAccountFragment();
        adminTasksFragment = new AdminTasksFragment();

        adminBottomNavigationView = (BottomNavigationView)
                findViewById(R.id.adminBottomNavigationView);
        getSupportFragmentManager().beginTransaction().replace(R.id.adminFragmentContainerView,
                adminHomeFragment).commit();

        adminBottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.homeItem) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.adminFragmentContainerView,
                            adminHomeFragment).commit();
                    return true;
                }else if (itemId == R.id.taskItem) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.adminFragmentContainerView,
                            adminTasksFragment).commit();
                    return true;
                } else if (itemId == R.id.accountItem) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.adminFragmentContainerView,
                            adminAccountFragment).commit();
                    return true;
                }
                return false;
            }});

        SharedPreferences sharedPreferences = getSharedPreferences("SharedPref",
                MODE_PRIVATE);
        email = sharedPreferences.getString("email", "");

    }
}
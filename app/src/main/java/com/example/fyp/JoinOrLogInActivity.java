package com.example.fyp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class JoinOrLogInActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    JoinOrLogInFSA joinOrLogInFSA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_or_log_in);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        viewPager2 = (ViewPager2)findViewById(R.id.viewPager2);
        joinOrLogInFSA = new JoinOrLogInFSA(this);
        viewPager2.setAdapter(joinOrLogInFSA);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
    }

    /*public void onJoinButtonClicked(View view) {
        EditText emailEditText = (EditText)findViewById(R.id.emailEditText);
        String email = emailEditText.getText().toString();
        EditText enterPasswordEditText = (EditText)findViewById(R.id.passwordEditText);
        String password = enterPasswordEditText.getText().toString();
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        boolean joinSuccess = dbHelper.createUser(email, password);
        if (!joinSuccess) {
            Toast.makeText(this,
                    "An account with this email address already exists!",
                    Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, JoinSuccessActivity.class);
            startActivity(intent);
            finish();
        }
    }*/
}
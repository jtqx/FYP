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
}
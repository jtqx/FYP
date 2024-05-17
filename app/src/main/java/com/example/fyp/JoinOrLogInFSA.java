package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class JoinOrLogInFSA extends FragmentStateAdapter {
    public JoinOrLogInFSA(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new LogInFragment();
            default:
                return new JoinFragment();
        }
    }

    @Override
    public int getItemCount() {
        // Number of tabs
        return 1;
    }
}

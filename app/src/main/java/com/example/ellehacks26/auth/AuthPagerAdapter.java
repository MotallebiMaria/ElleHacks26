package com.example.ellehacks26.auth;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ellehacks26.auth.LoginFragment;
import com.example.ellehacks26.auth.RegisterFragment;
import com.example.ellehacks26.utils.Config;

public class AuthPagerAdapter extends FragmentStateAdapter {

    public AuthPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return LoginFragment.newInstance();
        else return RegisterFragment.newInstance();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
package com.example.ellehacks26.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ellehacks26.BaseActivity;
import com.example.ellehacks26.HomeActivity;
import com.example.ellehacks26.R;
import com.example.ellehacks26.models.User;
import com.example.ellehacks26.utils.Config;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AuthActivity extends BaseActivity implements AuthContract.View {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private AuthPresenter presenter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        presenter = new AuthPresenter(this);
        initializeViews();
        setupViewPager();
    }

    private void initializeViews() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupViewPager() {
        AuthPagerAdapter adapter = new AuthPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(position == 0 ? "Login" : "Register")
        ).attach();
    }

    @Override
    public AuthPresenter getPresenter() {
        return presenter;
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToHome(String userId) {
        Intent intent = new Intent(AuthActivity.this, HomeActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }

    public void navigateToHomeWithUser(User user) {
        String userId = user.getUserId();
        Intent intent = new Intent(AuthActivity.this, HomeActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
        finish();
    }

    @Override
    public void setLoading(boolean isLoading) {
        runOnUiThread(() -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                // disable interactive elements during loading
                viewPager.setUserInputEnabled(false); // swiping
            } else {
                progressBar.setVisibility(View.GONE);
                // re-enable interactive elements
                viewPager.setUserInputEnabled(true); // swiping
            }
        });
    }
}

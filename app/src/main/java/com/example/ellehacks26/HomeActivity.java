package com.example.ellehacks26;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends BaseActivity {
    private Fragment dashboardFragment;
    private Fragment stocksFragment;
    private Fragment creditCardFragment;
    private Fragment lessonFragment;
    protected BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = findViewById(R.id.NavBottom);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnItemSelectedListener(this::fragmentChange);
        initializeFragments();
        setCurrentFragment(dashboardFragment);
    }

    protected boolean fragmentChange(MenuItem item) {
        if (item.getItemId() == R.id.home) {
            setCurrentFragment(dashboardFragment);
            return true;
        }
//        else if (item.getItemId() == R.id.stocks) {
//            setCurrentFragment(stocksFragment);
//            return true;
//        }
        else if (item.getItemId() == R.id.credit) {
            setCurrentFragment(creditCardFragment);
            return true;
        }
        else if (item.getItemId() == R.id.job) {
            setCurrentFragment(lessonFragment);
            return true;
        }
        return false;
    }

    protected void initializeFragments() {
        dashboardFragment = new DashboardFragment();
//        stocksFragment = new StocksFragment();
        creditCardFragment = new CreditCardFragment();
        lessonFragment = new LessonFragment();
    }

    public void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.changeFragments, fragment)
                .commit();
    }
}
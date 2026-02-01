package com.example.ellehacks26;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ellehacks26.models.User;
import com.example.ellehacks26.utils.UserManager;

public class DashboardFragment extends Fragment {

    private static final String ARG_USER_ID = "user_id";

    private String userId;
    private User user;
    private TextView textGreeting;
    private ProgressBar progressEnergy;
    private TextView textEnergyValue;
    private TextView textMoneyValue;
    private ProgressBar progressCredit;
    private TextView textCreditValue;

    public static DashboardFragment newInstance(String userId) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(ARG_USER_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textGreeting = view.findViewById(R.id.textGreeting);
        progressEnergy = view.findViewById(R.id.progressEnergy);
        textEnergyValue = view.findViewById(R.id.textEnergyValue);
        textMoneyValue = view.findViewById(R.id.textMoneyValue);
        progressCredit = view.findViewById(R.id.progressCredit);
        textCreditValue = view.findViewById(R.id.textCreditValue);

        loadUserData();
    }

    private void loadUserData() {
        if (userId == null) return;

        UserManager userManager = new UserManager();
        userManager.getUserById(userId, new UserManager.UserCallback() {
            @Override
            public void onUserLoaded(User loadedUser) {
                user = loadedUser;
                updateUI();
            }

            @Override
            public void onError(String error) {
                textGreeting.setText("Hello User");
                progressEnergy.setProgress(0);
                textEnergyValue.setText("0%");
                textMoneyValue.setText("$0.00");
                progressCredit.setProgress(0);
                textCreditValue.setText("$0 / $1000");
            }
        });
    }

    private void updateUI() {
        if (user == null) return;

        // Set greeting
        String displayName = user.getDisplayName();
        if (displayName != null && !displayName.isEmpty()) {
            textGreeting.setText("Hello " + displayName);
        } else {
            textGreeting.setText("Hello User");
        }

        // set energy (0-100)
        int energy = user.getEnergy();
        progressEnergy.setProgress(energy);
        textEnergyValue.setText(energy + "%");

        // set money
        double money = user.getMoney();
        textMoneyValue.setText(String.format("$%.2f", money));

        // set credit/debt (assume $1000 credit limit)
        double debtAmount = user.getDebtAmount();
        final double CREDIT_LIMIT = 1000.0;
        double creditUsedPercentage = (debtAmount / CREDIT_LIMIT) * 100;

        // cap at 100%
        int progressValue = (int) Math.min(creditUsedPercentage, 100);
        progressCredit.setProgress(progressValue);
        textCreditValue.setText(String.format("$%.0f / $%.0f", debtAmount, CREDIT_LIMIT));
    }

    @Override
    public void onResume() {
        super.onResume();
        // refresh user data when fragment resumes
        if (userId != null) {
            loadUserData();
        }
    }
}
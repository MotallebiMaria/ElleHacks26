package com.example.ellehacks26.auth;

public interface AuthContract {
    interface View {
        void showError(String message);
        void showSuccess(String message);
        void navigateToHome(String userId);
        void setLoading(boolean isLoading);
        Presenter getPresenter();
    }

    interface Presenter {
        void attemptLogin(String email, String password);
        void attemptRegistration(String email, String password, String confirmPassword);
    }
}
package com.example.ellehacks26.auth;

import android.util.Log;
import android.util.Patterns;

import com.example.ellehacks26.models.User;
import com.example.ellehacks26.utils.StoryManager;
import com.example.ellehacks26.utils.UserManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.regex.Pattern;

public class AuthPresenter implements AuthContract.Presenter {
    private final AuthContract.View view;
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private final Pattern emailPattern;
    private final StoryManager storyManager;

    public AuthPresenter(AuthContract.View view) {
        this(view, FirebaseAuth.getInstance(), FirebaseFirestore.getInstance());
    }

    public AuthPresenter(AuthContract.View view, FirebaseAuth auth, FirebaseFirestore firestore) {
        this(view, auth, firestore, Patterns.EMAIL_ADDRESS);
    }

    public AuthPresenter(AuthContract.View view, FirebaseAuth auth, FirebaseFirestore firestore, Pattern emailPattern) {
        this.view = view;
        this.auth = auth;
        this.db = firestore;
        this.emailPattern = emailPattern;
        this.storyManager = StoryManager.getInstance();
    }

    @Override
    public void attemptLogin(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            view.showError("Please fill all fields");
            return;
        }

        email = email + "@user.com";

        view.setLoading(true);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    view.setLoading(false);
                    if (task.isSuccessful()) {
                        String userId = Objects.requireNonNull(task.getResult().getUser()).getUid();
                        loadUserAndNavigate(userId);
                    } else {
                        view.showError("Login failed. Please try again.");
                        Log.d("AuthPresenter","Login failed: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    @Override
    public void attemptRegistration(String email, String password, String confirmPassword) {
        if (!validateRegistration(email, password, confirmPassword)) {
            return;
        }

        final String actualEmail = email + "@user.com";

        view.setLoading(true);
        auth.createUserWithEmailAndPassword(actualEmail, password)
                .addOnCompleteListener(task -> {
                    view.setLoading(false);
                    if (task.isSuccessful()) {
                        String userId = Objects.requireNonNull(task.getResult().getUser()).getUid();
                        saveUserToFirestore(userId, actualEmail);
                    } else {
                        view.showError("Registration failed: " + Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    private boolean validateRegistration(String email, String password, String confirmPassword) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            view.showError("Please fill all required fields");
            return false;
        }

        if (emailPattern == null) {
            view.showError("Please enter a username");
            return false;
        }

        if (password.length() < 6) {
            view.showError("Password must be at least 6 characters");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            view.showError("Passwords do not match");
            return false;
        }

        return true;
    }

    private void saveUserToFirestore(String userId, String email) {
        User user = createUserObject(userId, email);

        db.collection("users").document(userId)
                .set(user.toMap())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        storyManager.initializeUserStory(userId, new StoryManager.StoryCallback() {
                            @Override
                            public void onSuccess() {
                                view.showSuccess("Registration successful");
                                loadUserAndNavigate(userId);
                            }

                            @Override
                            public void onError(String error) {
                                Log.w("AuthPresenter", "Failed to initialize story: " + error);
                                view.showSuccess("Registration successful (story setup incomplete)");
                                loadUserAndNavigate(userId);
                            }
                        });
                    } else {
                        view.showError("Failed to save user data");
                    }
                });
    }

    private User createUserObject(String userId, String email) {
        User user = new User(email);
        user.setUserId(userId);
        return user;
    }

    private void loadUserAndNavigate(String userId) {
        UserManager userManager = new UserManager();
        userManager.getUserById(userId, new UserManager.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                if (view instanceof com.example.ellehacks26.auth.AuthActivity) {
                    ((com.example.ellehacks26.auth.AuthActivity) view).navigateToHomeWithUser(user);
                } else {
                    view.navigateToHome(user.getUserId());
                }
            }

            @Override
            public void onError(String errorMessage) {
                view.showError("Error loading user: " + errorMessage);
            }
        });
    }
}
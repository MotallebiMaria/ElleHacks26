package com.example.ellehacks26.utils;

import com.example.ellehacks26.models.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

public class UserManager {
    private final FirebaseFirestore db;

    public UserManager() {
        db = FirebaseFirestore.getInstance();
    }

    public interface UserCallback {
        void onUserLoaded(User user);
        void onError(String error);
    }

    public void getUserById(String userId, UserCallback callback) {
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            User user = parseUserFromSnapshot(document);
                            if (user != null) {
                                callback.onUserLoaded(user);
                            } else {
                                callback.onError("Failed to parse user data");
                            }
                        } else {
                            callback.onError("User not found");
                        }
                    } else {
                        callback.onError(Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    private User parseUserFromSnapshot(DocumentSnapshot document) {
        Map<String, Object> data = document.getData();
        if (data == null) return null;

        String userId = document.getId();

        User user = User.fromMap(data);
        user.setUserId(userId);

        return user;
    }
}
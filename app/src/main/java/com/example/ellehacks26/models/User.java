package com.example.ellehacks26.models;

import java.util.HashMap;
import java.util.Map;

public class User {
    protected String userId;
    protected String email;
    protected String displayName;

    public User(String email) {
        this.email = email;
    }

    // getter & setter methods
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("email", email);
        map.put("displayName", displayName);
        return map;
    }

    public static User fromMap(Map<String, Object> map) {
        String email = (String) map.get("email");

        User user = new User(email);
        user.setUserId((String) map.get("userId"));

        if (map.get("displayName") != null) {
            user.setDisplayName((String) map.get("displayName"));
        }

        return user;
    }
}
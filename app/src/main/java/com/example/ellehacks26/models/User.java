package com.example.ellehacks26.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class User {
    protected String userId;
    protected String email;
    protected String displayName;
    protected double money;  // dollars in debit/savings
    protected int energy;    // 0-100
    protected int currentStoryNumber;
    protected double debtAmount;  // credit card debt
    protected Set<String> completedLessons;  // lesson IDs that user has completed
    protected StoryEntry currentStory;

    // default constructor for Firestore
    public User() {
        this.completedLessons = new HashSet<>();
        this.money = 10.0;  // starting money
        this.energy = 100;   // starting energy
        this.currentStoryNumber = 1;
        this.debtAmount = 0.0;
        this.currentStory = null;
    }

    public User(String email) {
        this();
        this.email = email;
    }

    // getter & setter methods
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public StoryEntry getCurrentStory() { return currentStory; }
    public void setCurrentStory(StoryEntry currentStory) { this.currentStory = currentStory; }

    public double getMoney() { return money; }
    public void setMoney(double money) { this.money = money; }

    public int getEnergy() { return energy; }
    public void setEnergy(int energy) {
        this.energy = Math.min(100, Math.max(0, energy));
    }

    public int getCurrentStoryNumber() { return currentStoryNumber; }
    public void setCurrentStoryNumber(int currentStoryNumber) {
        this.currentStoryNumber = currentStoryNumber;
    }

    public double getDebtAmount() { return debtAmount; }
    public void setDebtAmount(double debtAmount) { this.debtAmount = debtAmount; }

    public Set<String> getCompletedLessons() { return completedLessons; }
    public void setCompletedLessons(Set<String> completedLessons) {
        this.completedLessons = completedLessons;
    }

    public void addCompletedLesson(String lessonId) {
        if (completedLessons == null) {
            completedLessons = new HashSet<>();
        }
        completedLessons.add(lessonId);
    }

    public boolean hasCompletedLesson(String lessonId) {
        return completedLessons != null && completedLessons.contains(lessonId);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("email", email);
        map.put("displayName", displayName);
        map.put("money", money);
        map.put("energy", energy);
        map.put("currentStoryNumber", currentStoryNumber);
        map.put("debtAmount", debtAmount);
        map.put("completedLessons", completedLessons != null ? new ArrayList<>(completedLessons) : new ArrayList<>());

        if (currentStory != null) map.put("currentStory", currentStory.toMap());
        else map.put("currentStory", null);

        return map;
    }

    public static User fromMap(Map<String, Object> map) {
        String email = (String) map.get("email");
        User user = new User(email);

        user.setUserId((String) map.get("userId"));
        user.setDisplayName((String) map.get("displayName"));

        Object moneyObj = map.get("money");
        if (moneyObj != null) {
            if (moneyObj instanceof Double) {
                user.setMoney((Double) moneyObj);
            } else if (moneyObj instanceof Long) {
                user.setMoney(((Long) moneyObj).doubleValue());
            }
        }

        Object energyObj = map.get("energy");
        if (energyObj != null) {
            if (energyObj instanceof Long) {
                user.setEnergy(((Long) energyObj).intValue());
            } else if (energyObj instanceof Integer) {
                user.setEnergy((Integer) energyObj);
            }
        }

        Object storyObj = map.get("currentStoryNumber");
        if (storyObj != null) {
            if (storyObj instanceof Long) {
                user.setCurrentStoryNumber(((Long) storyObj).intValue());
            } else if (storyObj instanceof Integer) {
                user.setCurrentStoryNumber((Integer) storyObj);
            }
        }

        Object debtObj = map.get("debtAmount");
        if (debtObj != null) {
            if (debtObj instanceof Double) {
                user.setDebtAmount((Double) debtObj);
            } else if (debtObj instanceof Long) {
                user.setDebtAmount(((Long) debtObj).doubleValue());
            }
        }

        Object lessonsObj = map.get("completedLessons");
        if (lessonsObj instanceof List) {
            Set<String> lessonsSet = new HashSet<>((List<String>) lessonsObj);
            user.setCompletedLessons(lessonsSet);
        }

        Object currentStoryObj = map.get("currentStory");
        if (currentStoryObj instanceof Map) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> storyMap = (Map<String, Object>) currentStoryObj;
                StoryEntry storyEntry = StoryEntry.fromMap(storyMap);
                user.setCurrentStory(storyEntry);
            } catch (Exception e) {
                // log error but don't crash
                e.printStackTrace();
            }
        }

        return user;
    }
}
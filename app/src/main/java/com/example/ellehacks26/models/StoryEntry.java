package com.example.ellehacks26.models;

import com.google.firebase.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class StoryEntry {
    private String entryId;
    private String userId;
    private String scenario;  // AI-generated scenario text
    private String userChoice;  // choice made by user
    private String result;  // Result/consequence text
    private double moneyChange;
    private int energyChange;
    private double debtChange;
    private Timestamp timestamp;
    private int storyNumber;

    public StoryEntry() {
        this.timestamp = Timestamp.now();
    }

    public StoryEntry(String userId, String scenario, String userChoice, String result,
                      double moneyChange, int energyChange, double debtChange, int storyNumber) {
        this();
        this.userId = userId;
        this.scenario = scenario;
        this.userChoice = userChoice;
        this.result = result;
        this.moneyChange = moneyChange;
        this.energyChange = energyChange;
        this.debtChange = debtChange;
        this.storyNumber = storyNumber;
    }

    // Getters and Setters
    public String getEntryId() { return entryId; }
    public void setEntryId(String entryId) { this.entryId = entryId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }

    public String getUserChoice() { return userChoice; }
    public void setUserChoice(String userChoice) { this.userChoice = userChoice; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public double getMoneyChange() { return moneyChange; }
    public void setMoneyChange(double moneyChange) { this.moneyChange = moneyChange; }

    public int getEnergyChange() { return energyChange; }
    public void setEnergyChange(int energyChange) { this.energyChange = energyChange; }

    public double getDebtChange() { return debtChange; }
    public void setDebtChange(double debtChange) { this.debtChange = debtChange; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public int getStoryNumber() { return storyNumber; }
    public void setStoryNumber(int storyNumber) { this.storyNumber = storyNumber; }

    // Convert to Firestore Map
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("scenario", scenario);
        map.put("userChoice", userChoice);
        map.put("result", result);
        map.put("moneyChange", moneyChange);
        map.put("energyChange", energyChange);
        map.put("debtChange", debtChange);
        map.put("timestamp", timestamp);
        map.put("storyNumber", storyNumber);
        return map;
    }

    // Create from Firestore Map
    public static StoryEntry fromMap(Map<String, Object> map) {
        StoryEntry entry = new StoryEntry();

        entry.setUserId((String) map.get("userId"));
        entry.setScenario((String) map.get("scenario"));
        entry.setUserChoice((String) map.get("userChoice"));
        entry.setResult((String) map.get("result"));
        entry.setStoryNumber(((Long) map.get("storyNumber")).intValue());

        // Handle numeric fields
        Object moneyChangeObj = map.get("moneyChange");
        if (moneyChangeObj != null) {
            if (moneyChangeObj instanceof Double) {
                entry.setMoneyChange((Double) moneyChangeObj);
            } else if (moneyChangeObj instanceof Long) {
                entry.setMoneyChange(((Long) moneyChangeObj).doubleValue());
            }
        }

        Object energyChangeObj = map.get("energyChange");
        if (energyChangeObj != null) {
            if (energyChangeObj instanceof Long) {
                entry.setEnergyChange(((Long) energyChangeObj).intValue());
            } else if (energyChangeObj instanceof Integer) {
                entry.setEnergyChange((Integer) energyChangeObj);
            }
        }

        Object debtChangeObj = map.get("debtChange");
        if (debtChangeObj != null) {
            if (debtChangeObj instanceof Double) {
                entry.setDebtChange((Double) debtChangeObj);
            } else if (debtChangeObj instanceof Long) {
                entry.setDebtChange(((Long) debtChangeObj).doubleValue());
            }
        }

        entry.setTimestamp((Timestamp) map.get("timestamp"));

        return entry;
    }
}
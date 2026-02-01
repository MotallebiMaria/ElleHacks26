package com.example.ellehacks26.utils;

public class DatabaseStructure {
    // Collections
    public static final String COLLECTION_USERS = "users";
    public static final String COLLECTION_LESSONS = "lessons";

    // User fields
    public static final String USER_ID = "userId";
    public static final String USER_EMAIL = "email";
    public static final String USER_DISPLAY_NAME = "displayName";
    public static final String USER_MONEY = "money";
    public static final String USER_ENERGY = "energy";
    public static final String USER_CURRENT_STORY = "currentStoryNumber";
    public static final String USER_DEBT = "debtAmount";
    public static final String USER_COMPLETED_LESSONS = "completedLessons";

    // Subcollections
    public static final String SUBCOLLECTION_STORY_ENTRIES = "storyEntries";

    // Story entry fields
    public static final String STORY_ENTRY_SCENARIO = "scenario";
    public static final String STORY_ENTRY_CHOICE = "userChoice";
    public static final String STORY_ENTRY_RESULT = "result";
    public static final String STORY_ENTRY_MONEY_CHANGE = "moneyChange";
    public static final String STORY_ENTRY_ENERGY_CHANGE = "energyChange";
    public static final String STORY_ENTRY_DEBT_CHANGE = "debtChange";
    public static final String STORY_ENTRY_TIMESTAMP = "timestamp";
    public static final String STORY_ENTRY_NUMBER = "storyNumber";

    // Default values
    public static final double DEFAULT_MONEY = 100.0;
    public static final int DEFAULT_ENERGY = 100;
    public static final int DEFAULT_STORY_NUMBER = 1;
    public static final double DEFAULT_DEBT = 0.0;

    // Energy bounds
    public static final int MIN_ENERGY = 0;
    public static final int MAX_ENERGY = 100;
}
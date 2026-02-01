package com.example.ellehacks26.models;

import java.util.List;

public class StoryScenario {
    private String title;
    private String scenario;
    private List<com.example.ellehacks26.models.StoryChoice> choices;

    public StoryScenario() {}

    public StoryScenario(String title, String scenario, List<com.example.ellehacks26.models.StoryChoice> choices) {
        this.title = title;
        this.scenario = scenario;
        this.choices = choices;
    }

    // Getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getScenario() { return scenario; }
    public void setScenario(String scenario) { this.scenario = scenario; }

    public List<com.example.ellehacks26.models.StoryChoice> getChoices() { return choices; }
    public void setChoices(List<com.example.ellehacks26.models.StoryChoice> choices) { this.choices = choices; }
}
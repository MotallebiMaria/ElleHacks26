package com.example.ellehacks26.models;

public class StoryChoice {
    private String text;
    private double moneyChange;
    private int energyChange;
    private double debtChange;
    private String outcome;
    private String lesson;

    public StoryChoice() {}

    public StoryChoice(String text, double moneyChange, int energyChange,
                       double debtChange, String outcome, String lesson) {
        this.text = text;
        this.moneyChange = moneyChange;
        this.energyChange = energyChange;
        this.debtChange = debtChange;
        this.outcome = outcome;
        this.lesson = lesson;
    }

    // Getters and setters
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public double getMoneyChange() { return moneyChange; }
    public void setMoneyChange(double moneyChange) { this.moneyChange = moneyChange; }

    public int getEnergyChange() { return energyChange; }
    public void setEnergyChange(int energyChange) { this.energyChange = energyChange; }

    public double getDebtChange() { return debtChange; }
    public void setDebtChange(double debtChange) { this.debtChange = debtChange; }

    public String getOutcome() { return outcome; }
    public void setOutcome(String outcome) { this.outcome = outcome; }

    public String getLesson() { return lesson; }
    public void setLesson(String lesson) { this.lesson = lesson; }
}
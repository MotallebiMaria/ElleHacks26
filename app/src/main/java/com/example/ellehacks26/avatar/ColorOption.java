package com.example.ellehacks26.avatar;

public class ColorOption {
    private final String id;
    private final String hexColor;
    private final String displayName;

    public ColorOption(String id, String hexColor, String displayName) {
        this.id = id;
        this.hexColor = hexColor;
        this.displayName = displayName;
    }

    public String getId() { return id; }
    public String getHexColor() { return hexColor; }
    public String getDisplayName() { return displayName; }
}
package com.example.ellehacks26.avatar;

public class HairStyle {
    private final String id;
    private final String displayName;
    private final int previewDrawableId;
    private final String avatarDrawableName;

    public HairStyle(String id, String displayName, int previewDrawableId, String avatarDrawableName) {
        this.id = id;
        this.displayName = displayName;
        this.previewDrawableId = previewDrawableId;
        this.avatarDrawableName = avatarDrawableName;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public int getPreviewDrawableId() { return previewDrawableId; }
    public String getAvatarDrawableName() { return avatarDrawableName; }
}
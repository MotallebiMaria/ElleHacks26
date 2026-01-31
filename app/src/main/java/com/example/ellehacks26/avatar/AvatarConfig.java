package com.example.ellehacks26.avatar;

import android.os.Parcel;
import android.os.Parcelable;

public class AvatarConfig implements Parcelable {
    private String skinColor;
    private String hairStyle;
    private String hairColor;
    private String accessory;
    private String eyeColor;

    public AvatarConfig() {
        this.skinColor = "#FFD2B48C";
        this.hairStyle = "hair_short";
        this.hairColor = "#FF000000";
        this.accessory = "accessory_none";
        this.eyeColor = "#FF000000";
    }

    protected AvatarConfig(Parcel in) {
        skinColor = in.readString();
        hairStyle = in.readString();
        hairColor = in.readString();
        accessory = in.readString();
        eyeColor = in.readString();
    }

    public static final Creator<AvatarConfig> CREATOR = new Creator<AvatarConfig>() {
        @Override
        public AvatarConfig createFromParcel(Parcel in) {
            return new AvatarConfig(in);
        }

        @Override
        public AvatarConfig[] newArray(int size) {
            return new AvatarConfig[size];
        }
    };

    public String getSkinColor() { return skinColor; }
    public void setSkinColor(String skinColor) { this.skinColor = skinColor; }

    public String getHairStyle() { return hairStyle; }
    public void setHairStyle(String hairStyle) { this.hairStyle = hairStyle; }

    public String getHairColor() { return hairColor; }
    public void setHairColor(String hairColor) { this.hairColor = hairColor; }

    public String getAccessory() { return accessory; }
    public void setAccessory(String accessory) { this.accessory = accessory; }

    public String getEyeColor() { return eyeColor; }
    public void setEyeColor(String eyeColor) { this.eyeColor = eyeColor; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(skinColor);
        dest.writeString(hairStyle);
        dest.writeString(hairColor);
        dest.writeString(accessory);
        dest.writeString(eyeColor);
    }
}
package com.example.ellehacks26.avatar;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;

public class AvatarRepository {
    private static final String PREF_NAME = "avatar_prefs";
    private static final String KEY_AVATAR_CONFIG = "avatar_config";

    private final SharedPreferences prefs;
    private final Gson gson;

    public AvatarRepository(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveAvatarConfig(AvatarConfig config) {
        String json = gson.toJson(config);
        prefs.edit().putString(KEY_AVATAR_CONFIG, json).apply();
    }

    public AvatarConfig loadAvatarConfig() {
        String json = prefs.getString(KEY_AVATAR_CONFIG, null);
        if (json != null) {
            return gson.fromJson(json, AvatarConfig.class);
        }
        return new AvatarConfig(); // Return default
    }

    public void clearAvatarConfig() {
        prefs.edit().remove(KEY_AVATAR_CONFIG).apply();
    }
}
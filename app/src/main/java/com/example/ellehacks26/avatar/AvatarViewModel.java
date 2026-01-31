package com.example.ellehacks26.avatar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AvatarViewModel extends ViewModel {
    private final MutableLiveData<AvatarConfig> avatarConfigLiveData =
            new MutableLiveData<>();
    private AvatarRepository repository;

    public void init(AvatarRepository repository) {
        this.repository = repository;
        loadAvatar();
    }

    public LiveData<AvatarConfig> getAvatarConfig() {
        return avatarConfigLiveData;
    }

    public void updateAvatarConfig(AvatarConfig config) {
        avatarConfigLiveData.setValue(config);
        if (repository != null) {
            repository.saveAvatarConfig(config);
        }
    }

    public void updateSkinColor(String colorHex) {
        AvatarConfig current = avatarConfigLiveData.getValue();
        if (current != null) {
            current.setSkinColor(colorHex);
            updateAvatarConfig(current);
        }
    }

    public void updateHairStyle(String style) {
        AvatarConfig current = avatarConfigLiveData.getValue();
        if (current != null) {
            current.setHairStyle(style);
            updateAvatarConfig(current);
        }
    }

    public void updateHairColor(String colorHex) {
        AvatarConfig current = avatarConfigLiveData.getValue();
        if (current != null) {
            current.setHairColor(colorHex);
            updateAvatarConfig(current);
        }
    }

    public void updateAccessory(String accessory) {
        AvatarConfig current = avatarConfigLiveData.getValue();
        if (current != null) {
            current.setAccessory(accessory);
            updateAvatarConfig(current);
        }
    }

    private void loadAvatar() {
        if (repository != null) {
            avatarConfigLiveData.setValue(repository.loadAvatarConfig());
        }
    }
}
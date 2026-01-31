package com.example.ellehacks26;

import android.content.Intent;
import android.os.Bundle;

import com.example.ellehacks26.utils.Config;

// this class handles everything that needs to be done as soon as the app starts
public class StartupActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, Config.STARTING_PAGE));
        finish();
    }
}
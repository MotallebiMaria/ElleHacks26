package com.example.ellehacks26;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // potential theme/style handling goes here ahhhhh
        setTheme(R.style.BaseTheme);
        super.onCreate(savedInstanceState);
    }

}
package com.example.ellehacks26.lessons;

import com.example.ellehacks26.BuildConfig;
import com.example.ellehacks26.R;

public class StocksQuizFragment extends BaseQuizFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.layout_stocks_quiz;
    }

    @Override
    protected String getApiKey() {
        return BuildConfig.GEMINI_API_KEY;
    }
}
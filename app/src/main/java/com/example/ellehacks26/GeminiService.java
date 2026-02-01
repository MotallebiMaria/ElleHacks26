package com.example.ellehacks26;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GeminiService {
    private static final String TAG = "GeminiService";
    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";

    private final String apiKey;
    private final OkHttpClient client;
    private final Gson gson;

    public GeminiService(String apiKey) {
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    public interface GeminiCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public void generateStoryScenario(int storyNumber, String userContext, GeminiCallback callback) {
        // Get user stats from SharedPreferences or wherever you store them
        // For now, we'll create a simple prompt

        String prompt = createStoryPrompt(storyNumber, userContext);

        // Create request body
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", prompt);

        content.put("parts", new Object[]{part});
        requestBody.put("contents", new Object[]{content});

        String jsonBody = gson.toJson(requestBody);

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json")
        );

        String url = BASE_URL + "?key=" + apiKey;

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "API call failed: " + e.getMessage());
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    String generatedText = parseGeminiResponse(responseBody);
                    callback.onSuccess(generatedText);
                } else {
                    Log.e(TAG, "API error: " + response.code() + " - " + response.message());
                    callback.onError("API error: " + response.code());
                }
            }
        });
    }

    private String createStoryPrompt(int storyNumber, String userContext) {
        return "Generate a financial literacy story scenario for kids aged 10-14. " +
                "Story should include:\n" +
                "1. A creative, engaging scenario about money, savings, investment, or financial decisions\n" +
                "2. 4 different choices the user can make\n" +
                "3. For each choice, include:\n" +
                "   - Money change (positive or negative number)\n" +
                "   - Energy change (positive or negative number, energy ranges 0-100)\n" +
                "   - Debt change (if applicable)\n" +
                "   - A detailed outcome description\n" +
                "   - A financial lesson learned\n\n" +
                "Format the response as a JSON object with this exact structure:\n" +
                "{\n" +
                "  \"title\": \"Story Title\",\n" +
                "  \"scenario\": \"Detailed scenario description...\",\n" +
                "  \"choices\": [\n" +
                "    {\n" +
                "      \"text\": \"Choice 1 description\",\n" +
                "      \"moneyChange\": -2.0,\n" +
                "      \"energyChange\": -10,\n" +
                "      \"debtChange\": 0.0,\n" +
                "      \"outcome\": \"What happens if you choose this...\",\n" +
                "      \"lesson\": \"Financial lesson learned\"\n" +
                "    },\n" +
                "    // ... 3 more choices\n" +
                "  ]\n" +
                "}\n\n" +
                "Current story number: " + storyNumber + "\n" +
                "User context: " + userContext + "\n" +
                "Make it educational but fun! Focus on financial concepts like saving, investing, debt, opportunity cost, etc.";
    }

    private String parseGeminiResponse(String response) {
        try {
            JsonObject jsonResponse = JsonParser.parseString(response).getAsJsonObject();
            JsonObject candidates = jsonResponse.getAsJsonArray("candidates").get(0).getAsJsonObject();
            JsonObject content = candidates.getAsJsonObject("content");
            String text = content.getAsJsonArray("parts").get(0).getAsJsonObject().get("text").getAsString();

            // Clean up the response - sometimes Gemini adds markdown or extra text
            text = text.replace("```json", "").replace("```", "").trim();

            return text;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing response: " + e.getMessage());
            return "{\"error\": \"Failed to parse response\"}";
        }
    }
}
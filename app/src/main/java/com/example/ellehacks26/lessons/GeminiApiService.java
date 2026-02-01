package com.example.ellehacks26.lessons;

import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GeminiApiService {
    private static final String TAG = "GeminiApiService";
    // UPDATED: Use the latest model names
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    // Alternative models if flash doesn't work:

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private static GeminiApiService instance;
    private final OkHttpClient client;
    private final Gson gson;

    public interface QuestionCallback {
        void onQuestionGenerated(String question, String correctAnswer, String[] incorrectAnswers);
        void onError(String errorMessage);
    }

    private GeminiApiService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }

    public static synchronized GeminiApiService getInstance() {
        if (instance == null) {
            instance = new GeminiApiService();
        }
        return instance;
    }

    public void generateQuestion(String topic, String lessonContent, String apiKey, QuestionCallback callback) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.contains("null") || apiKey.contains("YOUR_API_KEY")) {
            callback.onError("API key is not configured. Check your local.properties file.");
            Log.e(TAG, "Invalid API Key: " + (apiKey != null ? apiKey.substring(0, Math.min(apiKey.length(), 5)) : "null"));
            return;
        }

        Log.d(TAG, "API Key exists: " + (apiKey.length() > 0));

        // Create a better prompt for financial literacy questions
        String prompt = String.format(
                "Create a kid-friendly multiple-choice question about %s.\n" +
                        "Lesson: %s\n" +
                        "Return only JSON: {\"question\": \"text\", \"correct_answer\": \"text\", \"incorrect_answers\": [\"text1\",\"text2\",\"text3\"]}",
                topic, lessonContent
        );

        try {
            // Create request body
            JsonObject requestBody = new JsonObject();

            // Contents array
            JsonArray contentsArray = new JsonArray();
            JsonObject content = new JsonObject();
            JsonArray partsArray = new JsonArray();
            JsonObject textPart = new JsonObject();
            textPart.addProperty("text", prompt);
            partsArray.add(textPart);
            content.add("parts", partsArray);
            contentsArray.add(content);
            requestBody.add("contents", contentsArray);

            // Generation config for better responses
            JsonObject generationConfig = new JsonObject();
            generationConfig.addProperty("temperature", 0.7);
            generationConfig.addProperty("topP", 0.8);
            generationConfig.addProperty("topK", 40);
            generationConfig.addProperty("maxOutputTokens", 50000);
            requestBody.add("generationConfig", generationConfig);

            String jsonBody = gson.toJson(requestBody);

            // Build the URL with API key
            String url = GEMINI_API_URL + "?key=" + apiKey;
            Log.d(TAG, "Making request to: " + url.replace(apiKey, "API_KEY_HIDDEN"));

            RequestBody body = RequestBody.create(jsonBody, JSON);
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Network error: " + e.getMessage());
                    callback.onError("Network error. Check your internet connection.");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    int responseCode = response.code();

                    Log.d(TAG, "Response Code: " + responseCode);
                    Log.d(TAG, "Full Response: " + responseBody);

                    if (!response.isSuccessful()) {
                        String errorMessage;
                        if (responseCode == 404) {
                            errorMessage = "Model not found. Trying alternative...";
                        } else if (responseCode == 403) {
                            errorMessage = "API key rejected. Check if it's valid and enabled.";
                        } else if (responseCode == 429) {
                            errorMessage = "Rate limit exceeded. Try again later.";
                        } else {
                            errorMessage = "API Error: " + responseCode;
                        }
                        callback.onError(errorMessage);
                        return;
                    }

                    try {
                        // Parse the response
                        JsonObject jsonResponse = JsonParser.parseString(responseBody).getAsJsonObject();

                        if (!jsonResponse.has("candidates") || jsonResponse.getAsJsonArray("candidates").size() == 0) {
                            callback.onError("No response generated");
                            return;
                        }

                        JsonObject candidate = jsonResponse.getAsJsonArray("candidates")
                                .get(0).getAsJsonObject();

                        // Check for safety reasons blocking
                        if (candidate.has("finishReason") &&
                                "SAFETY".equals(candidate.get("finishReason").getAsString())) {
                            callback.onError("Content blocked by safety filters");
                            return;
                        }

                        JsonObject content = candidate.getAsJsonObject("content");
                        JsonArray parts = content.getAsJsonArray("parts");
                        String text = parts.get(0).getAsJsonObject().get("text").getAsString();

                        // DEBUG: Log what we get
                        Log.d(TAG, "Original text: " + text);

                        // NEW: Simple and robust cleaning
                        text = text.trim();
                        if (text.startsWith("```json")) {
                            text = text.substring(7).trim();
                        }
                        if (text.endsWith("```")) {
                            text = text.substring(0, text.length() - 3).trim();
                        }
                        // Remove any remaining backticks
                        text = text.replace("```", "").trim();

                        Log.d(TAG, "Cleaned text: " + text);

                        // Use lenient JSON parsing to handle any formatting issues
                        JsonReader reader = new JsonReader(new StringReader(text));
                        reader.setLenient(true);  // THIS FIXES THE "MalformedJsonException"
                        JsonObject questionData = JsonParser.parseReader(reader).getAsJsonObject();

                        if (!questionData.has("question") || !questionData.has("correct_answer") ||
                                !questionData.has("incorrect_answers")) {
                            callback.onError("Invalid response format from AI");
                            return;
                        }

                        String question = questionData.get("question").getAsString();
                        String correctAnswer = questionData.get("correct_answer").getAsString();
                        JsonArray incorrectArray = questionData.getAsJsonArray("incorrect_answers");

                        if (incorrectArray.size() != 3) {
                            callback.onError("Expected 3 incorrect answers, got " + incorrectArray.size());
                            return;
                        }

                        String[] incorrectAnswers = new String[3];
                        for (int i = 0; i < 3; i++) {
                            incorrectAnswers[i] = incorrectArray.get(i).getAsString();
                        }

                        callback.onQuestionGenerated(question, correctAnswer, incorrectAnswers);

                    } catch (Exception e) {
                        Log.e(TAG, "Parse error: " + e.getMessage());
                        e.printStackTrace();
                        // Show the FULL error in the toast
                        callback.onError("Parse error: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Request creation error: " + e.getMessage());
            callback.onError("Error creating request: " + e.getMessage());
        }
    }
}
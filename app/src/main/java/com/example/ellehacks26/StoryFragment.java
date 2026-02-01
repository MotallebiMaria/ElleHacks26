package com.example.ellehacks26;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.example.ellehacks26.models.StoryScenario;
import com.example.ellehacks26.models.StoryChoice;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;

public class StoryFragment extends Fragment {

    // UI Elements
    private TextView scenarioTextView;
    private TextView choiceText1, choiceText2, choiceText3, choiceText4;
    private View decisionLayout1, decisionLayout2, decisionLayout3, decisionLayout4;
    private Button chooseButton;
    private ProgressBar loadingIndicator;

    // Story data
    private StoryScenario currentStory;
    private StoryChoice selectedChoice;
    private int selectedChoiceIndex = -1;

    // User stats
    private double userMoney = 100.0;
    private int userEnergy = 100;
    private double userDebt = 0.0;
    private int currentStoryNumber = 1;

    // Gemini Service
    private GeminiService geminiService;
    private Gson gson;

    public StoryFragment() {
        // Constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story_page, container, false);

        // Initialize views
        scenarioTextView = view.findViewById(R.id.scenario_text);

        choiceText1 = view.findViewById(R.id.choice_text1);
        choiceText2 = view.findViewById(R.id.choice_text2);
        choiceText3 = view.findViewById(R.id.choice_text3);
        choiceText4 = view.findViewById(R.id.choice_text4);

        decisionLayout1 = view.findViewById(R.id.decision_layout1);
        decisionLayout2 = view.findViewById(R.id.decision_layout2);
        decisionLayout3 = view.findViewById(R.id.decision_layout3);
        decisionLayout4 = view.findViewById(R.id.decision_layout4);

        chooseButton = view.findViewById(R.id.choose_button);
        loadingIndicator = view.findViewById(R.id.loading_indicator);

        // Initialize Gemini Service
        String apiKey = BuildConfig.GEMINI_API_KEY;
        if (apiKey == null || apiKey.isEmpty() || apiKey.contains("AIzaSyBbzKMUU8K4yEIgCS3pcONnxDuiBYDGSHo")) {
            // Show error if API key is not set properly
            Toast.makeText(getContext(), "API key not configured", Toast.LENGTH_LONG).show();
            apiKey = "AIzaSyBbzKMUU8K4yEIgCS3pcONnxDuiBYDGSHo"; // Replace with your actual key
        }

        geminiService = new GeminiService(apiKey);
        gson = new Gson();

        // Load AI story
        loadAIStory();

        // Setup click listeners
        setupClickListeners();

        return view;
    }

    private void loadAIStory() {
        showLoading(true);

        String userContext = "User has $" + userMoney +
                ", Energy: " + userEnergy +
                ", Story #" + currentStoryNumber;

        geminiService.generateStoryScenario(currentStoryNumber, userContext,
                new GeminiService.GeminiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        requireActivity().runOnUiThread(() -> {
                            try {
                                // Clean the response
                                String cleanResponse = cleanJsonResponse(response);
                                System.out.println("DEBUG: Cleaned response: " + cleanResponse);

                                // Parse JSON
                                currentStory = parseStoryFromJson(cleanResponse);

                                if (currentStory != null) {
                                    displayStory(currentStory);
                                    Toast.makeText(getContext(),
                                            "AI Story Generated! #" + currentStoryNumber,
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // Fallback to lemonade story
                                    loadFallbackStory();
                                    Toast.makeText(getContext(),
                                            "Using fallback story",
                                            Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(),
                                        "Error parsing AI response",
                                        Toast.LENGTH_SHORT).show();
                                loadFallbackStory();
                            }
                            showLoading(false);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        requireActivity().runOnUiThread(() -> {
                            System.out.println("DEBUG: API Error: " + error);
                            Toast.makeText(getContext(),
                                    "AI Failed: " + error + ". Using fallback.",
                                    Toast.LENGTH_LONG).show();
                            loadFallbackStory();
                            showLoading(false);
                        });
                    }
                });
    }

    private String cleanJsonResponse(String response) {
        // Remove markdown code blocks
        response = response.replace("```json", "")
                .replace("```", "")
                .trim();

        // Find JSON object
        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");

        if (start != -1 && end > start) {
            return response.substring(start, end + 1);
        }

        return response;
    }

    private StoryScenario parseStoryFromJson(String json) {
        try {
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

            StoryScenario story = new StoryScenario();
            story.setTitle(jsonObject.has("title") ?
                    jsonObject.get("title").getAsString() : "AI Story #" + currentStoryNumber);

            story.setScenario(jsonObject.has("scenario") ?
                    jsonObject.get("scenario").getAsString() : "A financial scenario...");

            if (jsonObject.has("choices")) {
                List<StoryChoice> choices = new ArrayList<>();
                var choicesArray = jsonObject.getAsJsonArray("choices");

                for (int i = 0; i < Math.min(4, choicesArray.size()); i++) {
                    JsonObject choiceObj = choicesArray.get(i).getAsJsonObject();
                    StoryChoice choice = new StoryChoice();

                    choice.setText(choiceObj.has("text") ?
                            choiceObj.get("text").getAsString() : "Choice " + (i + 1));

                    choice.setMoneyChange(choiceObj.has("moneyChange") ?
                            choiceObj.get("moneyChange").getAsDouble() : 0.0);

                    choice.setEnergyChange(choiceObj.has("energyChange") ?
                            choiceObj.get("energyChange").getAsInt() : 0);

                    choice.setDebtChange(choiceObj.has("debtChange") ?
                            choiceObj.get("debtChange").getAsDouble() : 0.0);

                    choice.setOutcome(choiceObj.has("outcome") ?
                            choiceObj.get("outcome").getAsString() : "Result of this choice");

                    choice.setLesson(choiceObj.has("lesson") ?
                            choiceObj.get("lesson").getAsString() : "Financial lesson");

                    choices.add(choice);
                }

                // Ensure we have exactly 4 choices
                while (choices.size() < 4) {
                    StoryChoice emptyChoice = new StoryChoice();
                    emptyChoice.setText("Choice " + (choices.size() + 1));
                    emptyChoice.setMoneyChange(0.0);
                    emptyChoice.setEnergyChange(0);
                    emptyChoice.setOutcome("Default outcome");
                    emptyChoice.setLesson("Financial literacy");
                    choices.add(emptyChoice);
                }

                story.setChoices(choices);
            }

            return story;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loadFallbackStory() {
        currentStory = new StoryScenario();
        currentStory.setTitle("The Lemonade Stand");
        currentStory.setScenario("You're walking home from school when you see your friend Alex setting up a lemonade stand. They look frustrated because their ice melted and now the lemonade is warm. 'My mom said I could keep all the money I make today, but nobody wants warm lemonade!' Alex says. 'I need $2 for more ice from the store across the street, but I spent all my allowance.' Alex looks at you hopefully.");

        List<StoryChoice> choices = new ArrayList<>();

        // Choice 1
        StoryChoice choice1 = new StoryChoice();
        choice1.setText("I'll lend you $2 from my savings! We can split the profits 50/50");
        choice1.setMoneyChange(-2.0);
        choice1.setEnergyChange(-10);
        choice1.setOutcome("Great partnership! You helped Alex get ice. The stand became super popular and you made $10 in profit!");
        choice1.setLesson("Partnership & investment");
        choices.add(choice1);

        // Choice 2
        StoryChoice choice2 = new StoryChoice();
        choice2.setText("I'll use my $2 to buy the ice myself and run my OWN stand next to yours");
        choice2.setMoneyChange(-2.0);
        choice2.setEnergyChange(-15);
        choice2.setOutcome("You created competition! Both stands struggled. You only made $3 back and Alex is upset with you.");
        choice2.setLesson("Competition vs. cooperation");
        choices.add(choice2);

        // Choice 3
        StoryChoice choice3 = new StoryChoice();
        choice3.setText("I should save my money. Maybe I'll just buy a cup for $1 to be nice");
        choice3.setMoneyChange(-1.0);
        choice3.setEnergyChange(0);
        choice3.setOutcome("Alex appreciates your support but can't afford more ice. The stand closes early.");
        choice3.setLesson("Safe choices vs. risk");
        choices.add(choice3);

        // Choice 4
        StoryChoice choice4 = new StoryChoice();
        choice4.setText("I don't have money to help, but I have energy! I'll run to the store and ask if they'll donate ice for good publicity");
        choice4.setMoneyChange(0.0);
        choice4.setEnergyChange(-20);
        choice4.setOutcome("The store manager was impressed! They gave you ice AND featured your stand on their community board.");
        choice4.setLesson("Creative problem-solving");
        choices.add(choice4);

        currentStory.setChoices(choices);
        displayStory(currentStory);
    }

    private void displayStory(StoryScenario story) {
        // Display scenario
        scenarioTextView.setText(story.getScenario());

        // Display choices
        List<StoryChoice> choices = story.getChoices();
        if (choices != null && choices.size() >= 4) {
            choiceText1.setText(choices.get(0).getText());
            choiceText2.setText(choices.get(1).getText());
            choiceText3.setText(choices.get(2).getText());
            choiceText4.setText(choices.get(3).getText());
        }

        resetSelection();
    }

    private void setupClickListeners() {
        // Decision 1
        decisionLayout1.setOnClickListener(v -> {
            resetSelection();
            decisionLayout1.setBackgroundResource(R.drawable.border_selected);
            selectedChoiceIndex = 0;
            if (currentStory != null && currentStory.getChoices() != null &&
                    selectedChoiceIndex < currentStory.getChoices().size()) {
                selectedChoice = currentStory.getChoices().get(selectedChoiceIndex);
            }
        });

        // Decision 2
        decisionLayout2.setOnClickListener(v -> {
            resetSelection();
            decisionLayout2.setBackgroundResource(R.drawable.border_selected);
            selectedChoiceIndex = 1;
            if (currentStory != null && currentStory.getChoices() != null &&
                    selectedChoiceIndex < currentStory.getChoices().size()) {
                selectedChoice = currentStory.getChoices().get(selectedChoiceIndex);
            }
        });

        // Decision 3
        decisionLayout3.setOnClickListener(v -> {
            resetSelection();
            decisionLayout3.setBackgroundResource(R.drawable.border_selected);
            selectedChoiceIndex = 2;
            if (currentStory != null && currentStory.getChoices() != null &&
                    selectedChoiceIndex < currentStory.getChoices().size()) {
                selectedChoice = currentStory.getChoices().get(selectedChoiceIndex);
            }
        });

        // Decision 4
        decisionLayout4.setOnClickListener(v -> {
            resetSelection();
            decisionLayout4.setBackgroundResource(R.drawable.border_selected);
            selectedChoiceIndex = 3;
            if (currentStory != null && currentStory.getChoices() != null &&
                    selectedChoiceIndex < currentStory.getChoices().size()) {
                selectedChoice = currentStory.getChoices().get(selectedChoiceIndex);
            }
        });

        // Choose Button
        chooseButton.setOnClickListener(v -> {
            if (selectedChoice == null) {
                Toast.makeText(getContext(), "Please select an option first!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Show outcome
            showOutcomeDialog();

            // Update user stats
            updateUserStats();

            // Move to next story
            currentStoryNumber++;
            chooseButton.postDelayed(this::loadAIStory, 2000);
        });
    }

    private void showOutcomeDialog() {
        if (selectedChoice == null) return;

        String message = selectedChoice.getOutcome();
        if (selectedChoice.getLesson() != null && !selectedChoice.getLesson().isEmpty()) {
            message += "\n\nLesson: " + selectedChoice.getLesson();
        }

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Result")
                .setMessage(message)
                .setPositiveButton("Continue", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void updateUserStats() {
        if (selectedChoice == null) return;

        userMoney += selectedChoice.getMoneyChange();
        userEnergy += selectedChoice.getEnergyChange();
        userDebt += selectedChoice.getDebtChange();

        // Keep energy in bounds
        if (userEnergy > 100) userEnergy = 100;
        if (userEnergy < 0) userEnergy = 0;

        // Show updated stats
        String stats = String.format("Money: $%.2f | Energy: %d/100 | Debt: $%.2f",
                userMoney, userEnergy, userDebt);
        Toast.makeText(getContext(), stats, Toast.LENGTH_LONG).show();
    }

    private void resetSelection() {
        selectedChoice = null;
        selectedChoiceIndex = -1;
        decisionLayout1.setBackgroundResource(R.drawable.border);
        decisionLayout2.setBackgroundResource(R.drawable.border);
        decisionLayout3.setBackgroundResource(R.drawable.border);
        decisionLayout4.setBackgroundResource(R.drawable.border);
    }

    private void showLoading(boolean isLoading) {
        if (loadingIndicator != null) {
            loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }

        // Disable/enable UI while loading
        decisionLayout1.setEnabled(!isLoading);
        decisionLayout2.setEnabled(!isLoading);
        decisionLayout3.setEnabled(!isLoading);
        decisionLayout4.setEnabled(!isLoading);
        chooseButton.setEnabled(!isLoading);

        if (isLoading) {
            chooseButton.setText("Generating AI Story...");
            scenarioTextView.setText("Creating a new financial story for you...");
        } else {
            chooseButton.setText("Choose");
        }
    }

    // Helper class to hold story data
    private static class StoryData {
        String scenario;
        String[] choices;
        String[] outcomes;
        String[] lessons;
        double[][] moneyChanges;
        int[][] energyChanges;

        StoryData(String scenario, String[] choices, String[] outcomes,
                  String[] lessons, double[][] moneyChanges, int[][] energyChanges) {
            this.scenario = scenario;
            this.choices = choices;
            this.outcomes = outcomes;
            this.lessons = lessons;
            this.moneyChanges = moneyChanges;
            this.energyChanges = energyChanges;
        }
    }
}
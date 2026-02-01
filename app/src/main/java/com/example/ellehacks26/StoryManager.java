package com.example.ellehacks26;

import android.content.Context;
import com.example.ellehacks26.models.StoryScenario;
import com.example.ellehacks26.models.StoryChoice;
import com.example.ellehacks26.models.User;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

public class StoryManager {
    private GeminiService geminiService;
    private Gson gson;

    public StoryManager(Context context, String geminiApiKey) {
        this.geminiService = new GeminiService(geminiApiKey);
        this.gson = new Gson();
    }

    public void generateNewStory(User currentUser, StoryCallback storyCallback) {
    }

    public interface StoryCallback {
        void onStoryGenerated(StoryScenario story);

        void onSuccess(StoryScenario story);
        void onError(String error);
    }

    public void getStoryForUser(User user, StoryCallback callback) {
        String userContext = "User has $" + user.getMoney() +
                ", Energy: " + user.getEnergy() +
                ", Story #" + user.getCurrentStoryNumber();

        geminiService.generateStoryScenario(user.getCurrentStoryNumber(), userContext,
                new GeminiService.GeminiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            // Clean response
                            String cleanJson = response.replace("```json", "")
                                    .replace("```", "")
                                    .trim();

                            // Find JSON in response
                            int start = cleanJson.indexOf("{");
                            int end = cleanJson.lastIndexOf("}");

                            if (start != -1 && end > start) {
                                cleanJson = cleanJson.substring(start, end + 1);
                            }

                            // Parse
                            StoryScenario story = gson.fromJson(cleanJson, StoryScenario.class);
                            callback.onSuccess(story);

                        } catch (Exception e) {
                            // If AI fails, use your lemonade story
                            callback.onSuccess(getLemonadeStandStory());
                        }
                    }

                    @Override
                    public void onError(String error) {
                        // AI failed, use fallback
                        callback.onSuccess(getLemonadeStandStory());
                    }
                });
    }

    private StoryScenario getLemonadeStandStory() {
        List<StoryChoice> choices = new ArrayList<>();

        choices.add(new StoryChoice(
                "I'll lend you $2 from my savings! We can split the profits 50/50.",
                -2.0, -10, 0.0,
                "Great partnership! You helped Alex get ice. The stand became super popular and you made $10 in profit!",
                "Partnership & investment"
        ));

        choices.add(new StoryChoice(
                "I'll use my $2 to buy the ice myself and run my OWN stand next to yours.",
                -2.0, -15, 0.0,
                "You created competition! Both stands struggled. You only made $3 back and Alex is upset with you.",
                "Competition vs. cooperation"
        ));

        choices.add(new StoryChoice(
                "I should save my money. Maybe I'll just buy a cup for $1 to be nice.",
                -1.0, 0, 0.0,
                "Alex appreciates your support but can't afford more ice. The stand closes early.",
                "Safe choices vs. risk"
        ));

        choices.add(new StoryChoice(
                "I don't have money to help, but I have energy! I'll run to the store and ask if they'll donate ice.",
                0.0, -20, 0.0,
                "The store manager was impressed! They gave you ice AND featured your stand. You and Alex made $15!",
                "Creative problem-solving"
        ));

        return new StoryScenario(
                "The Lemonade Stand",
                "You're walking home from school when you see your friend Alex setting up a lemonade stand...",
                choices
        );
    }
}
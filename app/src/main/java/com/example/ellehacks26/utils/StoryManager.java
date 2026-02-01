package com.example.ellehacks26.utils;

import com.example.ellehacks26.models.StoryEntry;
import com.example.ellehacks26.models.User;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StoryManager {
    private static StoryManager instance;
    private final FirebaseFirestore db;

    // hard-coded first story
    private static final String FIRST_STORY_SCENARIO =
            "You're walking home from school when you see your friend Alex setting up a lemonade stand. " +
                    "They look frustrated because their ice melted and now the lemonade is warm. " +
                    "'My mom said I could keep all the money I make today, but nobody wants warm lemonade!' Alex says. " +
                    "'I need $2 for more ice from the store across the street, but I spent all my allowance.' " +
                    "Alex looks at you hopefully. You have $10 in your pocket that you were saving for a new game.";

    private static final Map<String, StoryChoice> FIRST_STORY_CHOICES = new HashMap<String, StoryChoice>() {{
        put("CHOICE_1", new StoryChoice(
                "I'll lend you $2 from my savings! We can split the profits 50/50.",
                "Great partnership! You helped Alex get ice. The stand became super popular " +
                        "and you made $10 in profit! Alex is now your business partner.",
                8.0,   // +$10 profit - $2 initial = +$8 net
                5,     // -10 energy + 15 success = +5 net
                0.0    // No debt change
        ));
        put("CHOICE_2", new StoryChoice(
                "I'll use my $2 to buy the ice myself and run my OWN stand next to yours.",
                "You created competition! Both stands struggled to attract customers. " +
                        "You only made $3 back and Alex is upset with you.",
                1.0,   // +$3 - $2 = +$1 net
                -20,   // -15 energy - 5 stress = -20 total
                0.0
        ));
        put("CHOICE_3", new StoryChoice(
                "I should save my money. Maybe I'll just buy a cup for $1 to be nice.",
                "Alex appreciates your support but can't afford more ice. " +
                        "The stand closes early. You feel okay but wonder if you missed an opportunity.",
                -1.0,  // Spent $1, no return
                5,     // +5 energy from being nice
                0.0
        ));
        put("CHOICE_4", new StoryChoice(
                "I don't have money to help, but I have energy! I'll run to the store " +
                        "and ask if they'll donate ice for good publicity.",
                "The store manager was impressed with your initiative! " +
                        "They gave you ice AND featured your stand on their community board. " +
                        "You and Alex made $15 total!",
                7.5,   // Half of $15 profit
                10,    // -20 energy + 30 accomplishment = +10 net
                0.0
        ));
    }};

    public interface StoryCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface StoryGenerationCallback {
        void onStoryGenerated(StoryEntry story);
        void onError(String error);
    }

    private StoryManager() {
        db = FirebaseFirestore.getInstance();
    }

    public static StoryManager getInstance() {
        if (instance == null) {
            instance = new StoryManager();
        }
        return instance;
    }

    // generate first story for new users
    public StoryEntry generateFirstStory(String userId) {
        StoryEntry firstStory = new StoryEntry();
        firstStory.setUserId(userId);
        firstStory.setScenario(FIRST_STORY_SCENARIO);
        firstStory.setStoryNumber(1);

        // store available choices as metadata (not in Firestore)
        // we'll handle choices in the UI
        firstStory.setUserChoice(null);
        firstStory.setResult(null);

        return firstStory;
    }

    // set first story when user is created
    public void initializeUserStory(String userId, StoryCallback callback) {
        StoryEntry firstStory = generateFirstStory(userId);

        Map<String, Object> update = new HashMap<>();
        update.put("currentStory", firstStory.toMap());

        db.collection("users").document(userId)
                .update(update)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onError(Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
    }

    // complete current story and store it in history
    public void completeCurrentStory(String userId, String choiceId, StoryCallback callback) {
        // get user document to access current story
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Map<String, Object> userData = task.getResult().getData();
                        if (userData != null) {
                            // extract current story from user data
                            Map<String, Object> currentStoryMap = (Map<String, Object>) userData.get("currentStory");
                            if (currentStoryMap != null) {
                                StoryEntry currentStory = StoryEntry.fromMap(currentStoryMap);

                                // get choice result
                                StoryChoice chosen = FIRST_STORY_CHOICES.get(choiceId);
                                if (chosen != null) {
                                    completeStoryTransaction(userId, currentStory, chosen, callback);
                                } else {
                                    callback.onError("Invalid choice selected");
                                }
                            } else {
                                callback.onError("No current story found");
                            }
                        } else {
                            callback.onError("User data not found");
                        }
                    } else {
                        callback.onError("Failed to get user: " +
                                (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                });
    }

    private void completeStoryTransaction(String userId, StoryEntry currentStory,
                                          StoryChoice chosen, StoryCallback callback) {
        // start a batch write for atomic operationnnnn
        WriteBatch batch = db.batch();

        DocumentReference userRef = db.collection("users").document(userId);

        StoryEntry completedStory = new StoryEntry(
                userId,
                currentStory.getScenario(),
                chosen.choiceText,
                chosen.resultText,
                chosen.moneyChange,
                chosen.energyChange,
                chosen.debtChange,
                currentStory.getStoryNumber()
        );
        completedStory.setTimestamp(Timestamp.now());

        DocumentReference storyEntryRef = userRef.collection("storyEntries").document();
        batch.set(storyEntryRef, completedStory.toMap());

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("money", FieldValue.increment(chosen.moneyChange));
        userUpdates.put("energy", FieldValue.increment(chosen.energyChange));
        userUpdates.put("debtAmount", FieldValue.increment(chosen.debtChange));
        userUpdates.put("currentStoryNumber", FieldValue.increment(1));
        userUpdates.put("currentStory", null); // genai will fill this later

        batch.update(userRef, userUpdates);

        batch.commit()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // generate next story asynchronously
                        generateNextStory(userId, currentStory.getStoryNumber() + 1, callback);
                    } else {
                        callback.onError("Transaction failed: " +
                                (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                });
    }

    private void generateNextStory(String userId, int nextStoryNumber, StoryCallback callback) {
        // TODO: replace with actual API call to Gemini

        if (nextStoryNumber == 2) {
            // placeholder second story
            StoryEntry nextStory = new StoryEntry();
            nextStory.setUserId(userId);
            nextStory.setScenario(
                    "Alex comes to you with exciting news! The local community center wants to feature " +
                            "your lemonade stand at their weekend fair. They offer two options: " +
                            "Option A: Pay $5 for a premium spot with guaranteed traffic. " +
                            "Option B: Get a free spot in a less busy area, but you'll need to do extra advertising."
            );
            nextStory.setStoryNumber(nextStoryNumber);

            Map<String, Object> update = new HashMap<>();
            update.put("currentStory", nextStory.toMap());

            db.collection("users").document(userId)
                    .update(update)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            callback.onError("Failed to set next story: " +
                                    (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                        }
                    });
        } else {
            callback.onSuccess();
        }
    }

    /*
    public void getUserStoryHistory(String userId, com.example.ellehacks26.utils.UserManager.StoryEntriesCallback callback) {
        db.collection("users").document(userId)
                .collection("storyEntries")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        java.util.List<StoryEntry> entries = new java.util.ArrayList<>();
                        for (com.google.firebase.firestore.QueryDocumentSnapshot document : task.getResult()) {
                            StoryEntry entry = StoryEntry.fromMap(document.getData());
                            entry.setEntryId(document.getId());
                            entries.add(entry);
                        }

                        if (callback instanceof com.example.ellehacks26.utils.UserManager.StoryEntriesCallback) {
                            ((com.example.ellehacks26.utils.UserManager.StoryEntriesCallback) callback).onStoryEntriesLoaded(entries);
                        }
                    } else {
                        callback.onError(task.getException() != null ? task.getException().getMessage() : "Unknown error");
                    }
                });
    }
     */

    // helper class for story choices
    private static class StoryChoice {
        String choiceText;
        String resultText;
        double moneyChange;
        int energyChange;
        double debtChange;

        StoryChoice(String choiceText, String resultText, double moneyChange,
                    int energyChange, double debtChange) {
            this.choiceText = choiceText;
            this.resultText = resultText;
            this.moneyChange = moneyChange;
            this.energyChange = energyChange;
            this.debtChange = debtChange;
        }
    }
}
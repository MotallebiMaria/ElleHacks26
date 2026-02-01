package com.example.ellehacks26.lessons;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.example.ellehacks26.R;

public abstract class BaseQuizFragment extends Fragment {

    protected QuestionModel currentQuestion;
    protected String lessonContent;
    protected String topic;

    // Abstract methods for subclasses to implement
    protected abstract int getLayoutId();
    protected abstract String getApiKey();

    @Override
    public void onResume() {
        super.onResume();
        // generate a new question every time the fragment becomes visible
        generateQuestion();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            // generate a new question when fragment becomes visible
            generateQuestion();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutId(), container, false);

        // Get lesson content from arguments
        if (getArguments() != null) {
            lessonContent = getArguments().getString("lesson_content");
            topic = getArguments().getString("topic");
        }

        generateQuestion();
        setupUI(rootView);

        return rootView;
    }

    private void generateQuestion() {
        if (lessonContent == null || topic == null) {
            showError("Missing lesson content");
            return;
        }

        // Reset UI state before generating new question
        resetQuestionUI();

        GeminiApiService.getInstance().generateQuestion(
                topic,
                lessonContent,
                getApiKey(),
                new GeminiApiService.QuestionCallback() {
                    @Override
                    public void onQuestionGenerated(String question, String correctAnswer, String[] incorrectAnswers) {
                        requireActivity().runOnUiThread(() -> {
                            currentQuestion = new QuestionModel(question, correctAnswer, incorrectAnswers);
                            updateQuestionUI();
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        requireActivity().runOnUiThread(() -> {
                            showError("Failed to generate question: " + errorMessage);
                            // Fallback to a default question
                            currentQuestion = getDefaultQuestion();
                            updateQuestionUI();
                        });
                    }
                }
        );
    }

    // Add this helper method
    private void resetQuestionUI() {
        if (getView() == null) return;

        // Reset all option buttons
        int[] optionIds = {R.id.option1, R.id.option2, R.id.option3, R.id.option4};
        for (int id : optionIds) {
            Button btn = getView().findViewById(id);
            if (btn != null) {
                btn.setEnabled(true);
                btn.setBackgroundTintList(null);
                btn.setText("..."); // Placeholder text while loading
            }
        }

        // Reset question text
        TextView questionText = getView().findViewById(R.id.questionText);
        if (questionText != null) {
            questionText.setText("Loading question...");
        }

        currentQuestion = null;
    }

    private void setupUI(View rootView) {
        Button nextButton = rootView.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(v -> navigateToLessonsPage());

        // Set up option buttons
        int[] optionIds = {R.id.option1, R.id.option2, R.id.option3, R.id.option4};
        for (int i = 0; i < optionIds.length; i++) {
            Button optionButton = rootView.findViewById(optionIds[i]);
            final int index = i;
            optionButton.setOnClickListener(v -> handleAnswerSelection(index));
        }
    }

    private void updateQuestionUI() {
        if (currentQuestion == null || getView() == null) return;

        TextView questionText = getView().findViewById(R.id.questionText);
        if (questionText != null) {
            questionText.setText(currentQuestion.getQuestion());
        }

        int[] optionIds = {R.id.option1, R.id.option2, R.id.option3, R.id.option4};
        String[] options = currentQuestion.getAllOptions();

        for (int i = 0; i < optionIds.length && i < options.length; i++) {
            Button optionButton = getView().findViewById(optionIds[i]);
            if (optionButton != null) {
                optionButton.setText(options[i]);
                optionButton.setBackgroundTintList(null); // Reset color
                optionButton.setEnabled(true);
            }
        }
    }

    private void handleAnswerSelection(int optionIndex) {
        if (currentQuestion == null || getView() == null) return;

        String[] options = currentQuestion.getAllOptions();
        if (optionIndex >= options.length) return;

        String selectedAnswer = options[optionIndex];
        boolean isCorrect = currentQuestion.isCorrect(selectedAnswer);

        // Visual feedback
        int[] optionIds = {R.id.option1, R.id.option2, R.id.option3, R.id.option4};
        Button selectedButton = getView().findViewById(optionIds[optionIndex]);

        if (selectedButton != null) {
            selectedButton.setBackgroundTintList(
                    getResources().getColorStateList(
                            isCorrect ? R.color.green : R.color.red
                    )
            );
        }

        // Disable all buttons after selection
        for (int id : optionIds) {
            Button btn = getView().findViewById(id);
            if (btn != null) btn.setEnabled(false);
        }

        // Show result message
        Toast.makeText(getContext(),
                isCorrect ? "Correct! 🎉" : "Try again! The correct answer was: " + currentQuestion.getCorrectAnswer(),
                Toast.LENGTH_LONG
        ).show();
    }

    private void navigateToLessonsPage() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.changeFragments, new LessonFragment())
                .addToBackStack(null)
                .commit();
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    private QuestionModel getDefaultQuestion() {
        // Simple fallback question
        return new QuestionModel(
                "What did you learn from this lesson?",
                "Financial responsibility",
                new String[]{"Nothing", "How to spend money", "Math equations"}
        );
    }
}
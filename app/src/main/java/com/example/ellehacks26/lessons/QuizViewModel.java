package com.example.ellehacks26.lessons;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.HashMap;
import java.util.Map;

public class QuizViewModel extends ViewModel {
    private final MutableLiveData<QuestionModel> currentQuestion = new MutableLiveData<>();
    private final Map<String, Boolean> lessonVisited = new HashMap<>();

    public LiveData<QuestionModel> getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(QuestionModel question) {
        currentQuestion.setValue(question);
    }

    // Track if we should generate new question for a lesson
    public boolean shouldGenerateNewQuestion(String lessonId) {
        if (!lessonVisited.containsKey(lessonId)) {
            lessonVisited.put(lessonId, true);
            return true; // First time, generate new question
        }
        return false; // Already visited this session, reuse
    }

    // Call this when leaving quiz or when lesson starts fresh
    public void markLessonForNewQuestion(String lessonId) {
        lessonVisited.remove(lessonId); // Remove so next time it generates new
    }

    // Clear all for fresh start
    public void clearAll() {
        lessonVisited.clear();
        currentQuestion.setValue(null);
    }
}
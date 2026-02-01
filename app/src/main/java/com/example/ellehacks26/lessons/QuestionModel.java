package com.example.ellehacks26.lessons;

public class QuestionModel {
    private String question;
    private String correctAnswer;
    private String[] incorrectAnswers;
    private String[] allOptions; // shuffled options

    public QuestionModel(String question, String correctAnswer, String[] incorrectAnswers) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
        this.allOptions = shuffleOptions(correctAnswer, incorrectAnswers);
    }

    private String[] shuffleOptions(String correct, String[] incorrect) {
        String[] options = new String[4];
        options[0] = correct;
        System.arraycopy(incorrect, 0, options, 1, 3);

        // basic shuffle
        for (int i = 0; i < options.length; i++) {
            int randomIndex = (int) (Math.random() * options.length);
            String temp = options[i];
            options[i] = options[randomIndex];
            options[randomIndex] = temp;
        }
        return options;
    }

    public String getQuestion() { return question; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String[] getAllOptions() { return allOptions; }
    public boolean isCorrect(String selectedAnswer) {
        return selectedAnswer.equals(correctAnswer);
    }
}
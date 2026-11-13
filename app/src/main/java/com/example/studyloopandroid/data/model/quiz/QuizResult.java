package com.example.studyloopandroid.data.model.quiz;

public class QuizResult {

    private String quizId;
    private int totalQuestions;
    private int correctAnswers;
    private double score;
    private String percentage;

    public String getQuizId() {
        return quizId;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public double getScore() {
        return score;
    }

    public String getPercentage() {
        return percentage;
    }
}
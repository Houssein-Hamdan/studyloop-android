package com.example.studyloopandroid.data.model.quiz;

public class QuizAnswerRequest {

    private String questionId;
    private String userAnswer;

    public QuizAnswerRequest(String questionId, String userAnswer) {
        this.questionId = questionId;
        this.userAnswer = userAnswer;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getUserAnswer() {
        return userAnswer;
    }
}
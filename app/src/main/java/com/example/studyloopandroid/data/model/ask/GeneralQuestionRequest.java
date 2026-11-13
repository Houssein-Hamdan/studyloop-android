package com.example.studyloopandroid.data.model.ask;

public class GeneralQuestionRequest {

    private String questionText;

    public GeneralQuestionRequest(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionText() {
        return questionText;
    }
}
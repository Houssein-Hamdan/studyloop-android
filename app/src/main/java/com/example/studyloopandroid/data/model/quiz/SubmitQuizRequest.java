package com.example.studyloopandroid.data.model.quiz;

import java.util.List;

public class SubmitQuizRequest {

    private List<QuizAnswerRequest> answers;

    public SubmitQuizRequest(List<QuizAnswerRequest> answers) {
        this.answers = answers;
    }

    public List<QuizAnswerRequest> getAnswers() {
        return answers;
    }
}
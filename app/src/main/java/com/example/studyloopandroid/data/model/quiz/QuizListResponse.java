package com.example.studyloopandroid.data.model.quiz;

import java.util.List;

public class QuizListResponse {

    private String message;

    private int count;

    private List<Quiz> quizzes;

    public String getMessage() {
        return message;
    }

    public int getCount() {
        return count;
    }

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

}

package com.example.studyloopandroid.data.model.quiz;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Quiz {

    private String id;

    private String lessonId;

    private String scope;

    private String difficulty;

    private String format;

    private int questionCount;

    private List<QuizQuestion> questions;

    private String createdAt;

    public String getId() {
        return id;
    }

    public String getLessonId() {
        return lessonId;
    }

    public String getScope() {
        return scope;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getFormat() {
        return format;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public List<QuizQuestion> getQuestions() {
        return questions;
    }

    public String getCreatedAt() {
        return createdAt;
    }

}

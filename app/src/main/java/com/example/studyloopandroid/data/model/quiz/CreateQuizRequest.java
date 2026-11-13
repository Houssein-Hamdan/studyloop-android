package com.example.studyloopandroid.data.model.quiz;

import java.util.List;

public class CreateQuizRequest {

    private String scope;
    private List<String> selectedTopicIds;
    private String difficulty;
    private String format;
    private int questionCount;

    public CreateQuizRequest() {
    }

    public CreateQuizRequest(
            String scope,
            List<String> selectedTopicIds,
            String difficulty,
            String format,
            int questionCount
    ) {
        this.scope = scope;
        this.selectedTopicIds = selectedTopicIds;
        this.difficulty = difficulty;
        this.format = format;
        this.questionCount = questionCount;
    }

    public String getScope() {
        return scope;
    }

    public List<String> getSelectedTopicIds() {
        return selectedTopicIds;
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
}
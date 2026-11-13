package com.example.studyloopandroid.data.model.lesson;

public class UpdateTopicProgressRequest {

    private final boolean isCompleted;

    public UpdateTopicProgressRequest(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

}

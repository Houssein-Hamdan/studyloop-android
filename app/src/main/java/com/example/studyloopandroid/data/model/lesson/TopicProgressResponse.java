package com.example.studyloopandroid.data.model.lesson;

public class TopicProgressResponse {

    private String id;
    private String topicId;
    private boolean isCompleted;
    private String completedAt;

    public String getId() {
        return id;
    }

    public String getTopicId() {
        return topicId;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public String getCompletedAt() {
        return completedAt;
    }
}
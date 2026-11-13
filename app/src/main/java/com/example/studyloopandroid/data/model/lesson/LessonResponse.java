package com.example.studyloopandroid.data.model.lesson;

import java.util.List;

public class LessonResponse {

    private String id;
    private String title;
    private List<TopicResponse> topics;
    private String createdAt;
    private String updatedAt;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<TopicResponse> getTopics() {
        return topics;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

}

package com.example.studyloopandroid.data.model.summary;

import java.util.List;

public class Summary {

    private String id;
    private String lessonId;
    private String depth;
    private String content;
    private List<String> selectedTopicIds;
    private String createdAt;
    private String updatedAt;

    public String getId() {
        return id;
    }

    public String getLessonId() {
        return lessonId;
    }

    public String getDepth() {
        return depth;
    }

    public String getContent() {
        return content;
    }

    public List<String> getSelectedTopicIds() {
        return selectedTopicIds;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
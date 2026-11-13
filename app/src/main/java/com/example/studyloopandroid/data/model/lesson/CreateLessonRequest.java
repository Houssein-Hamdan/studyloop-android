package com.example.studyloopandroid.data.model.lesson;

import java.util.List;

public class CreateLessonRequest {

    private final String title;
    private final String rawContent;
    private final List<CreateTopicRequest> topics;

    public CreateLessonRequest(
            String title,
            String rawContent,
            List<CreateTopicRequest> topics
    ) {
        this.title = title;
        this.rawContent = rawContent;
        this.topics = topics;
    }

    public String getTitle() {
        return title;
    }

    public String getRawContent() {
        return rawContent;
    }

    public List<CreateTopicRequest> getTopics() {
        return topics;
    }
}

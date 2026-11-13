package com.example.studyloopandroid.data.model.lesson;

public class UpdateLessonRequest {

    private final String title;

    public UpdateLessonRequest(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}

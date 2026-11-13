package com.example.studyloopandroid.data.model.lesson;

import java.util.List;

public class LessonsResponse {

    private String message;
    private int count;
    private List<LessonResponse> lessons;

    public String getMessage() {
        return message;
    }

    public int getCount() {
        return count;
    }

    public List<LessonResponse> getLessons() {
        return lessons;
    }

}

package com.example.studyloopandroid.data.model.lesson;

import java.util.List;

public class LessonProgressResponse {

    private String message;
    private ProgressData progress;
    private List<TopicProgressResponse> topicProgress;

    public String getMessage() {
        return message;
    }

    public ProgressData getProgress() {
        return progress;
    }

    public List<TopicProgressResponse> getTopicProgress() {
        return topicProgress;
    }

    public static class ProgressData {

        private String id;
        private String lessonId;
        private int completedTopicsCount;
        private int totalTopicsCount;
        private int completionPercentage;
        private boolean isLessonCompleted;

        public String getId() {
            return id;
        }

        public String getLessonId() {
            return lessonId;
        }

        public int getCompletedTopicsCount() {
            return completedTopicsCount;
        }

        public int getTotalTopicsCount() {
            return totalTopicsCount;
        }

        public int getCompletionPercentage() {
            return completionPercentage;
        }

        public boolean isLessonCompleted() {
            return isLessonCompleted;
        }
    }
}
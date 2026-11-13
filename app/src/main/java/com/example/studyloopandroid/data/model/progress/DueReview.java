package com.example.studyloopandroid.data.model.progress;

import com.google.gson.annotations.SerializedName;

public class DueReview {

    @SerializedName("id")
    private String id;

    @SerializedName("lessonId")
    private String lessonId;

    @SerializedName("completedTopicsCount")
    private int completedTopicsCount;

    @SerializedName("totalTopicsCount")
    private int totalTopicsCount;

    @SerializedName("completionPercentage")
    private int completionPercentage;

    @SerializedName("isLessonCompleted")
    private boolean isLessonCompleted;

    @SerializedName("lastReviewedAt")
    private String lastReviewedAt;

    @SerializedName("nextReviewDate")
    private String nextReviewDate;

    @SerializedName("reviewCount")
    private int reviewCount;

    @SerializedName("reviewIntervalDays")
    private int reviewIntervalDays;

    @SerializedName("lesson")
    private ReviewLesson lesson;

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

    public String getLastReviewedAt() {
        return lastReviewedAt;
    }

    public String getNextReviewDate() {
        return nextReviewDate;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public int getReviewIntervalDays() {
        return reviewIntervalDays;
    }

    public ReviewLesson getLesson() {
        return lesson;
    }
}
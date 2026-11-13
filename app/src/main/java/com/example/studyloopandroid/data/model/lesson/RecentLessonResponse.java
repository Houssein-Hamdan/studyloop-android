package com.example.studyloopandroid.data.model.lesson;

import com.google.gson.annotations.SerializedName;

public class RecentLessonResponse {

    @SerializedName("lessonId")
    private String lessonId;

    @SerializedName("lessonTitle")
    private String lessonTitle;

    @SerializedName("containerId")
    private String containerId;

    @SerializedName("totalTopics")
    private int totalTopics;

    @SerializedName("completedTopics")
    private int completedTopics;

    @SerializedName("progressPercentage")
    private int progressPercentage;

    @SerializedName("lastAccessedAt")
    private String lastAccessedAt;

    public String getLessonId() {
        return lessonId;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public String getContainerId() {
        return containerId;
    }

    public int getTotalTopics() {
        return totalTopics;
    }

    public int getCompletedTopics() {
        return completedTopics;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }

    public String getLastAccessedAt() {
        return lastAccessedAt;
    }

}

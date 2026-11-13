package com.example.studyloopandroid.data.model.progress;

import com.google.gson.annotations.SerializedName;

public class ReviewLesson {

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("containerId")
    private String containerId;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContainerId() {
        return containerId;
    }
}
package com.example.studyloopandroid.data.model.progress;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DueReviewResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("count")
    private int count;

    @SerializedName("reviews")
    private List<DueReview> reviews;

    public String getMessage() {
        return message;
    }

    public int getCount() {
        return count;
    }

    public List<DueReview> getReviews() {
        return reviews;
    }
}
package com.example.studyloopandroid.data.model.progress;

import com.google.gson.annotations.SerializedName;

public class ReviewCompleteResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("progress")
    private ReviewProgress progress;

    public String getMessage() {
        return message;
    }

    public ReviewProgress getProgress() {
        return progress;
    }

    public static class ReviewProgress {

        @SerializedName("reviewCount")
        private int reviewCount;

        @SerializedName("reviewIntervalDays")
        private int reviewIntervalDays;

        @SerializedName("nextReviewDate")
        private String nextReviewDate;

        @SerializedName("lessonId")
        private String lessonId;

        public String getLessonId() {
            return lessonId;
        }

        public int getReviewCount() {
            return reviewCount;
        }

        public int getReviewIntervalDays() {
            return reviewIntervalDays;
        }

        public String getNextReviewDate() {
            return nextReviewDate;
        }
    }
}
package com.example.studyloopandroid.data.model.analytics;

import com.example.studyloopandroid.data.model.lesson.RecentLessonResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AnalyticsOverviewResponse {

    @SerializedName("overview")
    private Overview overview;

    @SerializedName("quizzes")
    private QuizStats quizzes;

    @SerializedName("recentActivity")
    private List<RecentLessonResponse> recentActivity;

    public Overview getOverview() {
        return overview;
    }

    public QuizStats getQuizzes() {
        return quizzes;
    }

    public List<RecentLessonResponse> getRecentActivity() {
        return recentActivity;
    }

    public static class Overview {

        @SerializedName("totalLessons")
        private int totalLessons;

        @SerializedName("totalTopics")
        private int totalTopics;

        @SerializedName("completedTopics")
        private int completedTopics;

        @SerializedName("globalCompletionRate")
        private int globalCompletionRate;

        @SerializedName("totalAnnotations")
        private int totalAnnotations;

        public int getTotalLessons() {
            return totalLessons;
        }

        public int getTotalTopics() {
            return totalTopics;
        }

        public int getCompletedTopics() {
            return completedTopics;
        }

        public int getGlobalCompletionRate() {
            return globalCompletionRate;
        }

        public int getTotalAnnotations() {
            return totalAnnotations;
        }
    }

    public static class QuizStats {

        @SerializedName("totalTaken")
        private int totalTaken;

        @SerializedName("averageScore")
        private double averageScore;

        public int getTotalTaken() {
            return totalTaken;
        }

        public double getAverageScore() {
            return averageScore;
        }
    }

}

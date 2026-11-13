package com.example.studyloopandroid.data.repository;

import android.content.Context;

import com.example.studyloopandroid.data.model.analytics.AnalyticsOverviewResponse;
import com.example.studyloopandroid.data.model.lesson.RecentLessonsResponse;
import com.example.studyloopandroid.data.remote.AnalyticsApi;
import com.example.studyloopandroid.data.remote.RetrofitClient;

import retrofit2.Call;

public class AnalyticsRepository {

    private final AnalyticsApi analyticsApi;

    public AnalyticsRepository(Context context) {
        analyticsApi = RetrofitClient
                .getInstance(context)
                .create(AnalyticsApi.class);
    }

    // Existing method used by the Dashboard
    public Call<RecentLessonsResponse> getRecentLessons() {
        return analyticsApi.getRecentLessons();
    }

    // New method for the Analytics screen
    public Call<AnalyticsOverviewResponse> getOverview() {
        return analyticsApi.getOverview();
    }

}

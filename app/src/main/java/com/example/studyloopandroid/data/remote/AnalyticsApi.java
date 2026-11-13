package com.example.studyloopandroid.data.remote;

import com.example.studyloopandroid.data.model.analytics.AnalyticsOverviewResponse;
import com.example.studyloopandroid.data.model.lesson.RecentLessonsResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AnalyticsApi {

    // Existing endpoint used by the Dashboard
    @GET("analytics/overview")
    Call<RecentLessonsResponse> getRecentLessons();

    // Full analytics data
    @GET("analytics/overview")
    Call<AnalyticsOverviewResponse> getOverview();

}

package com.example.studyloopandroid.data.remote;

import com.example.studyloopandroid.data.model.summary.CreateSummaryRequest;
import com.example.studyloopandroid.data.model.summary.CreateSummaryResponse;
import com.example.studyloopandroid.data.model.summary.SummaryListResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface SummaryApiService {

    @POST("containers/{containerId}/lessons/{lessonId}/summaries")
    Call<CreateSummaryResponse> createSummary(
            @Path("containerId") String containerId,
            @Path("lessonId") String lessonId,
            @Body CreateSummaryRequest request
    );

    @GET("containers/{containerId}/lessons/{lessonId}/summaries")
    Call<SummaryListResponse> getSummaries(
            @Path("containerId") String containerId,
            @Path("lessonId") String lessonId
    );

    @GET("containers/{containerId}/lessons/{lessonId}/summaries/{id}")
    Call<CreateSummaryResponse> getSummary(
            @Path("containerId") String containerId,
            @Path("lessonId") String lessonId,
            @Path("id") String summaryId
    );

    @DELETE("containers/{containerId}/lessons/{lessonId}/summaries/{id}")
    Call<Void> deleteSummary(
            @Path("containerId") String containerId,
            @Path("lessonId") String lessonId,
            @Path("id") String summaryId
    );
}
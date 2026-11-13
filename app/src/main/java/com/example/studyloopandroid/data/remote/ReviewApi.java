package com.example.studyloopandroid.data.remote;

import com.example.studyloopandroid.data.model.progress.DueReviewResponse;
import com.example.studyloopandroid.data.model.progress.ReviewCompleteResponse;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ReviewApi {

    @GET("progress/due-reviews")
    Call<DueReviewResponse> getDueReviews();

    @POST("containers/{containerId}/lessons/{lessonId}/progress/review")
    Call<ReviewCompleteResponse> completeReview(
            @Path("containerId") String containerId,
            @Path("lessonId") String lessonId
    );

}
package com.example.studyloopandroid.data.repository;

import android.content.Context;

import com.example.studyloopandroid.data.model.progress.DueReviewResponse;
import com.example.studyloopandroid.data.remote.RetrofitClient;
import com.example.studyloopandroid.data.remote.ReviewApi;

import com.example.studyloopandroid.data.model.progress.ReviewCompleteResponse;

import retrofit2.Call;

public class ReviewRepository {

    private final ReviewApi reviewApi;

    public ReviewRepository(Context context) {
        reviewApi = RetrofitClient
                .getInstance(context)
                .create(ReviewApi.class);
    }

    public Call<DueReviewResponse> getDueReviews() {
        return reviewApi.getDueReviews();
    }

    public Call<ReviewCompleteResponse> completeReview(
            String containerId,
            String lessonId
    ) {
        return reviewApi.completeReview(containerId, lessonId);
    }
}
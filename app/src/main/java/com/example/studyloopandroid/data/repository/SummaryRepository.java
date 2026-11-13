package com.example.studyloopandroid.data.repository;

import android.content.Context;

import com.example.studyloopandroid.data.model.summary.CreateSummaryRequest;
import com.example.studyloopandroid.data.model.summary.CreateSummaryResponse;
import com.example.studyloopandroid.data.model.summary.SummaryListResponse;
import com.example.studyloopandroid.data.remote.RetrofitClient;
import com.example.studyloopandroid.data.remote.SummaryApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SummaryRepository {

    private final SummaryApiService apiService;

    public SummaryRepository(Context context) {
        apiService = RetrofitClient.getInstance(context)
                .create(SummaryApiService.class);
    }

    public void createSummary(
            String containerId,
            String lessonId,
            CreateSummaryRequest request,
            ApiCallback<CreateSummaryResponse> callback
    ) {
        apiService.createSummary(containerId, lessonId, request)
                .enqueue(new Callback<CreateSummaryResponse>() {
                    @Override
                    public void onResponse(
                            Call<CreateSummaryResponse> call,
                            Response<CreateSummaryResponse> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError(
                                    ApiErrorMapper.fromResponse(
                                            response,
                                            "Failed to create summary."
                                    )
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<CreateSummaryResponse> call,
                            Throwable t
                    ) {
                        callback.onError(
                                ApiErrorMapper.fromThrowable(t)
                        );
                    }
                });
    }

    public void getSummaries(
            String containerId,
            String lessonId,
            ApiCallback<SummaryListResponse> callback
    ) {
        apiService.getSummaries(containerId, lessonId)
                .enqueue(new Callback<SummaryListResponse>() {
                    @Override
                    public void onResponse(
                            Call<SummaryListResponse> call,
                            Response<SummaryListResponse> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError(
                                    ApiErrorMapper.fromResponse(
                                            response,
                                            "Failed to load summaries."
                                    )
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<SummaryListResponse> call,
                            Throwable t
                    ) {
                        callback.onError(
                                ApiErrorMapper.fromThrowable(t)
                        );
                    }
                });
    }

    public void getSummary(
            String containerId,
            String lessonId,
            String summaryId,
            ApiCallback<CreateSummaryResponse> callback
    ) {
        apiService.getSummary(containerId, lessonId, summaryId)
                .enqueue(new Callback<CreateSummaryResponse>() {
                    @Override
                    public void onResponse(
                            Call<CreateSummaryResponse> call,
                            Response<CreateSummaryResponse> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError(
                                    ApiErrorMapper.fromResponse(
                                            response,
                                            "Failed to load summary."
                                    )
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<CreateSummaryResponse> call,
                            Throwable t
                    ) {
                        callback.onError(
                                ApiErrorMapper.fromThrowable(t)
                        );
                    }
                });
    }

    public void deleteSummary(
            String containerId,
            String lessonId,
            String summaryId,
            ApiCallback<Void> callback
    ) {
        apiService.deleteSummary(containerId, lessonId, summaryId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(
                            Call<Void> call,
                            Response<Void> response
                    ) {
                        if (response.isSuccessful()) {
                            callback.onSuccess(null);
                        } else {
                            callback.onError(
                                    ApiErrorMapper.fromResponse(
                                            response,
                                            "Failed to delete summary."
                                    )
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<Void> call,
                            Throwable t
                    ) {
                        callback.onError(
                                ApiErrorMapper.fromThrowable(t)
                        );
                    }
                });
    }
}

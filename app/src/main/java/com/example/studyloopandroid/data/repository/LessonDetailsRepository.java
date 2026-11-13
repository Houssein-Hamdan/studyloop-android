package com.example.studyloopandroid.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.studyloopandroid.data.model.lesson.LessonDetailsResponse;
import com.example.studyloopandroid.data.model.lesson.LessonResponse;
import com.example.studyloopandroid.data.remote.LessonDetailsApi;
import com.example.studyloopandroid.data.remote.RetrofitClient;
import com.example.studyloopandroid.data.model.lesson.CreateTopicRequest;
import com.example.studyloopandroid.data.model.lesson.LessonProgressResponse;
import com.example.studyloopandroid.data.model.lesson.UpdateTopicProgressRequest;
import com.example.studyloopandroid.data.model.lesson.ProgressInitializationResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LessonDetailsRepository {

    private static final String TAG = "LessonDetailsRepo";

    private final LessonDetailsApi lessonDetailsApi;

    public LessonDetailsRepository(Context context) {
        lessonDetailsApi = RetrofitClient
                .getInstance(context)
                .create(LessonDetailsApi.class);
    }

    public void getLessonDetails(
            String containerId,
            String lessonId,
            ApiCallback<LessonResponse> callback
    ) {
        lessonDetailsApi
                .getLessonDetails(containerId, lessonId)
                .enqueue(new Callback<LessonDetailsResponse>() {

                    @Override
                    public void onResponse(
                            Call<LessonDetailsResponse> call,
                            Response<LessonDetailsResponse> response
                    ) {
                        if (response.isSuccessful()
                                && response.body() != null
                                && response.body().getLesson() != null) {

                            callback.onSuccess(
                                    response.body().getLesson()
                            );

                        } else if (response.code() == 404) {

                            callback.onError("Lesson not found.");

                        } else {
                            notifyHttpError(
                                    response,
                                    "Failed to load lesson",
                                    callback
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<LessonDetailsResponse> call,
                            Throwable t
                    ) {
                        notifyFailure(t, callback);
                    }
                });
    }

    public void createTopic(
            String containerId,
            String lessonId,
            CreateTopicRequest request,
            ApiCallback<Void> callback
    ) {
        lessonDetailsApi.createTopic(containerId, lessonId, request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(
                            Call<Void> call,
                            Response<Void> response
                    ) {
                        if (response.isSuccessful()) {
                            callback.onSuccess(null);
                        } else {
                            notifyHttpError(
                                    response,
                                    "Failed to create topic",
                                    callback
                            );
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        notifyFailure(t, callback);
                    }
                });
    }

    public void updateTopic(
            String containerId,
            String lessonId,
            String topicId,
            CreateTopicRequest request,
            ApiCallback<Void> callback
    ) {
        lessonDetailsApi.updateTopic(containerId, lessonId, topicId, request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(
                            Call<Void> call,
                            Response<Void> response
                    ) {
                        if (response.isSuccessful()) {
                            callback.onSuccess(null);
                        } else {
                            notifyHttpError(
                                    response,
                                    "Failed to update topic",
                                    callback
                            );
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        notifyFailure(t, callback);
                    }
                });
    }

    public void deleteTopic(
            String containerId,
            String lessonId,
            String topicId,
            ApiCallback<Void> callback
    ) {
        lessonDetailsApi.deleteTopic(containerId, lessonId, topicId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(
                            Call<Void> call,
                            Response<Void> response
                    ) {
                        if (response.isSuccessful()) {
                            callback.onSuccess(null);
                        } else {
                            notifyHttpError(
                                    response,
                                    "Failed to delete topic",
                                    callback
                            );
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        notifyFailure(t, callback);
                    }
                });
    }

    public void getLessonProgress(
            String containerId,
            String lessonId,
            ApiCallback<LessonProgressResponse> callback
    ) {
        lessonDetailsApi.getLessonProgress(containerId, lessonId)
                .enqueue(new Callback<LessonProgressResponse>() {

                    @Override
                    public void onResponse(
                            Call<LessonProgressResponse> call,
                            Response<LessonProgressResponse> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            notifyHttpError(
                                    response,
                                    "Failed to load progress",
                                    callback
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<LessonProgressResponse> call,
                            Throwable t
                    ) {
                        notifyFailure(t, callback);
                    }
                });
    }

    public void updateTopicProgress(
            String containerId,
            String lessonId,
            String topicId,
            boolean isCompleted,
            ApiCallback<Void> callback
    ) {
        UpdateTopicProgressRequest request =
                new UpdateTopicProgressRequest(isCompleted);

        lessonDetailsApi.updateTopicProgress(
                containerId,
                lessonId,
                topicId,
                request
        ).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(
                    Call<Void> call,
                    Response<Void> response
            ) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    notifyHttpError(
                            response,
                            "Failed to update topic progress",
                            callback
                    );
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                notifyFailure(t, callback);
            }
        });
    }


    public void getOrCreateProgress(
            String containerId,
            String lessonId,
            ApiCallback<ProgressInitializationResponse> callback
    ) {
        lessonDetailsApi.getOrCreateProgress(containerId, lessonId)
                .enqueue(new Callback<ProgressInitializationResponse>() {

                    @Override
                    public void onResponse(
                            Call<ProgressInitializationResponse> call,
                            Response<ProgressInitializationResponse> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            notifyHttpError(
                                    response,
                                    "Failed to initialize progress",
                                    callback
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<ProgressInitializationResponse> call,
                            Throwable t
                    ) {
                        notifyFailure(t, callback);
                    }
                });
    }

    private void notifyHttpError(
            Response<?> response,
            String action,
            ApiCallback<?> callback
    ) {
        Log.e(TAG, action + ". HTTP " + response.code());

        callback.onError(
                ApiErrorMapper.fromResponse(response, action + ".")
        );
    }

    private void notifyFailure(
            Throwable t,
            ApiCallback<?> callback
    ) {
        Log.e(TAG, "Request failed", t);

        callback.onError(
                ApiErrorMapper.fromThrowable(t)
        );
    }
}

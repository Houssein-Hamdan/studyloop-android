package com.example.studyloopandroid.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.studyloopandroid.data.model.lesson.CreateLessonRequest;
import com.example.studyloopandroid.data.model.lesson.LessonResponse;
import com.example.studyloopandroid.data.model.lesson.LessonsResponse;
import com.example.studyloopandroid.data.remote.LessonsApi;
import com.example.studyloopandroid.data.remote.RetrofitClient;
import com.example.studyloopandroid.data.model.lesson.UpdateLessonRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LessonRepository {

    private static final String TAG = "LessonsAPI";

    private final LessonsApi lessonsApi;

    public LessonRepository(Context context) {
        lessonsApi = RetrofitClient
                .getInstance(context)
                .create(LessonsApi.class);
    }

    public void getLessons(
            String containerId,
            ApiCallback<List<LessonResponse>> callback
    ) {
        lessonsApi.getLessons(containerId).enqueue(
                new Callback<LessonsResponse>() {

                    @Override
                    public void onResponse(
                            Call<LessonsResponse> call,
                            Response<LessonsResponse> response
                    ) {
                        if (response.isSuccessful()
                                && response.body() != null) {

                            List<LessonResponse> lessons =
                                    response.body().getLessons();

                            if (lessons == null) {
                                callback.onError(
                                        "Invalid response: lessons list is missing."
                                );
                                return;
                            }

                            callback.onSuccess(lessons);

                        } else {
                            Log.e(TAG, "Get lessons failed: HTTP "
                                    + response.code());

                            callback.onError(
                                    getHttpErrorMessage(response)
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<LessonsResponse> call,
                            Throwable t
                    ) {
                        handleNetworkError(t, callback);
                    }
                }
        );
    }

    public void createLesson(
            String containerId,
            CreateLessonRequest request,
            ApiCallback<Boolean> callback
    ) {
        lessonsApi.createLesson(containerId, request).enqueue(
                new Callback<Void>() {

                    @Override
                    public void onResponse(
                            Call<Void> call,
                            Response<Void> response
                    ) {
                        handleMutationResponse(response, callback);
                    }

                    @Override
                    public void onFailure(
                            Call<Void> call,
                            Throwable t
                    ) {
                        handleNetworkError(t, callback);
                    }
                }
        );
    }

    public void updateLesson(
            String containerId,
            String lessonId,
            UpdateLessonRequest request,
            ApiCallback<Boolean> callback
    ) {
        lessonsApi.updateLesson(
                containerId,
                lessonId,
                request
        ).enqueue(
                new Callback<Void>() {

                    @Override
                    public void onResponse(
                            Call<Void> call,
                            Response<Void> response
                    ) {
                        handleMutationResponse(response, callback);
                    }

                    @Override
                    public void onFailure(
                            Call<Void> call,
                            Throwable t
                    ) {
                        handleNetworkError(t, callback);
                    }
                }
        );
    }

    public void deleteLesson(
            String containerId,
            String lessonId,
            ApiCallback<Boolean> callback
    ) {
        lessonsApi.deleteLesson(
                containerId,
                lessonId
        ).enqueue(
                new Callback<Void>() {

                    @Override
                    public void onResponse(
                            Call<Void> call,
                            Response<Void> response
                    ) {
                        handleMutationResponse(response, callback);
                    }

                    @Override
                    public void onFailure(
                            Call<Void> call,
                            Throwable t
                    ) {
                        handleNetworkError(t, callback);
                    }
                }
        );
    }

    private void handleMutationResponse(
            Response<Void> response,
            ApiCallback<Boolean> callback
    ) {
        if (response.isSuccessful()) {
            callback.onSuccess(true);
        } else {
            Log.e(TAG, "Request failed: HTTP " + response.code());

            callback.onError(
                    getHttpErrorMessage(response)
            );
        }
    }

    private void handleNetworkError(
            Throwable t,
            ApiCallback<?> callback
    ) {
        Log.e(TAG, "Network request failed", t);

        callback.onError(
                ApiErrorMapper.fromThrowable(t)
        );
    }

    private String getHttpErrorMessage(Response<?> response) {
        int code = response.code();

        if (code == 400) {
            String backendMessage =
                    ApiErrorMapper.backendMessage(response);

            return backendMessage != null
                    ? backendMessage
                    : "Invalid lesson data. Please check your input.";
        }

        if (code == 404) {
            return "Lesson or Learning Space not found.";
        }

        return ApiErrorMapper.fromResponse(
                response,
                "Request failed."
        );
    }

}

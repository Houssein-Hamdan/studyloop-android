package com.example.studyloopandroid.data.repository;

import android.content.Context;

import com.example.studyloopandroid.data.model.quiz.CreateQuizRequest;
import com.example.studyloopandroid.data.model.quiz.CreateQuizResponse;
import com.example.studyloopandroid.data.model.quiz.QuizDetailsResponse;
import com.example.studyloopandroid.data.model.quiz.QuizListResponse;
import com.example.studyloopandroid.data.model.quiz.SubmitQuizRequest;
import com.example.studyloopandroid.data.model.quiz.SubmitQuizResponse;
import com.example.studyloopandroid.data.remote.QuizApiService;
import com.example.studyloopandroid.data.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizRepository {

    private final QuizApiService apiService;

    public QuizRepository(Context context) {
        apiService = RetrofitClient.getInstance(context)
                .create(QuizApiService.class);
    }

    public void createQuiz(
            String containerId,
            String lessonId,
            CreateQuizRequest request,
            ApiCallback<CreateQuizResponse> callback
    ) {
        apiService.createQuiz(containerId, lessonId, request)
                .enqueue(new Callback<CreateQuizResponse>() {

                    @Override
                    public void onResponse(
                            Call<CreateQuizResponse> call,
                            Response<CreateQuizResponse> response
                    ) {
                        if (response.isSuccessful()
                                && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError(
                                    ApiErrorMapper.fromResponse(
                                            response,
                                            "Failed to create quiz."
                                    )
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<CreateQuizResponse> call,
                            Throwable t
                    ) {
                        callback.onError(
                                ApiErrorMapper.fromThrowable(t)
                        );
                    }
                });
    }

    public void getLessonQuizzes(
            String containerId,
            String lessonId,
            ApiCallback<QuizListResponse> callback
    ) {
        apiService.getLessonQuizzes(containerId, lessonId)
                .enqueue(new Callback<QuizListResponse>() {

                    @Override
                    public void onResponse(
                            Call<QuizListResponse> call,
                            Response<QuizListResponse> response
                    ) {
                        if (response.isSuccessful()
                                && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError(
                                    ApiErrorMapper.fromResponse(
                                            response,
                                            "Failed to load quizzes."
                                    )
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<QuizListResponse> call,
                            Throwable t
                    ) {
                        callback.onError(
                                ApiErrorMapper.fromThrowable(t)
                        );
                    }
                });
    }

    public void getQuiz(
            String containerId,
            String lessonId,
            String quizId,
            ApiCallback<QuizDetailsResponse> callback
    ) {
        apiService.getQuiz(containerId, lessonId, quizId)
                .enqueue(new Callback<QuizDetailsResponse>() {

                    @Override
                    public void onResponse(
                            Call<QuizDetailsResponse> call,
                            Response<QuizDetailsResponse> response
                    ) {
                        if (response.isSuccessful()
                                && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError(
                                    ApiErrorMapper.fromResponse(
                                            response,
                                            "Failed to load quiz."
                                    )
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<QuizDetailsResponse> call,
                            Throwable t
                    ) {
                        callback.onError(
                                ApiErrorMapper.fromThrowable(t)
                        );
                    }
                });
    }

    public void submitQuiz(
            String containerId,
            String lessonId,
            String quizId,
            SubmitQuizRequest request,
            ApiCallback<SubmitQuizResponse> callback
    ) {
        apiService.submitQuiz(
                containerId,
                lessonId,
                quizId,
                request
        ).enqueue(new Callback<SubmitQuizResponse>() {

            @Override
            public void onResponse(
                    Call<SubmitQuizResponse> call,
                    Response<SubmitQuizResponse> response
            ) {
                if (response.isSuccessful()
                        && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(
                            ApiErrorMapper.fromResponse(
                                    response,
                                    "Failed to submit quiz."
                            )
                    );
                }
            }

            @Override
            public void onFailure(
                    Call<SubmitQuizResponse> call,
                    Throwable t
            ) {
                callback.onError(
                        ApiErrorMapper.fromThrowable(t)
                );
            }
        });
    }
}

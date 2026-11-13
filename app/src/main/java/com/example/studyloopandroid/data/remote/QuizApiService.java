package com.example.studyloopandroid.data.remote;

import com.example.studyloopandroid.data.model.quiz.CreateQuizRequest;
import com.example.studyloopandroid.data.model.quiz.CreateQuizResponse;
import com.example.studyloopandroid.data.model.quiz.QuizDetailsResponse;
import com.example.studyloopandroid.data.model.quiz.QuizListResponse;
import com.example.studyloopandroid.data.model.quiz.SubmitQuizRequest;
import com.example.studyloopandroid.data.model.quiz.SubmitQuizResponse;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface QuizApiService {

    @POST("containers/{containerId}/lessons/{lessonId}/quizzes")
    Call<CreateQuizResponse> createQuiz(
            @Path("containerId") String containerId,
            @Path("lessonId") String lessonId,
            @Body CreateQuizRequest request
    );

    @GET("containers/{containerId}/lessons/{lessonId}/quizzes")
    Call<QuizListResponse> getLessonQuizzes(
            @Path("containerId") String containerId,
            @Path("lessonId") String lessonId
    );

    @GET("containers/{containerId}/lessons/{lessonId}/quizzes/{quizId}")
    Call<QuizDetailsResponse> getQuiz(
            @Path("containerId") String containerId,
            @Path("lessonId") String lessonId,
            @Path("quizId") String quizId
    );

    @POST("containers/{containerId}/lessons/{lessonId}/quizzes/{quizId}/submit")
    Call<SubmitQuizResponse> submitQuiz(
            @Path("containerId") String containerId,
            @Path("lessonId") String lessonId,
            @Path("quizId") String quizId,
            @Body SubmitQuizRequest request
    );
}
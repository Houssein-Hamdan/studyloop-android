package com.example.studyloopandroid.data.remote;

import com.example.studyloopandroid.data.model.lesson.LessonProgressResponse;
import com.example.studyloopandroid.data.model.lesson.UpdateTopicProgressRequest;
import com.example.studyloopandroid.data.model.lesson.CreateTopicRequest;
import com.example.studyloopandroid.data.model.lesson.LessonDetailsResponse;
import com.example.studyloopandroid.data.model.lesson.ProgressInitializationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;

import retrofit2.http.PATCH;
public interface LessonDetailsApi {

    @GET("containers/{containerId}/lessons/{lessonId}")
    Call<LessonDetailsResponse> getLessonDetails(
            @Path("containerId") String containerId,
            @Path("lessonId") String lessonId
    );

    @POST("containers/{containerId}/lessons/{lessonId}/topics")
    Call<Void> createTopic(
            @Path("containerId") String containerId,
            @Path("lessonId") String lessonId,
            @Body CreateTopicRequest request
    );

    @PUT("containers/{containerId}/lessons/{lessonId}/topics/{topicId}")
    Call<Void> updateTopic(
            @Path("containerId") String containerId,
            @Path("lessonId") String lessonId,
            @Path("topicId") String topicId,
            @Body CreateTopicRequest request
    );

    @DELETE("containers/{containerId}/lessons/{lessonId}/topics/{topicId}")
    Call<Void> deleteTopic(
            @Path("containerId") String containerId,
            @Path("lessonId") String lessonId,
            @Path("topicId") String topicId
    );

    @GET("containers/{containerId}/lessons/{lessonId}/progress/details")
    Call<LessonProgressResponse> getLessonProgress(
            @Path("containerId") String containerId,
            @Path("lessonId") String lessonId
    );

    @PATCH("containers/{containerId}/lessons/{lessonId}/progress/topics/{topicId}")
    Call<Void> updateTopicProgress(
            @Path("containerId") String containerId,
            @Path("lessonId") String lessonId,
            @Path("topicId") String topicId,
            @Body UpdateTopicProgressRequest request
    );

    @GET("containers/{containerId}/lessons/{lessonId}/progress")
    Call<ProgressInitializationResponse> getOrCreateProgress(
            @Path("containerId") String containerId,
            @Path("lessonId") String lessonId
    );

}

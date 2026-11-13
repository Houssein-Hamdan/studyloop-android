package com.example.studyloopandroid.data.remote;

import com.example.studyloopandroid.data.model.lesson.CreateLessonRequest;
import com.example.studyloopandroid.data.model.lesson.LessonsResponse;
import com.example.studyloopandroid.data.model.lesson.UpdateLessonRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface LessonsApi {

    // Get all lessons inside a container
    @GET("containers/{containerId}/lessons")
    Call<LessonsResponse> getLessons(
            @Path("containerId") String containerId
    );

    // Create a new lesson
    @POST("containers/{containerId}/lessons")
    Call<Void> createLesson(
            @Path("containerId") String containerId,
            @Body CreateLessonRequest request
    );

    // Update an existing lesson
    @PUT("containers/{containerId}/lessons/{id}")
    Call<Void> updateLesson(
            @Path("containerId") String containerId,
            @Path("id") String lessonId,
            @Body UpdateLessonRequest request
    );

    // Delete a lesson
    @DELETE("containers/{containerId}/lessons/{id}")
    Call<Void> deleteLesson(
            @Path("containerId") String containerId,
            @Path("id") String lessonId
    );

}

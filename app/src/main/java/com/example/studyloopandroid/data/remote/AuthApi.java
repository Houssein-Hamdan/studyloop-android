package com.example.studyloopandroid.data.remote;

import com.example.studyloopandroid.data.model.auth.AuthResponse;
import com.example.studyloopandroid.data.model.auth.LoginRequest;
import com.example.studyloopandroid.data.model.auth.RegisterRequest;
import com.example.studyloopandroid.data.model.auth.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AuthApi {

    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<AuthResponse> register(@Body RegisterRequest request);

    @GET("auth/me")
    Call<UserResponse> getCurrentUser();

}

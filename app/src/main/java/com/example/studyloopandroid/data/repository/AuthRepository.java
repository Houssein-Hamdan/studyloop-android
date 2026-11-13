package com.example.studyloopandroid.data.repository;

import com.example.studyloopandroid.data.model.auth.AuthResponse;
import com.example.studyloopandroid.data.model.auth.LoginRequest;
import com.example.studyloopandroid.data.model.auth.RegisterRequest;
import com.example.studyloopandroid.data.model.auth.UserResponse;
import com.example.studyloopandroid.data.remote.AuthApi;
import com.example.studyloopandroid.data.remote.RetrofitClient;
import android.content.Context;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final AuthApi authApi;

    public AuthRepository(Context context) {
        authApi = RetrofitClient
                .getInstance(context)
                .create(AuthApi.class);
    }

    public void login(
            LoginRequest request,
            ApiCallback<AuthResponse> callback
    ) {
        authApi.login(request).enqueue(
                new Callback<AuthResponse>() {

                    @Override
                    public void onResponse(
                            Call<AuthResponse> call,
                            Response<AuthResponse> response
                    ) {
                        if (response.isSuccessful()
                                && response.body() != null) {

                            callback.onSuccess(response.body());

                        } else if (response.code() == 401) {
                            callback.onError("Invalid email or password.");
                        } else {
                            callback.onError(
                                    ApiErrorMapper.fromResponse(
                                            response,
                                            "Login failed."
                                    )
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<AuthResponse> call,
                            Throwable t
                    ) {
                        callback.onError(
                                ApiErrorMapper.fromThrowable(t)
                        );
                    }
                }
        );
    }

    public void register(
            RegisterRequest request,
            ApiCallback<AuthResponse> callback
    ) {
        authApi.register(request).enqueue(
                new Callback<AuthResponse>() {

                    @Override
                    public void onResponse(
                            Call<AuthResponse> call,
                            Response<AuthResponse> response
                    ) {
                        if (response.isSuccessful()
                                && response.body() != null) {

                            callback.onSuccess(response.body());

                        } else {
                            callback.onError(
                                    ApiErrorMapper.fromResponse(
                                            response,
                                            "Registration failed."
                                    )
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<AuthResponse> call,
                            Throwable t
                    ) {
                        callback.onError(
                                ApiErrorMapper.fromThrowable(t)
                        );
                    }
                }
        );
    }

    public void getCurrentUser(ApiCallback<UserResponse> callback) {

        authApi.getCurrentUser().enqueue(new Callback<UserResponse>() {

            @Override
            public void onResponse(
                    Call<UserResponse> call,
                    Response<UserResponse> response
            ) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());

                } else if (response.code() == 401) {
                    callback.onError(ApiErrorMapper.SESSION_EXPIRED);

                } else {
                    callback.onError(
                            ApiErrorMapper.fromResponse(
                                    response,
                                    "Failed to load user."
                            )
                    );
                }
            }

            @Override
            public void onFailure(
                    Call<UserResponse> call,
                    Throwable t
            ) {
                callback.onError(ApiErrorMapper.fromThrowable(t));
            }
        });

    }
}
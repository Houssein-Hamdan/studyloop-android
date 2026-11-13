package com.example.studyloopandroid.data.model.auth;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {

    private String message;

    @SerializedName("access_token")
    private String accessToken;

    private UserResponse user;

    public String getMessage() {
        return message;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public UserResponse getUser() {
        return user;
    }

}

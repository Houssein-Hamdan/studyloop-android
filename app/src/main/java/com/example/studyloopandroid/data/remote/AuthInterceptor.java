package com.example.studyloopandroid.data.remote;

import android.content.Context;

import com.example.studyloopandroid.data.local.SessionExpirationHandler;
import com.example.studyloopandroid.data.local.TokenStorage;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final TokenStorage tokenStorage;
    private final Context applicationContext;

    public AuthInterceptor(
            TokenStorage tokenStorage,
            Context applicationContext
    ) {
        this.tokenStorage = tokenStorage;
        this.applicationContext = applicationContext;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request originalRequest = chain.request();

        String path = originalRequest.url().encodedPath();

        boolean requiresAuth = !path.equals("/auth/login")
                && !path.equals("/auth/register");

        if (!requiresAuth) {
            return chain.proceed(originalRequest);
        }

        try {
            String token = tokenStorage.getToken();

            if (token == null || token.trim().isEmpty()) {
                return chain.proceed(originalRequest);
            }

            Request authenticatedRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();

            Response response = chain.proceed(authenticatedRequest);

            // The startup session check handles /auth/me itself so it can
            // decide between login and dashboard. Every other endpoint uses
            // the shared session-expiration flow.
            if (response.code() == 401
                    && !path.equals("/auth/me")) {
                SessionExpirationHandler.handleExpiredSession(
                        applicationContext,
                        tokenStorage
                );
            }

            return response;

        } catch (Exception e) {
            throw new IOException("Unable to read authentication token.", e);
        }
    }
}

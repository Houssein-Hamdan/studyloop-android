package com.example.studyloopandroid.data.remote;

import android.content.Context;

import com.example.studyloopandroid.data.local.TokenStorage;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitClient {

    private static final String BASE_URL =
            "https://studyloop-hslk.onrender.com/";

    private static Retrofit retrofit;

    private RetrofitClient() {
    }

    public static synchronized Retrofit getInstance(Context context) {

        if (retrofit == null) {

            TokenStorage tokenStorage = new TokenStorage(
                    context.getApplicationContext()
            );

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(
                            tokenStorage,
                            context.getApplicationContext()
                    ))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

}

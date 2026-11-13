package com.example.studyloopandroid.data.repository;

import android.content.Context;

import com.example.studyloopandroid.data.model.ask.GeneralAskResponse;
import com.example.studyloopandroid.data.model.ask.GeneralQuestionRequest;
import com.example.studyloopandroid.data.remote.AskApi;
import com.example.studyloopandroid.data.remote.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AskRepository {

    private final AskApi askApi;

    public AskRepository(Context context) {
        askApi = RetrofitClient
                .getInstance(context)
                .create(AskApi.class);
    }

    public void askQuestion(
            String questionText,
            AskCallback callback
    ) {
        GeneralQuestionRequest request =
                new GeneralQuestionRequest(questionText);

        askApi.askGeneralQuestion(request)
                .enqueue(new Callback<GeneralAskResponse>() {

                    @Override
                    public void onResponse(
                            Call<GeneralAskResponse> call,
                            Response<GeneralAskResponse> response
                    ) {
                        if (response.isSuccessful()
                                && response.body() != null) {

                            callback.onSuccess(response.body().getAnswer());

                        } else {
                            callback.onError(
                                    ApiErrorMapper.fromResponse(
                                            response,
                                            "Failed to get an answer."
                                    )
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<GeneralAskResponse> call,
                            Throwable t
                    ) {
                        callback.onError(
                                ApiErrorMapper.fromThrowable(t)
                        );
                    }
                });
    }

    public interface AskCallback {
        void onSuccess(String answer);

        void onError(String error);
    }
}

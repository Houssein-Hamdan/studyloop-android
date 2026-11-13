package com.example.studyloopandroid.data.remote;

import com.example.studyloopandroid.data.model.ask.GeneralAskResponse;
import com.example.studyloopandroid.data.model.ask.GeneralQuestionRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AskApi {

    @POST("ask")
    Call<GeneralAskResponse> askGeneralQuestion(
            @Body GeneralQuestionRequest request
    );
}